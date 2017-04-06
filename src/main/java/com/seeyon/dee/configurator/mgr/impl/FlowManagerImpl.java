package com.seeyon.dee.configurator.mgr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.dee.configurator.dao.DeeResourceDao;
import com.seeyon.dee.configurator.dao.DownloadDao;
import com.seeyon.dee.configurator.dao.FlowDao;
import com.seeyon.dee.configurator.dao.FlowSubDao;
import com.seeyon.dee.configurator.dao.FlowTypeDao;
import com.seeyon.dee.configurator.dao.ParameterDao;
import com.seeyon.dee.configurator.dao.RedoDao;
import com.seeyon.dee.configurator.dao.ScheduleDao;
import com.seeyon.dee.configurator.dao.SyncDao;
import com.seeyon.dee.configurator.mgr.FlowManagerMgr;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;
import com.seeyon.v3x.dee.context.AdapterKeyName;
import com.seeyon.v3x.dee.util.FileUtil;

import dwz.framework.exception.SystemException;
import dwz.framework.util.DeeList;


@Service("flowManager")
public class FlowManagerImpl implements FlowManagerMgr {
	
	@Autowired
    @Qualifier("flowDao")
	private FlowDao FlowBeanDao;
	
	@Autowired
    @Qualifier("downloadDao")
	private DownloadDao DownloadDao; 
	
	@Autowired
    @Qualifier("flowSubDao")
	private FlowSubDao FlowSubDao; 
	
	@Autowired
    @Qualifier("parameterDao")
	private ParameterDao ParameterDao; 
	
	@Autowired
    @Qualifier("scheduleDao")
	private ScheduleDao ScheduleDao; 
	
	@Autowired
    @Qualifier("deeResourceDao")
	private DeeResourceDao DeeResourceBeanDao;
	
	@Autowired
    @Qualifier("flowTypeDao")
    private FlowTypeDao flowTypeDao;
	
	@Autowired
    @Qualifier("syncDao")
    private SyncDao syncDao;
	
	@Autowired
    @Qualifier("redoDao")
    private RedoDao redoDao;

	@Override
	public void addFlowBean(FlowBean FB) throws SystemException{
		// TODO Auto-generated method stub
		FlowBeanDao.save(FB);
	}

	@Override
	public Collection<FlowBean> searchFlowBean(String orderField,
			int startIndex, int count, String keyWords) throws SystemException{
		// TODO Auto-generated method stub
		String hql = null;
		ArrayList<FlowBean> eaList = new ArrayList<FlowBean>();
		DeeList flowBeanList = new DeeList();
		if (keyWords != null && !keyWords.equals(""))
		{
			hql = flowBeanList.createQueryString("FlowBean", false, orderField, "DIS_NAME", keyWords,"CREATE_TIME");
		}
		else
		{		
			hql = flowBeanList.createQueryString("FlowBean", false, orderField,"CREATE_TIME");
		}
		
//		String hql = "select a from FlowBean as a order by a.CREATE_TIME ";
		Collection<FlowBean> FlowBean = FlowBeanDao.findByQuery(hql, startIndex, count);
		for(FlowBean po: FlowBean)
		{
			eaList.add(po);
		}
		
		return eaList;
	}
	
	@Override
    public Collection<FlowBean> searchFlowBeanByTypeId(String orderField, int startIndex, int numPerPage, String flowTypeId)
            throws SystemException {
	    String typeId = flowTypeId;
	    String hql = null;
        FlowTypeBean flowTypeBean = flowTypeDao.get(typeId);
        
        if (flowTypeBean != null && "-1".equals(flowTypeBean.getPARENT_ID())) {
            typeId = null;
        }
        
        if (typeId != null && !"".equals(typeId)) {
            hql = DeeList.createQueryStr("FlowBean", false, orderField, "flowType.FLOW_TYPE_ID", typeId,"CREATE_TIME");
        } else {
            DeeList deeList = new DeeList();
            hql = deeList.createQueryString("FlowBean", false, orderField,"CREATE_TIME");
        }
        
        return FlowBeanDao.findByQuery(hql, startIndex, numPerPage);
    }

	@Override
	public Integer searchFlowBeanNum(String className,String fieldName, String fieldValue) throws SystemException{
		// TODO Auto-generated method stub
		DeeList flowBeanList = new DeeList();
		String hql = flowBeanList.createQueryString(className, true, null, fieldName, fieldValue, null);
		Number totalCount = this.FlowBeanDao.countByQuery(hql);
		return totalCount.intValue();
	}
	
	@Override
	public FlowBean getFlowBean(String uuid) throws SystemException{
		// TODO Auto-generated method stub
		FlowBean flowBean = this.FlowBeanDao.get(uuid);
		return flowBean;
	}

	@Override
	public void updateFlowBean(FlowBean flowBean) throws SystemException{
		// TODO Auto-generated method stub
		this.FlowBeanDao.update(flowBean);
	}

	@Override
	public void deleteFlowBean(FlowBean flowBean) throws SystemException{
		// TODO Auto-generated method stub
		this.FlowBeanDao.deleteObject(flowBean);
	}

