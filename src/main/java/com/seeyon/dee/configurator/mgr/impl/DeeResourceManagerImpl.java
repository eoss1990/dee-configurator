package com.seeyon.dee.configurator.mgr.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.seeyon.v3x.dee.bean.JDBCDictBean;
import com.seeyon.v3x.dee.common.a8version.A8VersionManager;
import com.seeyon.v3x.dee.resource.DbDataSource;

import dwz.framework.util.DEEClientUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.v3x.dee.bean.A8MetaDatasourceBean;
import com.seeyon.v3x.dee.bean.JDBCResourceBean;
import com.seeyon.v3x.dee.bean.JNDIResourceBean;
import com.seeyon.dee.configurator.dao.DeeResourceDao;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.DBUtil;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.datasource.JDBCDataSource;
import com.seeyon.v3x.dee.datasource.JNDIDataSource;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.mgr.impl.BaseMgrImpl;
import dwz.framework.exception.SystemException;

@Service("deeResourceManager")
public class DeeResourceManagerImpl extends BaseMgrImpl<DeeResourceBean,String>
		implements DeeResourceManager {

	private DeeResourceDao deeResourceDao;
    
    
    @Autowired
    @Qualifier("deeResourceDao")
    public void setBaseDao(BaseDao<DeeResourceBean, String> baseDao) {
        super.baseDao = baseDao;
        this.deeResourceDao = (DeeResourceDao)baseDao;
    }
	/*****************数据源**************************/
	/** 
	 * 根据条件查询数据源列表
	 */
	@Override
	public List<DeeResourceBean> findAllDs(String[] params,int startIndex, int count,
			String orderField) throws SystemException {
		// TODO Auto-generated method stub
		List<DeeResourceBean> drList = deeResourceDao.QueryAllDs(params,startIndex, count, orderField);
		return drList;
	}
	/** 
	 * 根据条件查询数据源条数
	 * @throws SystemException 
	 */
	@Override
	public int dsCount(String[] params) throws SystemException {
		// TODO Auto-generated method stub
		return deeResourceDao.dsCount(params);
	}
	
	/** 
	 * 根据主键获取数据源
	 * @throws SystemException 
	 */
	@Override
	public DeeResourceBean getDrByDrId(String drId) throws SystemException {
		// TODO Auto-generated method stub
		DeeResourceBean drb = deeResourceDao.get(drId);
		drb.setResource_template_id(drb.getDeeResourceTemplate().getResource_template_id());
		return drb;
	}
	
	/** 
	 * 删除数据源
	 * @throws SystemException 
	 */
	@Override
	public int deleteByDrId(String drId) throws SystemException {
		// TODO Auto-generated method stub
		deeResourceDao.delete(drId);
		return 0;
	}
	
	/** 
	 * 判断是否有相同名称
	 * @throws SystemException 
	 */
	@Override
	public Boolean isHasSameDisName(String drId,String disName) throws SystemException {
		// TODO Auto-generated method stub
		String sHql = "";
		int dCount = 0;
		if(drId == null || "".equals(drId)){
			//查询全部数据源名称（新增）
			sHql = "select count(*) from DeeResourceBean as obj where obj.deeResourceTemplate.resourceType.type_id='3' and obj.dis_name=?";
			String[] params = {disName};
			dCount = deeResourceDao.countByQuery(sHql, params);
			return dCount==0?false:true;
		}
		//查询除本条外的数据源名称（修改）
		sHql = "select count(*) from DeeResourceBean as obj where obj.deeResourceTemplate.resourceType.type_id='3' and obj.resource_id<>? and obj.dis_name=?";
		String[] params = {drId,disName};
		dCount = deeResourceDao.countByQuery(sHql, params);
		return dCount==0?false:true;
	}
	
	/** 
	 * 判断是否有是否有被引用
	 * true:返回被引用数据源名称；false：返回""空串
	 * @throws SystemException 
	 */
	@Override
	public String isQuoteByFlow(String drId) throws SystemException {
		// TODO Auto-generated method stub
		String sHql = "select count(*) from DeeResourceBean as obj where obj.ref_id=?";
		String[] params = {drId};
		int dCount = deeResourceDao.countByQuery(sHql, params);
		if(dCount != 0){
			DeeResourceBean drb = deeResourceDao.get(drId);
			if(drb != null)
				return drb.getDis_name();
		}
		return "";
	}
	
	/** 
	 * 保存数据源
	 * @throws SystemException 
	 */
	@Override
	public void saveDatasource(DeeResourceBean drb) throws SystemException {
		// TODO Auto-generated method stub
		if(drb.getResource_id() == null || "".equals(drb.getResource_id())){
			String uuid = UuidUtil.uuid();
			drb.setResource_id(uuid);
			if(drb.getResource_name() == null || "".equals(drb.getResource_name()))
				drb.setResource_name(uuid);
		}

		drb.setResource_code(drb.getDr().toXML(drb.getResource_id()));
		deeResourceDao.save(drb);
	}
	
	/** 
	 * 修改数据源
	 * @throws SystemException 
	 */
	@Override
	public void modifyDatasource(DeeResourceBean drb) throws SystemException {
		// TODO Auto-generated method stub
		drb.setResource_code(drb.getDr().toXML(drb.getResource_id()));
		deeResourceDao.update(drb);
	}
	
	/** 
	 * 测试连接
	 * @throws Exception 
	 */
	@Override
	public Boolean testConnect(DeeResourceBean drb) throws SystemException {
		// TODO Auto-generated method stub
		A8MetaDatasourceBean amBean = null;
		if(drb.getDr() instanceof JDBCResourceBean){
			amBean = new A8MetaDatasourceBean();
			amBean.setDriver(((JDBCResourceBean)drb.getDr()).getDriver());
			amBean.setUrl(((JDBCResourceBean)drb.getDr()).getUrl());
			amBean.setUser(((JDBCResourceBean)drb.getDr()).getUser());
			amBean.setPassword(((JDBCResourceBean)drb.getDr()).getPassword());
			return testTmpCon(amBean);
		}
		else if(drb.getDr() instanceof JNDIResourceBean){
			amBean = new A8MetaDatasourceBean();
			amBean.setJndi(((JNDIResourceBean)drb.getDr()).getJndi());
			return testTmpCon(amBean);
		}
		else if(drb.getDr() instanceof A8MetaDatasourceBean){
			return testTmpCon((A8MetaDatasourceBean) drb.getDr());
		}
		return true;
	}
	private boolean testTmpCon(A8MetaDatasourceBean dsBean) throws SystemException {
        Connection con = null;
        JDBCDataSource ds = null;
        try {
        	if(dsBean.getDriver() != null && !"".equals(dsBean.getDriver())){
        		try{
        			ds = new JDBCDataSource(dsBean.getDriver(),dsBean.getUrl(),dsBean.getUser(), dsBean.getPassword());
            		con = ds.getConnection();
//            		Class.forName(dsBean.getDriver());
//            		DriverManager.setLoginTimeout(5);
//            		con = DriverManager.getConnection(dsBean.getUrl(), dsBean.getUser(), dsBean.getPassword());
        		}catch(SQLException e){
                	throw new SystemException(e);
        		}
        	}
        	else if(dsBean.getJndi() != null && !"".equals(dsBean.getJndi())){
        		JNDIDataSource jds = new JNDIDataSource();
        		jds.setJndi(dsBean.getJndi());
        		try {
					con = jds.getConnection();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
		        	throw new SystemException(e);
				}
        	}
            if(con != null)
                return true;
        } catch(Exception e) {
        	throw new SystemException(e);
        }
        finally {
            if(con != null) {
                try {
                    con.close();
                } catch(SQLException e) {
                	throw  new SystemException(e);
                }
            } if (ds != null) {
				ds.close();
			}
        }
        return false;
	}
	@Override
	public Boolean isA8Meta(DeeResourceBean drb) throws Exception {
		DbDataSource ds = null;
        Connection con = null;
        A8MetaDatasourceBean dsBean = null;

        if (drb.getDr() instanceof A8MetaDatasourceBean) {
            dsBean = (A8MetaDatasourceBean) drb.getDr();
            
            if (StringUtils.isNotBlank(dsBean.getDriver())) {
                // JDBC数据源
//                ds = DEEClientUtil.lookupDbDataSource(drb.getResource_name());
//                con = ds.getConnection();
        		Class.forName(dsBean.getDriver());
        		DriverManager.setLoginTimeout(5);
        		con = DriverManager.getConnection(dsBean.getUrl(), dsBean.getUser(), dsBean.getPassword());
            } else if (StringUtils.isNotBlank(dsBean.getJndi())) {
                // JNDI数据源
                JNDIDataSource jds = new JNDIDataSource();
                jds.setJndi(dsBean.getJndi());
                con = jds.getConnection();
            }
        }

        String version = DBUtil.getA8VersionStr(con);
        return A8VersionManager.getInstance().exchange(version) != null;
	}
	
	
	@Override
	public List<DeeResourceBean> findAllJdbcDs() throws SystemException {
		// TODO Auto-generated method stub
		return deeResourceDao.QueryAllJdbcDs();
	}
	/*****************字典**************************/
	@Override
	public List<DeeResourceBean> findAllDict(String keyWords, int startIndex,
			int count, String orderField)  throws SystemException {
		// TODO Auto-generated method stub
		String[] params = {keyWords==null?"":keyWords};
		List<DeeResourceBean> drList = deeResourceDao.QueryAllDict(params, startIndex, count, orderField);
		return drList;
	}
	@Override
	public int dictCount(String keyWords) throws SystemException {
		// TODO Auto-generated method stub
		String[] params = {keyWords==null?"":keyWords};
		return deeResourceDao.dictCount(params);
	}
	/** 
	 * 判断是否有相同名称
	 * @throws SystemException 
	 */
	@Override
	public Boolean isHasSameDisNameOnDict(String drId,String disName) throws SystemException {
		// TODO Auto-generated method stub
		String sHql = "";
		int dCount = 0;
		if(drId == null || "".equals(drId)){
			//查询全部字典名称（新增）
			sHql = "select count(*) from DeeResourceBean as obj where obj.deeResourceTemplate.resource_template_id in('12','13','29') and obj.dis_name=?";
			String[] params = {disName};
			dCount = deeResourceDao.countByQuery(sHql, params);
			return dCount==0?false:true;
		}
		//查询除本条外的字典名称（修改）
		sHql = "select count(*) from DeeResourceBean as obj where obj.deeResourceTemplate.resource_template_id in('12','13','29') and obj.resource_id<>? and obj.dis_name=?";
		String[] params = {drId,disName};
		dCount = deeResourceDao.countByQuery(sHql, params);
		return dCount==0?false:true;
	}
	@Override
	public List<KeyValue> findAllDictDs() throws SystemException {
		// TODO Auto-generated method stub
		String sHql = "from DeeResourceBean as obj where obj.deeResourceTemplate.resource_template_id in('12','13','29') order by obj.deeResourceTemplate.resource_template_id";
		String[] params = {};
		List<DeeResourceBean> oldDictList = deeResourceDao.listAll(sHql, params);
		List<KeyValue> dictList = new ArrayList<KeyValue>();
		KeyValue dictK = null;
		for(DeeResourceBean dict:oldDictList){
			dictK = new KeyValue();
			if(Integer.parseInt(dict.getDeeResourceTemplate().getResource_template_id()) 
					== DeeResourceEnum.JDBCDictionary.ordinal()){
				dictK.setKey("dict('"+dict.getResource_id()+"')");
				dictK.setValue("JDBC字典-" + dict.getDis_name());
			}
			else if(Integer.parseInt(dict.getDeeResourceTemplate().getResource_template_id()) 
					== DeeResourceEnum.StaticDictionary.ordinal()){
				dictK.setKey("dict('"+dict.getDis_name()+"')");
				dictK.setValue("枚举字典-" + dict.getDis_name());
			}
			else{
				if(dict.getResource_code() == null || "".equals(dict.getResource_code()))
					dictK.setKey("uuid()");
				else
					dictK.setKey(dict.getFunc_name()+"('"+dict.getResource_code()+"')");
				dictK.setValue("系统函数-" + dict.getDis_name());
			}
			dictList.add(dictK);
		}
		return dictList;
	}

}
