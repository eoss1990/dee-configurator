package com.seeyon.dee.configurator.action;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.opensymphony.xwork2.ActionContext;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.DEEConstants;
import com.seeyon.v3x.dee.TransformFactory;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.JDBCDictBean;
import com.seeyon.v3x.dee.bean.StaticDictBean;
import com.seeyon.v3x.dee.bean.SysFunctionBean;
import com.seeyon.dee.configurator.dto.SystemParameter;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.enumerate.EnumerateConfig;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;

public class DictAndFuncAction extends BaseAction {

	/**
	 * 数据字典业务
	 */
	private static final long serialVersionUID = 6205652745601910492L;
	private final static Log log = LogFactory.getLog(DictAndFuncAction.class);

    @Autowired
    @Qualifier("deeResourceManager")
	private DeeResourceManager deeResourceManager;
    
	private String navTabId;	
	private String callbackType;	
	private String uid;
	private String ids;
	
	private List<SystemParameter> dict;
	private String resource_id;
	private String resource_name;
	private String dis_name;
	private String dictType;
	private String ref_id;
	private String tableName;
	private String keyColumn;
	private String valueColumn;
	private String func_name;
	private String resource_para;
	
	
	
	public DeeResourceManager getDeeResourceManager() {
		return deeResourceManager;
	}
	public void setDeeResourceManager(DeeResourceManager deeResourceManager) {
		this.deeResourceManager = deeResourceManager;
	}
	
	/** 
	 * 获取字典列表
	 */
	public String dictlist(){
		String keyWd = getKeywords();
		int pageNum = this.getPageNum() > 0 ? this.getPageNum() - 1 : 0;
		int startIndex = pageNum * getNumPerPage();
		try {
			if(getOrderDirection() == null){
				setOrderDirection("desc");
				setOrderField("create_time");
			}
			List<DeeResourceBean> drbList = deeResourceManager.findAllDict(keyWd,startIndex, getNumPerPage(), realOrderField());
			ActionContext.getContext().put("dictList", drbList);
			this.setTotalCount(deeResourceManager.dictCount(keyWd));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取数据字典列表异常："+e.getLocalizedMessage());
		}
		return INPUT;
	}
	
	/** 
	 * 展示数据字典编辑页面
	 */
	public String dictshow(){
		DeeResourceBean dr = null;
		String dictType = "jdbc";
		try {
			ActionContext.getContext().put("dsList", deeResourceManager.findAllJdbcDs());
			//新增
			if(uid == null || "".equals(uid))
				return INPUT;
			//编辑
			dr = deeResourceManager.getDrByDrId(uid);
			if(dr == null){
				return ajaxForwardError("未获取到数据字典信息！");
			}
			DeeResource rb = new ConvertDeeResourceBean(dr).getResource();
			if(rb instanceof JDBCDictBean){
				ActionContext.getContext().put("jdbcDr", rb);
				dictType = "jdbc";
			}
			else if(rb instanceof StaticDictBean){
				List<SystemParameter> dt = new ArrayList<SystemParameter>();
				Map<String, String> map = ((StaticDictBean) rb).getMap();
				SystemParameter sp = null;
				for (Entry<String, String> entry : map.entrySet()) {
					sp = new SystemParameter();
					sp.setParamName(entry.getKey());
					sp.setParamValue(entry.getValue());
					dt.add(sp);
				}
				ActionContext.getContext().put("staticDr", dt);
				dictType = "static";
			}
			else{
				ActionContext.getContext().put("funcDr", rb);
				dictType = "func";
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取数据字典异常："+e.getLocalizedMessage());
		}
		ActionContext.getContext().put("dictType", dictType);
		ActionContext.getContext().put("bean", dr);
		return INPUT;
	}	
		
	private String delDsById(String drId) throws SystemException{
//		String retMsg = deeResourceManager.isQuoteByFlow(drId);
		String retMsg = "";
//		if(!"".equals(retMsg)){
//			return retMsg;
//		}
		deeResourceManager.deleteByDrId(drId);
		return retMsg;
	}
	
	/** 
	 * 批量字典删除
	 */
	public String deleteAll(){
		String[] arrId=ids.split(",");
//		String retMsg = "";
		String uuidMsg = "";
		for(String id:arrId){
			if(id == null || "".equals(id))
				continue;
//			String rMsg = "";
			try {
				if("201211151047".equals(id)){
					uuidMsg = "UUID系统函数";
					continue;
				}
				delDsById(id);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(),e);
				return ajaxForwardError("删除数据字典异常："+e.getLocalizedMessage());
			}
//			if(!"".equals(rMsg)){
//				retMsg += "".equals(retMsg)?rMsg:"，"+rMsg;
//			}
		}
//		if(!"".equals(retMsg)){
//			if(!"".equals(uuidMsg))
//				return ajaxForwardError("数据字典【"+retMsg+"】被引用和"+uuidMsg+"，不能被删除！");
//			return ajaxForwardError("数据字典【"+retMsg+"】被引用，不能被删除！");
//		}
		if(!"".equals(uuidMsg))
			return ajaxForwardError(uuidMsg+"，不能被删除！");
		return ajaxForwardSuccess(getText("msg.operation.success"));
	}
	
