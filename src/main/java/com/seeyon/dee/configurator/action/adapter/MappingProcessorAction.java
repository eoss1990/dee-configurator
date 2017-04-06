package com.seeyon.dee.configurator.action.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.v3x.dee.bean.ColumnMappingProcessorBean;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.JDBCReaderBean;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.dee.configurator.mgr.FlowBaseMgr;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.mapping.model.MappingResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;
import dwz.framework.util.MappingUtil;
import dwz.framework.util.ProcessUtil;

public class MappingProcessorAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7699068010107682950L;
	private static Log log = LogFactory.getLog(MappingProcessorAction.class);
	
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;

	@Autowired
	@Qualifier("flowBaseMgr")
	private FlowBaseMgr flowBaseMgr; 
	
//    @Autowired
//    @Qualifier("dsTreeManager")
//	private DSTreeManager dsTreeManager;
    
    private static final String MAPPING = "mapping_processor";
    
	private String navTabId;	
	private String callbackType;
	private String jsonData;
	private String mapingData;
	
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private String mapIndex;
    private String transNoMapping;
    private String chk;
    
    private DeeResourceBean bean;
    private DeeResourceBean refBean;
    private MappingResourceBean refMapRb;
    
    private String[] mapLeft;
    private String[] sourceTableName;
    private String[] sourceColumnName;
    private String[] sourceTableName2;
    private String[] sourceColumnName2;
    
    private String[] mapRight;
    private String[] targetTableName;
    private String[] targetColumnName;
    private String[] targetTableName2;
    private String[] targetColumnName2;
    private String[] targetColumnInfo;
    private String[] selType;
    
    /**
     * 查看
     * 
     * @return
     */
    public String mappingShow() {
		try {
			ActionContext.getContext().put("dsList", deeResourceManager.findAllDictDs());
			//新增
			if(resourceId == null || "".equals(resourceId))
				return MAPPING;
			//编辑
			bean = deeResourceManager.get(resourceId);
			if(bean == null){
				return ajaxForwardError("未获取到字段映射主记录信息！");
			}
			bean.setDr(new ConvertDeeResourceBean(bean).getResource());
			refBean = deeResourceManager.get(bean.getRef_id());
			if(refBean == null){
				return ajaxForwardError("未获取到字段映射信息！");
			}
			refMapRb = new MappingResourceBean(refBean.getResource_code());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error("获取字段映射异常:"+e.getLocalizedMessage(),e);
            return ajaxForwardError("获取字段映射异常:"+e.getLocalizedMessage());
		}
        return MAPPING;
    }

	/** 
	 * MappingProcessor保存
	 */
	public String saveMappingProcessor(){
		if(flowId == null || "".equals(flowId)){
			return ajaxForwardError("FlowId为空，不能作任何保存！");
		}
		try{
			String retMsg = chkVals();
			if(!"".equals(retMsg)){
				//数据校验
				return ajaxForwardError(retMsg);
			}
			retMsg = saveRefMapping();
			if(!"".equals(retMsg)){
				//保存映射
				return ajaxForwardError(retMsg);
			}
			
			setMainResource();
			if(resourceId != null && !"".equals(resourceId)){
				//修改字段映射适配器
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
			String retMsg = "保存字段映射出错：" + e.getMessage();
			log.error(retMsg,e);
			return ajaxForwardError(retMsg);
		}
	}
	
	private void setMainResource(){
		ColumnMappingProcessorBean colMap = new ColumnMappingProcessorBean();
		if(resourceId != null && !"".equals(resourceId)){
			//修改映射适配器
			bean.setResource_id(resourceId);
			bean.setResource_name(resourceId);
			colMap.setName(resourceId);
		}
		else{
	        String uuid = UuidUtil.uuid();
			bean.setResource_id(uuid);
			bean.setResource_name(uuid);
			colMap.setName(uuid);
		}
		DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
		tmpBean.setResource_template_id(DeeResourceEnum.COLUMNMAPPINGPROCESSOR.ordinal()+"");
		bean.setDeeResourceTemplate(tmpBean);
		colMap.setMapping(bean.getRef_id());
		colMap.setDesc("");
		colMap.setTransNoMapping("1".equals(transNoMapping));
		bean.setDr(colMap);
		bean.setCreate_time(DateUtil.getSysTime());
	}
	private String setRefMapping(String rsId){
		String retMsg = "";
		if(refMapRb == null)
			refMapRb = new MappingResourceBean();
        refMapRb.setName(rsId);
		DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
		tmpBean.setResource_template_id(DeeResourceEnum.EXCHANGEMAPPING.ordinal()+"");
        refBean.setDeeResourceTemplate(tmpBean);
        if("0".equals(mapIndex)){
        	//映射列表页保存
			retMsg = setColMapping();
			if(!"".equals(retMsg)){
				//数据校验
				return retMsg;
			}
        	refBean.setDr(refMapRb);
        }
        else{
        	//脚本页保存
        	refBean.setDr(new ConvertDeeResourceBean(refBean).getResource());
        }
        refBean.setResource_id(rsId);
        refBean.setResource_name(rsId);
        refBean.setDis_name("字段映射"+rsId);
        refBean.setCreate_time(DateUtil.getSysTime());
		return retMsg;
	}
		
	//保存映射配置
	private String saveRefMapping(){
		if(flowId == null || "".equals(flowId)){
			return "FlowId为空，不能作任何保存！";
		}
		String retMsg = chkVals();
		try{
			if(!"".equals(retMsg)){
				//数据校验
				return retMsg;
			}
			if(bean.getRef_id() == null || "".equals(bean.getRef_id())){
				//没有映射配置，需要新增
				String uuid = UuidUtil.uuid();
				retMsg = setRefMapping(uuid);
				if(!"".equals(retMsg)){
					//数据校验
					return retMsg;
				}
		        deeResourceManager.saveDatasource(refBean);
		        bean.setRef_id(refBean.getResource_id());
			}
			else{
				//修改映射配置
				retMsg = setRefMapping(bean.getRef_id());
				if(!"".equals(retMsg)){
					//数据校验
					return retMsg;
				}
		        deeResourceManager.modifyDatasource(refBean);				
			}
			return retMsg;
		}
		catch(Throwable e){
			retMsg = "保存字段映射出错：" + e.getMessage();
			log.error(retMsg,e);
			return retMsg;
		}
	}

	private String chkVals(){
		String retMsg = "";
		if(bean.getDis_name() == null || "".equals(bean.getDis_name())){
			retMsg = "名称不能为空！";
			return retMsg;
		}
		return retMsg;
	}
	
	private String setColMapping(){
//		refMapRb 
		String retMsg = "";
		if(mapLeft == null ||mapLeft.length < 1){
			retMsg = "字段映射【源】不能为空！";
			return retMsg;
		}
		if(mapRight == null ||mapRight.length < 1){
			retMsg = "字段映射【目标】不能为空！";
			return retMsg;
		}
		if(mapLeft.length != mapRight.length){
			retMsg = "字段映射【源】与【目标】记录不一致！";
			return retMsg;
		}
		String[] desc = new String[mapLeft.length];
		for (int i = 0; i < mapLeft.length; i++) {
			String[] mL = mapLeft[i].split("/");
			if(mL == null || mL.length != 2 || "".equals(mL[0]) || "".equals(mL[1])){
				retMsg = "解析【源】字段有误，请检查！";
				return retMsg;
			}
			sourceTableName[i] = getVal(mL[0], sourceTableName2[i], sourceTableName[i]);
			sourceColumnName[i] = getVal(mL[1], sourceColumnName2[i],sourceColumnName[i]);
			String[] mR = mapRight[i].split("/");
			if(mR == null || mR.length != 2 || "".equals(mR[0]) || "".equals(mR[1])){
				retMsg = "解析【目标】字段有误，请检查！";
				return retMsg;
			}
			targetTableName[i] = getVal(mR[0], targetTableName2[i], targetTableName[i]);
			targetColumnName[i] = getVal(mR[1], targetColumnName2[i],targetColumnName[i]);
			desc[i] = "";
			
			//字段名#表类型(表中文名)#字段中文#表名
			if(targetColumnInfo[i] == null || "".equals(targetColumnInfo[i])){
				targetColumnInfo[i] = targetColumnName[i]+"#"+targetTableName2[i]+"#"+targetColumnName2[i]+"#"+targetTableName[i];
			}
		}
		refMapRb.setSourceTableName(sourceTableName);
		refMapRb.setSourceTableDisplay(sourceTableName2);
		refMapRb.setSourceColumnName(sourceColumnName);
		refMapRb.setSourceColumnDisplay(sourceColumnName2);
		refMapRb.setTargetTableName(targetTableName);
		refMapRb.setTargetTableDisplay(targetTableName2);
		refMapRb.setTargetColumnName(targetColumnName);
		refMapRb.setTargetColumnDisplay(targetColumnName2);
		refMapRb.setTargetColumnInfo(targetColumnInfo);
		refMapRb.setMapping(selType);
		refMapRb.setChk(chk);
		refMapRb.setDesc(desc);
		return retMsg;
	}
	
	private String getVal(String value1, String checkValue, String value2) {
		if (!"".equals(value1)) {
			if (value1.equals(checkValue) && !"".equals(value2)) {
				return value2;
			} else {
				return value1;
			}
		}
		return value2;
	}
	
	//切换数据可视化编辑框和脚本
	public String mappingChg(){
		String retMsg = "";
		try{
			ActionContext.getContext().put("retFlag","yes");
	        if("0".equals(mapIndex)){
	        	//脚本转换为可视化
	        	if(refBean.getResource_code() == null || "".equals(refBean.getResource_code())){
					JSONArray jsList = new JSONArray();
					jsonData = jsList.toString();
					return ajaxForwardSuccess("");
	        	}
	        	refMapRb = new MappingResourceBean(refBean.getResource_code());
				if(refMapRb == null || refMapRb.getSourceTableName() == null){
					//
					JSONArray jsList = new JSONArray();
					jsonData = jsList.toString();
					return ajaxForwardSuccess("");
				}
				jsonData = mapping2Json(refMapRb);
	        }
	        else{
	        	//将可视化转换为脚本
	        	refMapRb = new MappingResourceBean();
	    		if(mapLeft == null || mapRight == null){
	    			mapingData = "";
					return ajaxForwardSuccess("");
	    		}
				retMsg = setColMapping();
				if(!"".equals(retMsg)){
					//数据校验
					return ajaxForwardError(retMsg);
				}
				String mapName = bean.getRef_id();
				if(bean.getRef_id() == null || "".equals(bean.getRef_id())){
					mapName = UuidUtil.uuid();
					bean.setRef_id(mapName);
				}
				refMapRb.setName(mapName);
				refBean.setDr(refMapRb);
				mapingData = StringEscapeUtils.escapeHtml(refMapRb.toXML());
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("转换异常："+e.getLocalizedMessage(),e);
			return ajaxForwardError("转换异常："+e.getLocalizedMessage());
		}
		return ajaxForwardSuccess("");
	}

	//获取JDBCReader数据源SQL信息
	public String getJdbcReaderSql(){
		try {
			if(flowId == null || "".equals(flowId)){
				return ajaxForwardError("FlowId为空，不能获取JDBC来源！");
			}
			List<DeeResourceBean> rsList = flowBaseMgr.findResourceList(flowId);
			if(rsList == null || rsList.size() < 1)
				return ajaxForwardError("未获取任何来源！");
			String retMsg = "";
			List<KeyValue> jdbcList = new ArrayList<KeyValue>();
			List<KeyValue> fieldList = null;
			for(DeeResourceBean dr:rsList){
				if(Integer.parseInt(dr.getDeeResourceTemplate().getResource_template_id()) == DeeResourceEnum.JDBCREADER.ordinal()){
					JDBCReaderBean jr = new JDBCReaderBean(dr.getResource_code());
					for(Entry<String, String> entry : jr.getMap().entrySet()){
						if(entry.getKey() == null || "".equals(entry.getKey()))
							continue;
						if(entry.getValue() == null || "".equals(entry.getValue()))
							continue;
						fieldList = analysisSql(entry.getKey(),entry.getValue());
						if(fieldList == null){
							if(entry.getValue().indexOf("*") > 0){
								retMsg += "来源SQL:【"+entry.getValue()+"】，暂不作装载；";
							}
							else{
								retMsg += "解析来源SQL:【"+entry.getValue()+"】出错，未获到字段信息；";
							}
							continue;
						}
						jdbcList.addAll(fieldList);
					}
				}
			}
			
			if(!"".equals(retMsg))
				return ajaxForwardError(retMsg);
			if(jdbcList.size() < 1)
				return ajaxForwardError("没有JDBC来源！");
			jsonData = fieldList2Json(jdbcList);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error("获取JDBC来源异常："+e.getLocalizedMessage(),e);
			return ajaxForwardError("获取JDBC来源异常："+e.getLocalizedMessage());
		}
		return ajaxForwardSuccess("装载字段成功！");
	}
	
	private List<KeyValue> analysisSql(String mapTableName,String sSql){
		String newSql = sSql;
		sSql = sSql.trim().toLowerCase();
		List<KeyValue> fieldList = new ArrayList<KeyValue>();
		//判断是否有select *
		if(sSql.indexOf("*") < 0){
			if(sSql.indexOf(" from ") < 0)
				return null;
			KeyValue kv = null;
			String fieldStr = newSql.substring(6,sSql.indexOf(" from "));
			String[] fArray = fieldStr.split(",");
			if(fArray == null || fArray.length == 0)
				return null;
			for(int i=0;i<fArray.length;i++){
				if(fArray[i] == null)
					continue;
				kv = new KeyValue();
				kv.setKey(mapTableName);
				String field = fArray[i].trim();
				String newField = field.toLowerCase();
				if(newField.indexOf(" as ") > 0){
					newField = field.substring(newField.indexOf("as")+2);
				}
				else if(newField.indexOf(" ") > 0){
					newField = field.substring(newField.indexOf(" ")+1);
				}
				if(newField.indexOf(".") > 0){
					newField = field.substring(newField.indexOf(".")+1);
				}
				kv.setValue(newField.trim());
				fieldList.add(kv);
			}
			return fieldList;
		}
		else{
			return null;
//			//获取库中表
//			if(sSql.indexOf(" from") < 0)
//				return null;
//			String tableNames = sSql
//			DeeResourceBean dr = deeResourceManager.get(rsId);
//			List<TreeNode> nList = dsTreeManager.getDSAllColumns(dr, id);
		}
		
	}

	private String fieldList2Json(List<KeyValue> kvList) throws SystemException {
		// TODO Auto-generated method stub
		JSONObject jObj = null;
		JSONArray jsList = new JSONArray();
        String jsonData = ""; 
        for(KeyValue kv:kvList){
        	jObj = new JSONObject();
        	jObj.put("t", kv.getKey());
        	jObj.put("c", kv.getValue());
        	jsList.add(jObj);
        }
        jsonData = jsList.toString();
        return jsonData;  
	}
	
	private String mapping2Json(MappingResourceBean mapBean) throws SystemException {
		// TODO Auto-generated method stub
		JSONObject jObj = null;
		JSONArray jsList = new JSONArray();
        String jData = ""; 
    	for (int i = 0; i < mapBean.getSourceTableName().length; i++) {
        	jObj = new JSONObject();
        	jObj.put("st", MappingUtil.getNull2Str(mapBean.getSourceTableName()[i]));
        	jObj.put("sc", MappingUtil.getNull2Str(mapBean.getSourceColumnName()[i]));
        	jObj.put("st2", MappingUtil.getNull2Str(mapBean.getSourceTableDisplay()[i]));
        	jObj.put("sc2", MappingUtil.getNull2Str(mapBean.getSourceColumnDisplay()[i]));
        	
        	jObj.put("tt", MappingUtil.getNull2Str(mapBean.getTargetTableName()[i]));
        	jObj.put("tc", MappingUtil.getNull2Str(mapBean.getTargetColumnName()[i]));
        	jObj.put("tt2", MappingUtil.getNull2Str(mapBean.getTargetTableDisplay()[i]));
        	jObj.put("tc2", MappingUtil.getNull2Str(mapBean.getTargetColumnDisplay()[i]));
        	jObj.put("tci", MappingUtil.getNull2Str(mapBean.getTargetColumnInfo()[i]));

        	jObj.put("m", MappingUtil.getNull2Str(mapBean.getMapping()[i]));
        	jsList.add(jObj);
        }
        jData = jsList.toString();
        return jData;  
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

	public DeeResourceBean getBean() {
		return bean;
	}

	public void setBean(DeeResourceBean bean) {
		this.bean = bean;
	}

	public MappingResourceBean getRefMapRb() {
		return refMapRb;
	}

	public void setRefMapRb(MappingResourceBean refMapRb) {
		this.refMapRb = refMapRb;
	}

	public DeeResourceBean getRefBean() {
		return refBean;
	}

	public void setRefBean(DeeResourceBean refBean) {
		this.refBean = refBean;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(String mapIndex) {
		this.mapIndex = mapIndex;
	}

	public String[] getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String[] sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String[] getSourceColumnName() {
		return sourceColumnName;
	}

	public void setSourceColumnName(String[] sourceColumnName) {
		this.sourceColumnName = sourceColumnName;
	}

	public String[] getSourceTableName2() {
		return sourceTableName2;
	}

	public void setSourceTableName2(String[] sourceTableName2) {
		this.sourceTableName2 = sourceTableName2;
	}

	public String[] getSourceColumnName2() {
		return sourceColumnName2;
	}

	public void setSourceColumnName2(String[] sourceColumnName2) {
		this.sourceColumnName2 = sourceColumnName2;
	}

	public String[] getTargetTableName() {
		return targetTableName;
	}

	public void setTargetTableName(String[] targetTableName) {
		this.targetTableName = targetTableName;
	}

	public String[] getTargetColumnName() {
		return targetColumnName;
	}

	public void setTargetColumnName(String[] targetColumnName) {
		this.targetColumnName = targetColumnName;
	}

	public String[] getTargetTableName2() {
		return targetTableName2;
	}

	public void setTargetTableName2(String[] targetTableName2) {
		this.targetTableName2 = targetTableName2;
	}

	public String[] getTargetColumnName2() {
		return targetColumnName2;
	}

	public void setTargetColumnName2(String[] targetColumnName2) {
		this.targetColumnName2 = targetColumnName2;
	}

	public String[] getTargetColumnInfo() {
		return targetColumnInfo;
	}

	public void setTargetColumnInfo(String[] targetColumnInfo) {
		this.targetColumnInfo = targetColumnInfo;
	}

	public String[] getSelType() {
		return selType;
	}

	public void setSelType(String[] selType) {
		this.selType = selType;
	}

	public String[] getMapLeft() {
		return mapLeft;
	}

	public void setMapLeft(String[] mapLeft) {
		this.mapLeft = mapLeft;
	}

	public String[] getMapRight() {
		return mapRight;
	}

	public void setMapRight(String[] mapRight) {
		this.mapRight = mapRight;
	}

	public String getTransNoMapping() {
		return transNoMapping;
	}

	public void setTransNoMapping(String transNoMapping) {
		this.transNoMapping = transNoMapping;
	}

	public String getChk() {
		return chk;
	}

	public void setChk(String chk) {
		this.chk = chk;
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

	public FlowBaseMgr getFlowBaseMgr() {
		return flowBaseMgr;
	}

	public void setFlowBaseMgr(FlowBaseMgr flowBaseMgr) {
		this.flowBaseMgr = flowBaseMgr;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public String getMapingData() {
		return mapingData;
	}

	public void setMapingData(String mapingData) {
		this.mapingData = mapingData;
	}
}
