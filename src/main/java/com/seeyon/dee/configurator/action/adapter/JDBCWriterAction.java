package com.seeyon.dee.configurator.action.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.opensymphony.xwork2.ActionContext;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.JDBCReaderBean;
import com.seeyon.v3x.dee.bean.JDBCWriterBean;
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
/**
 * JDBC writer适配器
 * @author yangyu
 *
 */
public class JDBCWriterAction extends BaseAction{

	private static final long serialVersionUID = -5410212586505920731L;
	private static Logger logger = Logger.getLogger(JDBCWriterAction.class.getName());
	private static final String JDBCWRITER = "jdbcwriter";
	
	@Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private List<KeyValue> items;
    private DeeResourceBean bean;
    private String ref_id;

    /**
     * 查看
     * 
     * @return
     */
    public String jdbcWritershow() {
		try {
			ActionContext.getContext().put("dsList", deeResourceManager.findAllJdbcDs());
			//新增
			if(resourceId == null || "".equals(resourceId))
				return JDBCWRITER;
			//编辑
			bean = deeResourceManager.getDrByDrId(resourceId);
			if(bean == null){
				return ajaxForwardError("未获取到JdbcWriter适配器信息！");
			}
			ActionContext.getContext().put("jdbcBean", new ConvertDeeResourceBean(bean).getResource());
		} catch (SystemException e) {
			logger.error(e);
            return ajaxForwardError("获取数据源异常:"+e.getLocalizedMessage());
		}
        return JDBCWRITER;
    }
    
	/** 
	 * JDBCWriter保存或修改
	 */
	public String saveJdbcWriter(){
		if(flowId == null || "".equals(flowId)){
			return ajaxForwardError("FlowId为空，不能作任何保存！");
		}
		try{
			String retMsg = chkVals();
			if(!"".equals(retMsg)){
				//数据校验
				return ajaxForwardError(retMsg);
			}
			
			if(!checkParams(items)){
				return ajaxForwardError("参数重复，修改后再保存！");
			}
			
			
			setDRB(bean);
			if(resourceId != null && !"".equals(resourceId)){
				//修改JDBC目标适配器
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
			String retMsg = "保存JDBC目标出错：" + e.getMessage();
			logger.error(retMsg);
			return ajaxForwardError(retMsg);
		}
	}

	
	/** 
	 * 填写DeeResourceBean数据信息
	 */
	private void setDRB(DeeResourceBean drb){
		if(drb != null){
			String rsId = UuidUtil.uuid();
			if(resourceId != null && !"".equals(resourceId)){
				rsId = resourceId;
			}
			drb.setResource_id(rsId);
			drb.setResource_name(rsId);
			DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
			tmpBean.setResource_template_id(DeeResourceEnum.JDBCWRITER.ordinal() + "");
            drb.setRef_id(ref_id);
			drb.setDeeResourceTemplate(tmpBean);
//			drb.setDis_name(bean.getDis_name());
			drb.setResource_desc("");
			drb.setCreate_time(DateUtil.getSysTime());
			JDBCWriterBean jb = new JDBCWriterBean();
			jb.setName(rsId);
			jb.setDataSource(ref_id);
			jb.setDesc("");
			Map<String, String> sqlMap = new LinkedHashMap<String, String>();
	        for (KeyValue item : items) {
	        	sqlMap.put(item.getKey(), item.getValue());
	        }
	        jb.setMap(sqlMap);
	        drb.setDr(jb);
	        
		}
	}
	
	private String chkVals(){
		String retMsg = "";
		if( bean.getDis_name()== null || "".equals(bean.getDis_name())){
			retMsg = "名称不能为空！";
			return retMsg;
		}
		if(items == null || items.size() < 1){
			retMsg = "表名、主键不能为空！";
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
    
    public boolean checkParams(List<KeyValue> items)
    {
    	for(int i=0;i<items.size();i++)
    	{
    		for(int j=i+1;j<items.size();j++)
    		{
    			if(items.get(i).getKey().trim().equals(items.get(j).getKey().trim()))
    			{
    				return false;
    			}
    		}
    	}
    	return true;
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

	public DeeResourceBean getBean() {
		return bean;
	}

	public void setBean(DeeResourceBean bean) {
		this.bean = bean;
	}

	public String getRef_id() {
		return ref_id;
	}

	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}
}
