package com.seeyon.dee.configurator.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.bean.A8MetaDatasourceBean;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.JDBCResourceBean;
import com.seeyon.v3x.dee.bean.JNDIResourceBean;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.datasource.DeePooledDataSource;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;


public class DeeResourceAction extends BaseAction {

	/**
	 * 数据源管理业务
	 */
	private static final long serialVersionUID = -4209553945185879205L;
	private final static Log log = LogFactory.getLog(DeeResourceAction.class);

    @Autowired
    @Qualifier("deeResourceManager")
	private DeeResourceManager deeResourceManager;
    
	private String navTabId;	
	private String callbackType;	
	private String uid;
	private String ids;
	
	private String dis_name;
	private String resource_id;
	private String driver;
	private String url;
	private String username;
	private String password;
	private String jndi;
	private String dsType;
	private String a8metajdbc;
	private String a8metajndi;
	private String dsDrv;
	
	private DeePooledDataSource dpds;
	
	
	/** 
	 * 获取数据源列表
	 */
	public String dslist(){
		String keyWd = getKeywords();
		int pageNum = this.getPageNum() > 0 ? this.getPageNum() - 1 : 0;
		int startIndex = pageNum * getNumPerPage();
		try {
			if(keyWd == null)
				keyWd = "";
			String[] params = {"3",keyWd};
			if(getOrderDirection() == null){
				setOrderDirection("desc");
				setOrderField("create_time");
			}
			List<DeeResourceBean> drbList = deeResourceManager.findAllDs(params,startIndex, getNumPerPage(), realOrderField());
            for (DeeResourceBean bean : drbList) {
                DeeResource deeResourceSubBean = new ConvertDeeResourceBean(bean).getResource();
                bean.setDr(deeResourceSubBean);
            }
			ActionContext.getContext().put("dsList", drbList);
			String[] params1 = {"3",keyWd};
			this.setTotalCount(deeResourceManager.dsCount(params1));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取数据源列表异常："+e.getLocalizedMessage());
		}
		return INPUT;
	}
	
