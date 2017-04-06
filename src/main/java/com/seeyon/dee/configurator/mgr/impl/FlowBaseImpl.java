package com.seeyon.dee.configurator.mgr.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.dee.configurator.mgr.FlowBaseMgr;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.bean.JDBCResourceBean;
import com.seeyon.v3x.dee.common.base.util.DBUtil;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.ibm.icu.text.SimpleDateFormat;
import com.seeyon.dee.configurator.dao.*;
import com.seeyon.dee.configurator.dto.FlowParameter;

import dwz.framework.exception.SystemException;

@Service("flowBaseMgr")
public class FlowBaseImpl implements FlowBaseMgr {
	
	@Autowired
    @Qualifier("flowTypeDao")
	private FlowTypeDao FlowTypeDao;
	
	@Autowired
    @Qualifier("flowDao")
	private FlowDao FlowBeanDao;
	
	@Autowired
    @Qualifier("deeResourceDao")
	private DeeResourceDao DeeResourceDao;
	
	@Autowired
    @Qualifier("parameterDao")
	private ParameterDao ParameterDao;
	
	@Autowired
    @Qualifier("flowSubDao")
	private FlowSubDao FlowSubDao;
	
	@Autowired
    @Qualifier("downloadDao")
	private DownloadDao DownloadDao;
	
	@Override
	public Collection<FlowTypeBean> findAll() throws SystemException
	{
		return FlowTypeDao.findAll();
	}
	
	@Override
	public String save(FlowBean FlowBean) throws SystemException
	{
		String uuid = UuidUtil.uuid();
		FlowBean.setFLOW_ID(uuid);
		FlowBean.setFLOW_NAME(uuid);
		FlowBeanDao.save(FlowBean);
		return uuid;
	}
	
	public List<String> findAllDisName(String name) throws SystemException{
	    String sql="select dis_name from dee_flow where dis_name like '"+ name+ "%'";
	    List<String> disNameList =  new ArrayList<String>();
	    disNameList = FlowBeanDao.findAllDisName(sql);
	    return disNameList;
	}
	
	@Override
	public boolean checkName(String flowName) throws SystemException
	{
		String sql = "select count(*) from dee_flow where dis_name = ?";
		String[] params = {flowName};
		int count = FlowBeanDao.countBySQLQuery(sql, params);
		if(count<1)
		return true;
		else
		return false;
	}
	
	@Override
	public FlowBean getFlow(String uuid) throws SystemException
	{
		FlowBean flow = FlowBeanDao.get(uuid);
		return flow;
	}
	
	

	@Override
	public void update(FlowBean FlowBean) throws SystemException{
		// TODO Auto-generated method stub
		FlowBeanDao.saveOrUpdate(FlowBean);
	}