	@Override
	public void delete(String id) throws SystemException{
		// TODO Auto-generated method stub

        	AdapterKeyName adapterKeyName = AdapterKeyName.getInstance();
			FlowBean flBean = FlowBeanDao.get(id);
			if(flBean != null)
			{
				String sql = "select resource_id from dee_flow_sub where flow_id = ?";
				String[] param = {id};
				Collection resourceId = FlowSubDao.findBySQLQuery(sql, param, -1, -1);
				if(resourceId.size()>0)
				{
					Object[] resIdArray = null;
					sql="select download_id from dee_download where resource_id = ?";
					for(int i=0; i<resourceId.size(); i++){
						  resIdArray = resourceId.toArray();
						  String resId = (String)resIdArray[i];
						  String[] para = {resId};
						  Collection downloadId = DownloadDao.findBySQLQuery(sql, para, -1, -1);
						  if(downloadId.size()>0)
						  {
							  for(int j=0;j<downloadId.size();j++)
							  {
								  Object[] obj = downloadId.toArray();
								  String downId = (String)obj[j];
								  DownloadBean dlBean = DownloadDao.get(downId);
								  if(dlBean != null)
									  DownloadDao.deleteObject(dlBean);
							  }
						  }
	
						}
					
					sql="select flow_sub_id from dee_flow_sub where flow_id = ?";
					Collection flowSubId = FlowSubDao.findBySQLQuery(sql, param, -1, -1);
					if(flowSubId.size()>0)
					{
						for(int i=0; i<flowSubId.size(); i++){
							 Object[] subIdArray = flowSubId.toArray();
							 String subId = (String)subIdArray[i];
							 FlowSubBean fsBean = FlowSubDao.get(subId);
							 if(fsBean != null)
								 FlowSubDao.deleteObject(fsBean);
						}
					}
					
					for(int i=0;i<resIdArray.length;i++)
					{
						DeeResourceBean drBean = DeeResourceBeanDao.get((String)resIdArray[i]);
						if(drBean != null)
							DeeResourceBeanDao.deleteObject(drBean);
					}
					
					
				}
				
//				sql="select flow_sub_id from dee_flow_sub where flow_id = ?";
//				Collection flowSubId = FlowSubDao.findBySQLQuery(sql, param, -1, -1);
//				if(flowSubId.size()>0)
//				{
//					for(int i=0; i<flowSubId.size(); i++){
//						 Object[] subIdArray = flowSubId.toArray();
//						 String subId = (String)subIdArray[i];
//						 FlowSubBean fsBean = FlowSubDao.get(subId);
//						 if(fsBean != null)
//							 FlowSubDao.deleteObject(fsBean);
//					}
//				}
				
				sql="select para_id from dee_flow_parameter where flow_id = ?";
				Collection paraId = ParameterDao.findBySQLQuery(sql, param, -1, -1);
				if(paraId.size()>0)
				{
					for(int i=0; i<paraId.size(); i++){
						 Object[] paraIdArray = paraId.toArray();
						 String para = (String)paraIdArray[i];
						 ParameterBean pmBean = ParameterDao.get(para);
						 if(pmBean != null)
							 ParameterDao.deleteObject(pmBean);
					}
				}
				
				sql="select schedule_id from dee_schedule where flow_id = ?";
				Collection scheId = ParameterDao.findBySQLQuery(sql, param, -1, -1);
				if(scheId.size()>0)
				{
					for(int i=0; i<scheId.size(); i++){
						 Object[] scheIdArray = scheId.toArray();
						 String sche = (String)scheIdArray[i];
						 ScheduleBean sdBean = ScheduleDao.get(sche);
						 if(sdBean != null)
							 ScheduleDao.deleteObject(sdBean);
					}
				}
				
				sql="select redo_id from dee_redo where flow_id = ?";
				Collection redoId = redoDao.findBySQLQuery(sql, param, -1, -1);
				if(redoId.size()>0)
				{
					for(int i=0; i<redoId.size(); i++){
						 Object[] redoIdArray = redoId.toArray();
						 String redo = (String)redoIdArray[i];
						 RedoBean rdBean = redoDao.get(redo);
						 if(rdBean != null)
							 redoDao.deleteObject(rdBean);
					}
				}
				
				sql="select sync_id from dee_sync_history where flow_id = ?";
				Collection syncId = syncDao.findBySQLQuery(sql, param, -1, -1);
				if(syncId.size()>0)
				{
			    	String path = "";
			    	String fid = id;
					for(int i=0; i<syncId.size(); i++){
						 Object[] syncIdArray = syncId.toArray();
						 String sync = (String)syncIdArray[i];
						 SyncBean syncBean = syncDao.get(sync);
						 if(syncBean != null){
							 	syncDao.deleteObject(syncBean);
							 	path = adapterKeyName.getDeeHome() + "flowLogs" + File.separator + adapterKeyName.getFlowMap().get(fid) + "_" + fid + File.separator;
					            path = path + sync + ".properties";
					            File file = new File(path);
					            if (file.exists()){
					            	file.delete();
					            }
						 }
					}
				}
				
				FlowBeanDao.deleteObject(flBean);

			}
	}

	@Override
	public List<FlowBean> findAll() throws SystemException{
		// TODO Auto-generated method stub
		return FlowBeanDao.findAll();
		
	}
}
