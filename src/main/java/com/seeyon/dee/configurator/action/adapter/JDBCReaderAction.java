package com.seeyon.dee.configurator.action.adapter;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.JDBCReaderBean;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;

public class JDBCReaderAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6460595386625708311L;
    private static final String JDBCREADER = "jdbcreader";
	private final static Log log = LogFactory.getLog(JDBCReaderAction.class);
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
	
    private String navTabId;	
	private String callbackType;	
    
    private String flowId;          // flow标识
    private String resourceId;      // 资源ID --> 修改时使用
    private int sort;               // 排序号   --> 新增时使用
    private String resourceType;
    private List<KeyValue> items;
    private String ref_id;
//    private String dis_name;
    private DeeResourceBean bean;

	
    /**
     * 查看
     * 
     * @return
     */
    public String jdbcreadershow() {
//		DeeResourceBean dr = null;
		try {
			ActionContext.getContext().put("dsList", deeResourceManager.findAllJdbcDs());
			//新增
			if(resourceId == null || "".equals(resourceId))
				return JDBCREADER;
			//编辑
			bean = deeResourceManager.getDrByDrId(resourceId);
			if(bean == null){
				return ajaxForwardError("未获取到JdbcReader适配器信息！");
			}
//			ActionContext.getContext().put("bean", dr);
			ActionContext.getContext().put("jdbcReader", new ConvertDeeResourceBean(bean).getResource());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
            return ajaxForwardError("获取数据源异常:"+e.getLocalizedMessage());
		}
        return JDBCREADER;
    }

	/** 
	 * JDBCreader保存
	 */
	public String saveJdbcReader(){
		if(flowId == null || "".equals(flowId)){
			return ajaxForwardError("FlowId为空，不能作任何保存！");
		}
		try{
			String retMsg = chkVals();
			if(!"".equals(retMsg)){
				//数据校验
				return ajaxForwardError(retMsg);
			}
			setDRB();
			if(resourceId != null && !"".equals(resourceId)){
				//修改JDBCReader适配器
                deeAdapterMgr.updateAdapter(bean);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,sort,flowId);
                return ajaxForwardSuccess("修改成功！", infor);
			}
			//新增适配器
            deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
            //反回写到页面
            resourceId = bean.getResource_id();
            String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_SAVE,sort,flowId);
            return ajaxForwardSuccess("新增成功！", infor);
		}
		catch(Throwable e){
			String retMsg = "保存JDBCREADER出错：" + e.getMessage();
			log.error(retMsg,e);
			return ajaxForwardError(retMsg);
		}
	}
	/** 
	 * 填写DeeResourceBean数据信息
	 */
	private void setDRB(){
		if(bean != null){
			String rsId = UuidUtil.uuid();
			if(resourceId != null && !"".equals(resourceId)){
				rsId = resourceId;
			}
			bean.setResource_id(rsId);
			bean.setResource_name(rsId);
			DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
			tmpBean.setResource_template_id(DeeResourceEnum.JDBCREADER.ordinal() + "");
			bean.setRef_id(ref_id);
			bean.setDeeResourceTemplate(tmpBean);
//			drb.setDis_name(dis_name);
			bean.setResource_desc("");
			bean.setCreate_time(DateUtil.getSysTime());
			JDBCReaderBean jb = new JDBCReaderBean();
			jb.setName(rsId);
			jb.setDataSource(ref_id);
			Map<String, String> sqlMap = new LinkedHashMap<String, String>();
	        for (KeyValue item : items) {
	        	sqlMap.put(item.getKey(), StringEscapeUtils.escapeXml(item.getValue()));
	        }
	        jb.setMap(sqlMap);
	        bean.setDr(jb);
		}
	}
	
	private String chkVals(){
		String retMsg = "";
		if(bean.getDis_name() == null || "".equals(bean.getDis_name())){
			retMsg = "名称不能为空！";
			return retMsg;
		}
		if(items == null || items.size() < 1){
			retMsg = "查询SQL不能为空！";
			return retMsg;
		}
		Map<String, String> tmpMap = new HashMap<String, String>();
        for (KeyValue item : items) {
        	tmpMap.put(item.getKey(),"");
        }
		if(items.size() != tmpMap.size()){
			retMsg = "SQL列表中不能有相同的SQL名称！";
			return retMsg;
		}
		return retMsg;
	}

    public FlowSubBean getFlowSubBean(DeeResourceBean drb) {
        FlowSubBean sub = new FlowSubBean();
        FlowBean flowBean = new FlowBean();
        flowBean.setFLOW_ID(flowId);
        
        sub.setFlow_sub_id(UuidUtil.uuid());
        sub.setFlow(flowBean);
        sub.setDeeResource(drb);
        sub.setSort(sort);
        
        return sub;
    }
	public DeeResourceManager getDeeResourceManager() {
		return deeResourceManager;
	}


	public void setDeeResourceManager(DeeResourceManager deeResourceManager) {
		this.deeResourceManager = deeResourceManager;
	}


	public DeeAdapterMgr getDeeAdapterMgr() {
		return deeAdapterMgr;
	}


	public void setDeeAdapterMgr(DeeAdapterMgr deeAdapterMgr) {
		this.deeAdapterMgr = deeAdapterMgr;
	}


	public String getFlowId() {
		return flowId;
	}


	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}


	public String getResourceId() {
		return resourceId;
	}


	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}


	public int getSort() {
		return sort;
	}


	public void setSort(int sort) {
		this.sort = sort;
	}


	public String getResourceType() {
		return resourceType;
	}


	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}


	public List<KeyValue> getItems() {
		return items;
	}


	public void setItems(List<KeyValue> items) {
		this.items = items;
	}

	public String getRef_id() {
		return ref_id;
	}

	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}

//	public String getDis_name() {
//		return dis_name;
//	}
//
//	public void setDis_name(String dis_name) {
//		this.dis_name = dis_name;
//	}

	public DeeResourceBean getBean() {
		return bean;
	}

	public void setBean(DeeResourceBean bean) {
		this.bean = bean;
	}

	public String getNavTabId() {
		return navTabId;
	}

	public void setNavTabId(String navTabId) {
		this.navTabId = navTabId;
	}

	public String getCallbackType() {
		return callbackType;
	}

	public void setCallbackType(String callbackType) {
		this.callbackType = callbackType;
	}
    
    
}