	@Override
	public  List<DeeResourceBean> findMappingById(String mapId){
		List<DeeResourceBean> drb = new ArrayList<DeeResourceBean>();
		try{
			String hql = "from DeeResourceBean dr where dr.resource_id=?";
			String[] params = {mapId};
			drb = DeeResourceDao.findByQuery(hql, params, -1, -1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return drb;
	}
	@Override
	public List<DeeResourceBean> findResourceList(String flowId) {
		// TODO Auto-generated method stub

		List<DeeResourceBean> drb = new ArrayList<DeeResourceBean>();
		try{
			String hql = "from DeeResourceBean dr where dr.flowSub.flow.FLOW_ID=?";
			String[] params = {flowId};		
	        drb = DeeResourceDao.findByQuery(hql, params, -1, -1);	        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return drb;
        	
	}

	@Override
	public void saveOrUpdateParams(List<FlowParameter> params,String uuid) throws SystemException {
		// TODO Auto-generated method stub
		List<ParameterBean> pbList = findParamsByFlowId(uuid);
		if(pbList!=null)
		{
			for(ParameterBean pb:pbList)
			ParameterDao.deleteObject(pb);
		}
		if(params!=null&&params.size()>0)
		{
			for(FlowParameter para:params)
			{
                if (para == null) {
                    continue;
                }
				String paraId = UuidUtil.uuid();
				ParameterBean pb = new ParameterBean();
				pb.setDIS_NAME(para.getDisName());
				pb.setPARA_NAME(para.getParaName());
				pb.setPARA_VALUE(para.getDefaultValue());
				pb.setPARA_ID(paraId);
				FlowBean fb = new FlowBean();
				fb.setFLOW_ID(uuid);
				pb.setFlowBean(fb);
				ParameterDao.save(pb);
			}
		}
	}

	public void copyAndSavePara(ParameterBean pb) throws SystemException{
	    ParameterDao.save(pb);
	}
	
	@Override
	public List<ParameterBean> findParamsByFlowId(String flowId)
			throws SystemException {
		// TODO Auto-generated method stub
		String hql = "from ParameterBean pb where pb.flowBean.FLOW_ID=?";
		String[] params = {flowId};
		return ParameterDao.findByQuery(hql, params, -1, -1);
	}

	@Override
	public void updateOrder(int start, int end, String flowId,
			String resId, String proName) throws SystemException {
		// TODO Auto-generated method stub
		if(start!=end)
		{
			if(proName.equals("sourceProcess"))
			{
				start=start+1000;
				end=end+1000; 
			}
			else if(proName.equals("exchangeProcess"))
			{
				start=start+2000;
				end=end+2000; 
			}
			else if(proName.equals("targetProcess"))
			{
				start=start+3000;
				end=end+3000; 
			}
		
		
			String hql="from FlowSubBean fsb where fsb.flow.FLOW_ID = ? and fsb.sort >= ? and fsb.sort <= ?";
			Object[] params = new Object[3];
			params[0] = flowId;
			params[1] = start < end?start:end;
			params[2] = start < end?end:start;
			List<FlowSubBean> fsbList = new ArrayList<FlowSubBean>();
			fsbList = FlowSubDao.findByQuery(hql, params, -1, -1);
			
			for(FlowSubBean fsb:fsbList)
			{
				if(start<end)
				{
					if(fsb.getSort()==start)
					{
						fsb.setSort(end);
					}
					else
					{
						int sort = fsb.getSort();
						fsb.setSort(sort-1);
					}
				}
				else
				{
					if(fsb.getSort()==start)
					{
						fsb.setSort(end);
					}
					else
					{
						int sort = fsb.getSort();
						fsb.setSort(sort+1);
					}
				}
				
				FlowSubDao.update(fsb);
			}
		
		}
	}

	@Override
	public void deleteRes(String flowId, String resId) throws SystemException {
		// TODO Auto-generated method stub
		String hql="from FlowSubBean fsb where fsb.flow.FLOW_ID = ? and fsb.deeResource.resource_id = ?";
		String[] params = new String[2];
		params[0] = flowId;
		params[1] = resId;
		List<FlowSubBean> fsbList = new ArrayList<FlowSubBean>();
		List<DeeResourceBean> drbList = new ArrayList<DeeResourceBean>();
		fsbList = FlowSubDao.findByQuery(hql, params, -1, -1);
        FlowBean fb = FlowBeanDao.get(flowId);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        fb.setEXT4(str);
        FlowBeanDao.update(fb);
		for(FlowSubBean fsb:fsbList)
		{
			int sort = fsb.getSort();
			if(sort>=1000 && sort<2000)
			{
				String hqlOne = "from FlowSubBean fsb where fsb.flow.FLOW_ID = ? and fsb.sort > ? and fsb.sort <2000";
				Object[] paramsOne = new Object[2];
				paramsOne[0] = flowId;
				paramsOne[1] = sort;
				this.changeOrder(hqlOne, paramsOne);

			}
			else if(sort>=2000 && sort<3000)
			{
				String hqlTwo = "from FlowSubBean fsb where fsb.flow.FLOW_ID = ? and fsb.sort > ? and fsb.sort <3000";
				Object[] paramsTwo = new Object[2];
				paramsTwo[0] = flowId;
				paramsTwo[1] = sort;
				this.changeOrder(hqlTwo, paramsTwo);
			}
			else if(sort>=3000)
			{
				String hqlThree = "from FlowSubBean fsb where fsb.flow.FLOW_ID = ? and fsb.sort > ? ";
				Object[] paramsThree = new Object[2];
				paramsThree[0] = flowId;
				paramsThree[1] = sort;
				this.changeOrder(hqlThree, paramsThree);
			}
			drbList.add(fsb.getDeeResource());
			FlowSubDao.deleteObject(fsb);
		}
		for(DeeResourceBean drb:drbList)
		{
			String hqlFour = "from DownloadBean dlb where dlb.refResource.resource_id=?";
			String[] paramsFour = {drb.getResource_id()};
			List<DownloadBean> dlbList = DownloadDao.listAll(hqlFour, paramsFour);			
			for(DownloadBean dlb:dlbList)
			{
				DownloadDao.deleteObject(dlb);
			}
			DeeResourceDao.deleteObject(drb);
		}
	}
	
	public void changeOrder(String hql,Object[] params) throws SystemException
	{
		List<FlowSubBean> fsbList = new ArrayList<FlowSubBean>();
		fsbList = FlowSubDao.findByQuery(hql, params, -1, -1);
		for(FlowSubBean fsb:fsbList)
		{
			int sort = fsb.getSort();
			fsb.setSort(sort-1);					
			FlowSubDao.update(fsb);
		}
	}

	@Override
	public FlowTypeBean getFlowTypeBean(String flowTypeId)
			throws SystemException {
		FlowTypeBean ftb = FlowTypeDao.get(flowTypeId);
		return ftb;
	}

	@Override
	public String checkProcessOnly(String[] idsArray) throws SystemException {
		for(String ids:idsArray)
		{
			DeeResourceBean drb = DeeResourceDao.get(ids);
			if(drb.getDeeResourceTemplate().getResource_template_id().equals("16")||drb.getDeeResourceTemplate().getResource_template_id().equals("19"))
				continue;
			else
				return "true";
		}
		return "false";
	}

    @Override
    public JDBCResourceBean getResourceById(String inid) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }
	
}
