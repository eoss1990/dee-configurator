package com.seeyon.dee.configurator.action.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.seeyon.v3x.dee.adapter.sap.jco.plugin.ComparatorJCoData;
import com.seeyon.v3x.dee.adapter.sap.jco.plugin.jcoData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.SapJcoProcessorBean;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;


/**
 * SapJco Action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午03:11:59
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class SapJcoProcessorAction extends BaseAction {

    private static final long serialVersionUID = -7134362349445304675L;
    
    private static Logger logger = Logger.getLogger(SapJcoProcessorAction.class.getName());

    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String SAPJCO_VIEW = "sapjco";
    private static final String JCOSTRUCTURE = "jcostructure";
    private static final String JCOTABLE = "jcotable";
    
    private String flowId;          // flow标识
    private String resourceId;      // 资源ID --> 修改时使用
    private int sort;               // 排序号   --> 新增时使用
    private String resourceType;
    private List<KeyValue> items;
    private List<KeyValue> jcoReturn;
    private List<jcoData> jcoStructureData = new ArrayList<jcoData>();
    private List<jcoData> jcoTableData = new ArrayList<jcoData>();
    private SapJcoProcessorBean sjpBean;
    private DeeResourceBean bean;

    /**
     * 保存
     * 
     * @return
     */
    public String save() {
        if (items == null) {
            items = new ArrayList<KeyValue>();
        }
        
        if(jcoReturn == null){
        	return ajaxForwardError("返回值不能为空！");
        }
        
        if (sjpBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        
        if (!toDeeResource()) {
            return ajaxForwardError(Constants.PARAMSCHECKMSG);
        }
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(sjpBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
            bean.setDeeResourceTemplate(tmpBean);
            
            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        } else {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                SapJcoProcessorBean sjpB = new SapJcoProcessorBean();
    			if (drb != null && drb.getDeeResourceTemplate() != null)
    			{
    				drb.setResource_template_id(drb.getDeeResourceTemplate().getResource_template_id());
    				drb.setResource_template_name(drb.getDeeResourceTemplate().getResource_template_name());
    				sjpB = (SapJcoProcessorBean) new ConvertDeeResourceBean(drb).getResource();
    			}
                sjpBean.setJcoStructureMap(sjpB.getJcoStructureMap());
                sjpBean.setJcoTableMap(sjpB.getJcoTableMap());
                drb.setDis_name(bean.getDis_name());
                drb.setDr(sjpBean);
                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,drb.getFlowSub().getSort(),flowId);
                return ajaxForwardSuccess("修改成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常!");
            }
        }
    }
    
    /**
     * 查看
     * 
     * @return
     */
    public String view() {
        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && 
                    bean.getDeeResourceTemplate() != null)
                {
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    sjpBean = (SapJcoProcessorBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return SAPJCO_VIEW;
    }
    
    /**
     * 回显JCoStructure映射配置界面
     * @return
     */
    public String viewJcoStructureMap(){
    	this.getSapJcoBean();
    	this.setJcoStructureDataList();
    	return JCOSTRUCTURE;
    }
    
    /**
     * 回显JCoTable映射配置界面
     * @return
     */
    public String viewJcoTableMap(){
    	this.getSapJcoBean();
    	this.setJcoTableDataList();
    	return JCOTABLE;
    }
    
    /**
     * 保存jcoStructure映射配置
     * @return
     */
    public String saveJcoStructureMap(){
    	SapJcoProcessorBean sjpB = new SapJcoProcessorBean();
    	DeeResourceBean drb = new DeeResourceBean();
    	
    	if(jcoStructureData == null){
    		return ajaxForwardError("映射配置不能为空！");
    	}

    	if (StringUtils.isNotBlank(resourceId)) {
    		try {
    			drb = deeResourceManager.get(resourceId);
    			if (drb != null && drb.getDeeResourceTemplate() != null)
    			{
    				drb.setResource_template_id(drb.getDeeResourceTemplate().getResource_template_id());
    				drb.setResource_template_name(drb.getDeeResourceTemplate().getResource_template_name());
    				sjpB = (SapJcoProcessorBean) new ConvertDeeResourceBean(drb).getResource();
    			}
    			else{
    				return ajaxForwardError("找不到适配器！");
    			}
    		} catch (Exception e) {
    			logger.error(e.getMessage(), e);
    		}
    	}else
    	{
    		return ajaxForwardError("适配器未保存！");
    	}

    	try {
    		String error = this.setJcoStructureMap(sjpB, jcoStructureData);
    		if(error.equals("keyRepeat")) return ajaxForwardError("JCoStructure名+字段名不能重复！");
    		if(error.equals("differentDoc")) return ajaxForwardError("同一JCoStructure只能来源于一张表，请通过映射配置转换！");
    		drb.setDr(sjpB);
    		deeAdapterMgr.updateAdapter(drb);
    		return ajaxForwardSuccess("保存成功！");
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    		return ajaxForwardError("异常!");
    	}
    }
    
    public String saveJcoTableMap(){
    	SapJcoProcessorBean sjpB = new SapJcoProcessorBean();
    	DeeResourceBean drb = new DeeResourceBean();
    	
    	if(jcoTableData == null){
    		return ajaxForwardError("映射配置不能为空！");
    	}

    	if (StringUtils.isNotBlank(resourceId)) {
    		try {
    			drb = deeResourceManager.get(resourceId);
    			if (drb != null && drb.getDeeResourceTemplate() != null)
    			{
    				drb.setResource_template_id(drb.getDeeResourceTemplate().getResource_template_id());
    				drb.setResource_template_name(drb.getDeeResourceTemplate().getResource_template_name());
    				sjpB = (SapJcoProcessorBean) new ConvertDeeResourceBean(drb).getResource();
    			}
    			else{
    				return ajaxForwardError("找不到适配器！");
    			}
    		} catch (Exception e) {
    			logger.error(e.getMessage(), e);
    		}
    	}else
    	{
    		return ajaxForwardError("适配器未保存！");
    	}

    	try {
    		String error = this.setJcoTableMap(sjpB, jcoTableData);
    		if(error.equals("keyRepeat")) return ajaxForwardError("JCoTable名+字段名不能重复！");
    		if(error.equals("differentDoc")) return ajaxForwardError("同一JCoTable只能来源于一张表，请通过映射配置转换！");
    		drb.setDr(sjpB);
    		deeAdapterMgr.updateAdapter(drb);
    		return ajaxForwardSuccess("保存成功！");
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    		return ajaxForwardError("异常!");
    	}
    }
    
    /**
     * 通过resourceId取得SapJcoProcessorBean
     */
    public void getSapJcoBean(){
    	
    	if(StringUtils.isNotBlank(resourceId)){
    		try{
    			bean = deeResourceManager.get(resourceId);
                if (bean != null && 
                    bean.getDeeResourceTemplate() != null)
                {
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    sjpBean = (SapJcoProcessorBean) new ConvertDeeResourceBean(bean).getResource();
                }
    		}catch(Exception e){
    			logger.error(e.getMessage(), e);
    		}
    	}
    }
    
    /**
     * 将jcoStrucutureMap转换成jcoData
     */
    public void setJcoStructureDataList(){

    	if(sjpBean.getJcoStructureMap()!=null)
    	{
	    	for(Entry<String, String> entry : sjpBean.getJcoStructureMap().entrySet()) {
	    		String[] struc = entry.getKey().split(",");
	    		String[] doc = entry.getValue().split(",");
	    		jcoData jd = new jcoData(struc[0],struc[1],doc[0],doc[1]);
	    		this.jcoStructureData.add(jd);
	    	}
    	}
    }
    
    /**
     * 将jcoTableMap转换成jcoData
     */
    public void setJcoTableDataList(){

    	if(sjpBean.getJcoTableMap()!=null)
    	{
    		for(Entry<String, String> entry : sjpBean.getJcoTableMap().entrySet()) {
    			String[] struc = entry.getKey().split(",");
    			String[] doc = entry.getValue().split(",");
    			jcoData jd = new jcoData(struc[0],struc[1],doc[0],doc[1]);
    			this.jcoTableData.add(jd);
    		}
    	}
    }
    
    /**
     * 转换
     * 
     * @return true，没有重复项；false，有重复项
     */
    public boolean toDeeResource() {
        boolean exist = true;
        Map<String, String> paraMap = new LinkedHashMap<String, String>();
        Map<String, String>	jcoReturnMap = new LinkedHashMap<String, String>();
        
        for (KeyValue item : items) {
        	if(item==null)
        		continue;
            if (paraMap.containsKey(item.getKey())) {
                exist = false;
                break;
            }
            paraMap.put(item.getKey(), item.getValue());
        }
        
        for (KeyValue item : jcoReturn) {
        	if(item==null)
        		continue;
            if (jcoReturnMap.containsKey(item.getKey())) {
                exist = false;
                break;
            }
            jcoReturnMap.put(item.getKey(), item.getValue());
        }
        sjpBean.setMap(paraMap);
        sjpBean.setJcoReturnMap(jcoReturnMap);
        return exist;
    }
    
    /**
     * 
     * @param sjpB
     * @param jcoData
     * @return
     */
    public String setJcoStructureMap(SapJcoProcessorBean sjpB,List<jcoData> jcoData){
    	String error = "right";
        Map<String, String> jcoDataMap = new LinkedHashMap<String, String>();  
        Map<String, String> structureDoc = new LinkedHashMap<String, String>();
        ComparatorJCoData comparator = new ComparatorJCoData();
        
		Collections.sort(jcoData, comparator);
        for (jcoData item : jcoData) {
        	if(item==null)
        		continue;
        	if(jcoDataMap.containsKey(item.getJcoName()+","+item.getJcoValue()))
        	{
        		error="keyRepeat";
        		break;
        	}
        	if(structureDoc.containsKey(item.getJcoName())&&!structureDoc.containsValue(item.getDocName()))
        	{
        		error="differentDoc";
        		break;
        	}
        	jcoDataMap.put(item.getJcoName()+","+item.getJcoValue(), item.getDocName()+","+item.getDocValue());
        	structureDoc.put(item.getJcoName(), item.getDocName());
        }
        sjpB.setJcoStructureMap(jcoDataMap);
        return error;
    }
    
    /**
     * 
     * @param sjpB
     * @param jcoData
     * @return
     */
    public String setJcoTableMap(SapJcoProcessorBean sjpB,List<jcoData> jcoData){
    	String error = "right";
        Map<String, String> jcoDataMap = new LinkedHashMap<String, String>();
        Map<String, String> tableDoc = new LinkedHashMap<String, String>();
        ComparatorJCoData comparator = new ComparatorJCoData();
        
		Collections.sort(jcoData, comparator);
        for (jcoData item : jcoData) {
        	if(item==null)
        		continue;
        	if(jcoDataMap.containsKey(item.getJcoName()+","+item.getJcoValue()))
        	{
        		error="keyRepeat";
        		break;
        	}
        	if(tableDoc.containsKey(item.getJcoName())&&!tableDoc.containsValue(item.getDocName()))
        	{
        		error="differentDoc";
        		break;
        	}
        	jcoDataMap.put(item.getJcoName()+","+item.getJcoValue(), item.getDocName()+","+item.getDocValue());
        	tableDoc.put(item.getJcoName(), item.getDocName());
        }
        sjpB.setJcoTableMap(jcoDataMap);
        return error;
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

    public SapJcoProcessorBean getSjpBean() {
        return sjpBean;
    }

    public void setSjpBean(SapJcoProcessorBean sjpBean) {
        this.sjpBean = sjpBean;
    }

	public List<KeyValue> getJcoReturn() {
		return jcoReturn;
	}

	public void setJcoReturn(List<KeyValue> jcoReturn) {
		this.jcoReturn = jcoReturn;
	}

	public List<jcoData> getJcoStructureData() {
		return jcoStructureData;
	}

	public void setJcoStructureData(List<jcoData> jcoStructureData) {
		this.jcoStructureData = jcoStructureData;
	}

	public List<jcoData> getJcoTableData() {
		return jcoTableData;
	}

	public void setJcoTableData(List<jcoData> jcoTableData) {
		this.jcoTableData = jcoTableData;
	}
	
	
    
    
    
}