	/** 
	 * 数据源保存
	 */
	public String saveDict(){
		DeeResourceBean drb = new DeeResourceBean();
		DEEClient client = new DEEClient();
		try{
			setDRB(drb);
			String drId = drb.getResource_id();
			int templateId = Integer.parseInt(drb.getDeeResourceTemplate().getResource_template_id());
			String retMsg = chkVals(templateId);
			if(!"".equals(retMsg)){
				//数据校验
				return ajaxForwardError(retMsg);
			}
			setRB(templateId,drb);
			if(drId != null && !"".equals(drId)){
				//修改数据源
				if(deeResourceManager.isHasSameDisNameOnDict(drId, drb.getDis_name())){
					return ajaxForwardError("该字典名称已经存在，请更换！");
				}
				deeResourceManager.modifyDatasource(drb);
				GenerationCfgUtil.getInstance().generationMainFile(
						GenerationCfgUtil.getDEEHome());
				client.refreshContext();
				if("com.seeyon.v3x.dee.bean.StaticDictBean".equals(drb.getDr().getClass().getName())){
					EnumerateConfig ec = EnumerateConfig.getInstance();
					Properties propertie = ec.getPropertie();
					propertie = new Properties();
					try {
						FileInputStream inputFile = new FileInputStream(TransformFactory.getInstance()
								.getConfigFilePath("dictionary.properties"));
						propertie.load(new InputStreamReader(inputFile, DEEConstants.CHARSET_UTF8));
						inputFile.close();
					} catch (FileNotFoundException ex) {
						log.error("File read fail!"+ex.getMessage(),ex);
					} catch (IOException ex) {
						log.error("Load file error!"+ex.getMessage(),ex);
					}
					ec.setPropertie(propertie);
				}
				return ajaxForwardSuccess(getText("msg.operation.success"));
			}
			if(deeResourceManager.isHasSameDisNameOnDict(drId, drb.getDis_name())){
				return ajaxForwardError("该字典名称已经存在，请更换！");
			}
			//新增数据源
			deeResourceManager.saveDatasource(drb);
			GenerationCfgUtil.getInstance().generationMainFile(
					GenerationCfgUtil.getDEEHome());
			client.refreshContext();
			if("com.seeyon.v3x.dee.bean.StaticDictBean".equals(drb.getDr().getClass().getName())){
				EnumerateConfig ec = EnumerateConfig.getInstance();
				Properties propertie = ec.getPropertie();
				propertie = new Properties();
				try {
					FileInputStream inputFile = new FileInputStream(TransformFactory.getInstance()
							.getConfigFilePath("dictionary.properties"));
					propertie.load(new InputStreamReader(inputFile, DEEConstants.CHARSET_UTF8));
					inputFile.close();
				} catch (FileNotFoundException ex) {
					log.error("File read fail!"+ex.getMessage(),ex);
				} catch (IOException ex) {
					log.error("Load file error!"+ex.getMessage(),ex);
				}
				ec.setPropertie(propertie);
			}
			return ajaxForwardSuccess(getText("msg.operation.success"));
		}
//		catch(TransformException e){
//			String retMsg = "保存数据源出错：" + e.getMessage();
//			return ajaxForwardError(retMsg);
//		}
		catch(Throwable e){
			String retMsg = "保存字典出错：引擎刷新上下文异常--" + e.getMessage();
			log.error(retMsg,e);
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
			if("jdbc".equals(dictType)){
				tmpBean.setResource_template_id(DeeResourceEnum.JDBCDictionary.ordinal() + "");
			}
			else if("static".equals(dictType)){
				tmpBean.setResource_template_id(DeeResourceEnum.StaticDictionary.ordinal() + "");
			}
			else{
				tmpBean.setResource_template_id(DeeResourceEnum.SystemFunction.ordinal() + "");
				drb.setFunc_name(func_name);
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
		if(tmpId == DeeResourceEnum.JDBCDictionary.ordinal()){
			JDBCDictBean rb = new JDBCDictBean();
			rb.setDataSource(ref_id);
			rb.setTableName(tableName);
			rb.setKeyColumn(keyColumn);
			rb.setValueColumn(valueColumn);
			if(drb.getResource_id() == null || "".equals(drb.getResource_id())){
				drb.setResource_id(UuidUtil.uuid());
			}
			rb.setName(drb.getResource_id());//rb.setName(dis_name); //使用resource_id
			drb.setDr(rb);
		}
		else if(tmpId == DeeResourceEnum.StaticDictionary.ordinal()){
			StaticDictBean rb = new StaticDictBean();
			Map<String, String> map = new LinkedHashMap();
			if(dict != null && dict.size()>0){
				for(SystemParameter dsp:dict){
					map.put(dsp.getParamName(), dsp.getParamValue());
				}
			}
			rb.setName(dis_name);  //注意这里应该是引用名为dis_name
			rb.setMap(map);
			drb.setDr(rb);
		}
		else{
			SysFunctionBean rb = new SysFunctionBean();
			rb.setResource_para(resource_para);
			drb.setDr(rb);
		}
	}
	
	private String chkVals(int tmpId){
		String retMsg = "";
		if(dis_name == null || "".equals(dis_name)){
			retMsg = "名称不能为空！";
			return retMsg;
		}
		if(tmpId == DeeResourceEnum.JDBCDictionary.ordinal()){
			if(ref_id == null || "".equals(ref_id)){
				retMsg = "数据源不能为空！";
				return retMsg;
			}
			if(tableName == null || "".equals(tableName)){
				retMsg = "表名不能为空！";
				return retMsg;
			}
			if(keyColumn == null || "".equals(keyColumn)){
				retMsg = "枚举字段不能为空！";
				return retMsg;
			}
			if(valueColumn == null || "".equals(valueColumn)){
				retMsg = "枚举值不能为空！";
				return retMsg;
			}
		}
		else if(tmpId == DeeResourceEnum.StaticDictionary.ordinal()){
			if(dict == null || dict.size() < 1){
				retMsg = "枚举不能为空！";
				return retMsg;
			}
			Map<String, String> map = new HashMap();
			for(SystemParameter dsp:dict){
				map.put(dsp.getParamName(), dsp.getParamValue());
			}
			if(map.size() != dict.size()){
				retMsg = "枚举Key不能重复！";
				return retMsg;
			}
		}
		else{
			if(func_name == null || "".equals(func_name)){
				retMsg = "函数名不能为空！";
				return retMsg;
			}
			if(resource_para == null || "".equals(resource_para)){
				retMsg = "参数不能为空！";
				return retMsg;
			}
		}
		return retMsg;
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
	public List<SystemParameter> getDict() {
		return dict;
	}
	public void setDict(List<SystemParameter> dict) {
		this.dict = dict;
	}
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
	public String getResource_name() {
		return resource_name;
	}
	public void setResource_name(String resource_name) {
		this.resource_name = resource_name;
	}
	public String getDis_name() {
		return dis_name;
	}
	public void setDis_name(String dis_name) {
		this.dis_name = dis_name;
	}
	public String getDictType() {
		return dictType;
	}
	public void setDictType(String dictType) {
		this.dictType = dictType;
	}
	public String getRef_id() {
		return ref_id;
	}
	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getKeyColumn() {
		return keyColumn;
	}
	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}
	public String getValueColumn() {
		return valueColumn;
	}
	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}
	public String getFunc_name() {
		return func_name;
	}
	public void setFunc_name(String func_name) {
		this.func_name = func_name;
	}
	public String getResource_para() {
		return resource_para;
	}
	public void setResource_para(String resource_para) {
		this.resource_para = resource_para;
	}
	
}