	/** 
	 * 展示数据源编辑页面
	 */
	public String dsshow(){
		//新增
		if(uid == null || "".equals(uid)){
			JDBCResourceBean rb = new JDBCResourceBean();
			//设置连接池信息
			DeePooledDataSource tmpDs = new DeePooledDataSource("");
			rb.setDeePooledDS(tmpDs);
			ActionContext.getContext().put("jdbcDr", rb);
			return INPUT;
		}
		//编辑
		DeeResourceBean dr = null;
		String metaFlag = "no";
		String dsType = "jdbc";
		try {
			dr = deeResourceManager.getDrByDrId(uid);
			if(dr == null){
				return ajaxForwardError("未获取到数据源信息！");
			}
			DeeResource rb = new ConvertDeeResourceBean(dr).getResource();
			if(rb instanceof JDBCResourceBean){
				ActionContext.getContext().put("jdbcDr", rb);
				dsType = "jdbc";
			}
			else if(rb instanceof JNDIResourceBean){
				ActionContext.getContext().put("jndiDr", rb);
				dsType = "jndi";
			}
			else{
				A8MetaDatasourceBean ab = (A8MetaDatasourceBean) rb;
				metaFlag = "yes";
				if(ab != null && "".equals(ab.getJndi())){
					//属于JDBC元数据
					ActionContext.getContext().put("jdbcDr", rb);
					dsType = "jdbc";
				}
				else{
					//属于JNDI元数据
					ActionContext.getContext().put("jndiDr", rb);
					dsType = "jndi";
				}
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("查询数据源异常："+e.getLocalizedMessage());
		}
		ActionContext.getContext().put("dstype", dsType);
		ActionContext.getContext().put("ismeta", metaFlag);
		ActionContext.getContext().put("bean", dr);
		return INPUT;
	}
	
	/** 
	 * 数据源保存
	 */
	public String saveDatasource(){
		DeeResourceBean drb = new DeeResourceBean();
		DEEClient client = new DEEClient();
		try{
			setDRB(drb);
			String retMsg = chkVals();
			if(!"".equals(retMsg)){
				//数据校验
				return ajaxForwardError(retMsg);
			}
			setRB(Integer.parseInt(drb.getDeeResourceTemplate().getResource_template_id()),drb);
			String drId = drb.getResource_id();
			if(drId != null && !"".equals(drId)){
				//修改数据源
				if(deeResourceManager.isHasSameDisName(drId, drb.getDis_name())){
					return ajaxForwardError("该数据源名称已经存在，请更换！");
				}
				deeResourceManager.modifyDatasource(drb);
				GenerationCfgUtil.getInstance().generationMainFile(
						GenerationCfgUtil.getDEEHome());
				client.refreshContext();
				return ajaxForwardSuccess(getText("msg.operation.success"));
			}
			if(deeResourceManager.isHasSameDisName(drId, drb.getDis_name())){
				return ajaxForwardError("该数据源名称已经存在，请更换！");
			}
			//新增数据源
			deeResourceManager.saveDatasource(drb);
			GenerationCfgUtil.getInstance().generationMainFile(
					GenerationCfgUtil.getDEEHome());
			client.refreshContext();
			return ajaxForwardSuccess(getText("msg.operation.success"));
		}
//		catch(TransformException e){
//			String retMsg = "保存数据源出错：" + e.getMessage();
//			return ajaxForwardError(retMsg);
//		}
		catch(Throwable e){
			String retMsg = "保存数据源出错：引擎刷新上下文异常--" + e.getMessage();
			log.error(e.getMessage(),e);
			return ajaxForwardError(retMsg);
		}
	}
	/** 
	 * 填写DeeResourceBean数据信息
	 */
	private void setDRB(DeeResourceBean drb){
		if(drb != null){
			drb.setResource_id(resource_id);
			drb.setResource_name(resource_id);
			DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
			if("jdbc".equals(dsType)){
				if("yes".equals(a8metajdbc))
					tmpBean.setResource_template_id(DeeResourceEnum.A8MetaDatasource.ordinal() + "");
				else	
					tmpBean.setResource_template_id(DeeResourceEnum.JDBCDATASOURCE.ordinal()+"");
			}
			else{
				if("yes".equals(a8metajndi))
					tmpBean.setResource_template_id(DeeResourceEnum.A8MetaDatasource.ordinal() + "");
				else	
					tmpBean.setResource_template_id(DeeResourceEnum.JNDIDataSource.ordinal()+"");
			}
			drb.setDeeResourceTemplate(tmpBean);
			drb.setDis_name(dis_name);
			drb.setResource_desc("");
			drb.setCreate_time(DateUtil.getSysTime());
		}
	}
	/** 
	 * 填写DeeResource数据信息
	 */
	private void setRB(int tmpId,DeeResourceBean drb){
		if(tmpId == DeeResourceEnum.JDBCDATASOURCE.ordinal()){
			JDBCResourceBean rb = new JDBCResourceBean();
			rb.setDriver(driver);
			rb.setUrl(url);
			rb.setUser(username);
			rb.setPassword(password);
			rb.setResource_name(drb.getResource_name());
			rb.setResoutce_desc("");
			//设置连接池信息
			DeePooledDataSource tmpDs = new DeePooledDataSource("");
			if(dpds != null){
				tmpDs.setMaxPoolSize(dpds.getMaxPoolSize());
				tmpDs.setMinPoolSize(dpds.getMinPoolSize());
				tmpDs.setInitialPoolSize(dpds.getInitialPoolSize());
				tmpDs.setAcquireRetryAttempts(dpds.getAcquireRetryAttempts());
				tmpDs.setCheckoutTimeout(dpds.getCheckoutTimeout());
				tmpDs.setMaxIdleTime(dpds.getMaxIdleTime());
			}
			rb.setDeePooledDS(tmpDs);
			drb.setDr(rb);
		}
		else if(tmpId == DeeResourceEnum.JNDIDataSource.ordinal()){
			JNDIResourceBean rb = new JNDIResourceBean();
			rb.setJndi(jndi);
			rb.setResource_name(drb.getResource_name());
			rb.setResoutce_desc("");
			drb.setDr(rb);
		}
		else{
			A8MetaDatasourceBean rb = new A8MetaDatasourceBean();
			if("jdbc".equals(dsType)){
				rb.setDriver(driver);
				rb.setUrl(url);
				rb.setUser(username);
				rb.setPassword(password);
				//设置连接池信息
				DeePooledDataSource tmpDs = new DeePooledDataSource("");
				if(dpds != null){
					tmpDs.setMaxPoolSize(dpds.getMaxPoolSize());
					tmpDs.setMinPoolSize(dpds.getMinPoolSize());
					tmpDs.setInitialPoolSize(dpds.getInitialPoolSize());
					tmpDs.setAcquireRetryAttempts(dpds.getAcquireRetryAttempts());
					tmpDs.setCheckoutTimeout(dpds.getCheckoutTimeout());
					tmpDs.setMaxIdleTime(dpds.getMaxIdleTime());
				}
				rb.setDeePooledDS(tmpDs);
			}
			else{
				rb.setJndi(jndi);
			}
			rb.setResource_name(drb.getResource_name());
			rb.setResoutce_desc("");
			drb.setDr(rb);
		}
	}
	
	/** 
	 * 判断是否A8元数据
	 */
	public String isA8meta(){
		DeeResourceBean drb = new DeeResourceBean();
		try{
			setDRB(drb);
			int templateId = Integer.parseInt(drb.getDeeResourceTemplate().getResource_template_id());
			if(templateId != DeeResourceEnum.A8MetaDatasource.ordinal()){
				return ajaxForwardSuccess("跳过");
			}
			setRB(templateId,drb);
			if(!deeResourceManager.testConnect(drb)){
				return ajaxForwardError("连接不通，是否还继续保存？");			
			}
			try{
				if(!deeResourceManager.isA8Meta(drb)){
					ActionContext.getContext().put("retFlag", "cannot");
					return ajaxForwardError("不是A8元数据，不能保存！");			
				}
			}catch(Exception e){
				ActionContext.getContext().put("retFlag", "cannot");
				return ajaxForwardError("不是A8元数据，不能保存！");			
			}
			return ajaxForwardSuccess("是A8元数据");
		}
		catch(SystemException e){
			String retMsg = "判断元数据失败，是否还继续保存？";
			log.debug(e.getMessage(),e);
			return ajaxForwardError(retMsg);
		}
		catch(Exception e){
			String retMsg = "判断元数据出错，是否还继续保存？";
			log.debug(e.getMessage(),e);
			return ajaxForwardError(retMsg);
		}
	}
	/** 
	 * 数据源记录删除
	 */
	public String delete(){
		String retMsg = "";
		try {
			retMsg = delDsById(uid);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("删除数据源异常："+e.getLocalizedMessage());
		}
		if(!"".equals(retMsg)){
			return ajaxForwardError("数据源【"+retMsg+"】被引用，不能删除！");
		}
		return ajaxForwardSuccess(getText("msg.operation.success"));
	}
	
	private String delDsById(String drId) throws SystemException{
		String retMsg = deeResourceManager.isQuoteByFlow(drId);
		if(!"".equals(retMsg)){
			return retMsg;
		}
		deeResourceManager.deleteByDrId(drId);
		return retMsg;
	}
	
	/** 
	 * 批量数据源删除
	 */
	public String deleteAll(){
		String[] arrId=ids.split(",");
		String retMsg = "";
		for(String id:arrId){
			if(id == null || "".equals(id))
				continue;
			String rMsg = "";
			try {
				rMsg = delDsById(id);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				return ajaxForwardError("删除数据源异常："+e.getLocalizedMessage());
			}
			if(!"".equals(rMsg)){
				retMsg += "".equals(retMsg)?rMsg:"，"+rMsg;
			}
		}
		if(!"".equals(retMsg)){
			return ajaxForwardError("数据源【"+retMsg+"】被引用，不能被删除！");
		}
		return ajaxForwardSuccess(getText("msg.operation.success"));
	}
	
	/** 
	 * 测试数据源
	 */
	public String testCon(){
		DeeResourceBean drb = new DeeResourceBean();
		try{
			setDRB(drb);
			int templateId = Integer.parseInt(drb.getDeeResourceTemplate().getResource_template_id());
			setRB(templateId,drb);
			if(deeResourceManager.testConnect(drb)){
				if(templateId == DeeResourceEnum.A8MetaDatasource.ordinal()){
					try{
						if(!deeResourceManager.isA8Meta(drb)){
							return ajaxForwardError("连接成功，但该数据源不能作为A8元数据！");			
						}
					}catch(Exception e){
						return ajaxForwardError("连接成功，但该数据源不能作为A8元数据！");			
					}
				}
				return ajaxForwardSuccess("连接成功");				
			}
			return ajaxForwardError("连接失败");
		}
		catch(Throwable e){
//			String retMsg = "测试数据源出错：" + e.getMessage();
			String retMsg = "测试数据源失败，请检查配置！";
			log.error(e.getMessage(),e);
			return ajaxForwardError(retMsg);
		}
	}
	
	/** 
	 * 数据校验
	 */
	private String chkVals(){
		String retMsg = "";
		if(dis_name == null || "".equals(dis_name)){
			retMsg = "名称不能为空！";
			return retMsg;
		}
		if("jdbc".equals(dsType)){
			if(driver == null || "".equals(driver)){
				retMsg = "数据库驱动不能为空！";
				return retMsg;
			}
			if(url == null || "".equals(url)){
				retMsg = "数据连接地址不能为空！";
				return retMsg;
			}
			if(username == null || "".equals(username)){
				retMsg = "用户名不能为空！";
				return retMsg;
			}
			if(password == null || "".equals(password)){
				retMsg = "密码不能为空！";
				return retMsg;
			}
		}
		else{
			if(jndi == null || "".equals(jndi)){
				retMsg = "JNDI名称不能为空！";
				return retMsg;
			}
		}
		return retMsg;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public DeeResourceManager getDeeResourceManager() {
		return deeResourceManager;
	}

	public void setDeeResourceManager(DeeResourceManager deeResourceManager) {
		this.deeResourceManager = deeResourceManager;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJndi() {
		return jndi;
	}

	public void setJndi(String jndi) {
		this.jndi = jndi;
	}

	public String getDsType() {
		return dsType;
	}

	public void setDsType(String dsType) {
		this.dsType = dsType;
	}

	public String getA8metajdbc() {
		return a8metajdbc;
	}

	public void setA8metajdbc(String a8metajdbc) {
		this.a8metajdbc = a8metajdbc;
	}

	public String getA8metajndi() {
		return a8metajndi;
	}

	public void setA8metajndi(String a8metajndi) {
		this.a8metajndi = a8metajndi;
	}

	public String getDis_name() {
		return dis_name;
	}

	public void setDis_name(String dis_name) {
		this.dis_name = dis_name;
	}

	public String getCallbackType() {
		return callbackType;
	}

	public void setCallbackType(String callbackType) {
		this.callbackType = callbackType;
	}

	public String getNavTabId() {
		return navTabId;
	}

	public void setNavTabId(String navTabId) {
		this.navTabId = navTabId;
	}

	public String getDsDrv() {
		return dsDrv;
	}

	public void setDsDrv(String dsDrv) {
		this.dsDrv = dsDrv;
	}

	public DeePooledDataSource getDpds() {
		return dpds;
	}

	public void setDpds(DeePooledDataSource dpds) {
		this.dpds = dpds;
	}
	
}
