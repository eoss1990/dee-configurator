package com.seeyon.dee.configurator.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;

public class SelectDSTreeAction extends BaseAction {

	/**
	 * 数据源树选择业务
	 */
	private static final long serialVersionUID = 2084504080956751901L;
	private final static Log log = LogFactory.getLog(SelectDSTreeAction.class);
	
	private String dsId;
	private String id;
	private String nId;
	private String nType;
	private String nForm;
	private String jsonData;  //返回业务子树json数据
	
    @Autowired
    @Qualifier("dsTreeManager")
	private DSTreeManager dsTreeManager;

    @Autowired
    @Qualifier("deeResourceManager")
	private DeeResourceManager deeResourceManager;
    
    
	public DSTreeManager getDsTreeManager() {
		return dsTreeManager;
	}

	public void setDsTreeManager(DSTreeManager dsTreeManager) {
		this.dsTreeManager = dsTreeManager;
	}
    
	
	public DeeResourceManager getDeeResourceManager() {
		return deeResourceManager;
	}

	public void setDeeResourceManager(DeeResourceManager deeResourceManager) {
		this.deeResourceManager = deeResourceManager;
	}
	
	public String selectds(){
//		dsId = "-534670898896024784";
		List<TreeNode> nList = null;
		
		String jData = "";
		try {
			DeeResourceBean dr = deeResourceManager.get(dsId);
			if(dr == null)
				return ajaxForwardError("获取到资源信息!");
			//获取NC
			nList = dsTreeManager.getNCAllTables();
			//获取组织机构
			nList.addAll(dsTreeManager.getA8ORGAllTables());
			
			if(Integer.parseInt(dr.getDeeResourceTemplate().getResource_template_id()) == DeeResourceEnum.A8MetaDatasource.ordinal()){
				//获取A8元数据
				nList.addAll(dsTreeManager.getA8MetaAllTables(dr));
			}
			else{
				//获取数据源
				nList.addAll(dsTreeManager.getDSAllTables(dr));
			}
			if(nList == null || nList.size() == 0)
				return ajaxForwardError("获取表节点失败！");
			jData = dsTreeManager.treeList2Json(nList);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取表异常："+e.getLocalizedMessage());
		}
		ActionContext.getContext().put("jData", jData);
		
		if(request.getParameter("type").equals("wirter"))
			return "writer";
		else
			return INPUT;
	}
	public String selectJdbcReaderDs(){
//		dsId = "-534670898896024784";
		List<TreeNode> nList = null;
		
		try {
			DeeResourceBean dr = deeResourceManager.get(dsId);
			if(dr == null)
				return ajaxForwardError("获取到资源信息!");
			//获取NC
			nList = dsTreeManager.getNCAllTables();
			//获取组织机构
			nList.addAll(dsTreeManager.getA8ORGAllTables());
			
			//获取数据库表
			nList.addAll(dsTreeManager.getDSAllTables(dr));
			
			if(Integer.parseInt(dr.getDeeResourceTemplate().getResource_template_id()) == DeeResourceEnum.A8MetaDatasource.ordinal()){
				//获取A8元数据
				nList.addAll(dsTreeManager.getA8MetaAllTables(dr));
			}
			if(nList == null || nList.size() == 0)
				return ajaxForwardError("获取表节点失败！");
			jsonData = dsTreeManager.treeList2Json(nList);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取表异常："+e.getLocalizedMessage());
		}
		return ajaxForwardSuccess("获节点成功！");
	}
	
	public String selectOneDs(){
		List<TreeNode> nList = null;
		
		try {
			if("2".equals(dsId)){
				//获取NC
				nList = dsTreeManager.getNCAllTables();
			}
			else if("3".equals(dsId)){
				//获取组织机构
				nList = dsTreeManager.getA8ORGAllTables();
			}
			else{
				DeeResourceBean dr = deeResourceManager.get(dsId);
				if(dr == null)
					return ajaxForwardError("获取到资源信息!");
				//获取数据源
				nList = dsTreeManager.getDSAllTables(dr);
				if(Integer.parseInt(dr.getDeeResourceTemplate().getResource_template_id()) == DeeResourceEnum.A8MetaDatasource.ordinal()){
					//获取A8元数据
					nList.addAll(dsTreeManager.getA8MetaAllTables(dr));
				}
			}
			if(nList == null || nList.size() == 0)
				return ajaxForwardError("获取表节点失败！");
			jsonData = dsTreeManager.treeList2Json(nList);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取表异常："+e.getLocalizedMessage());
		}
		return ajaxForwardSuccess("获节点成功！");
	}
	
	public String selectMappingDs(){
		try {
			ActionContext.getContext().put("dsList", deeResourceManager.findAllJdbcDs());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			return ajaxForwardError("获取数据源异常："+e.getLocalizedMessage());
		}
		return INPUT;
	}
	
	public String selectMultiMappingDs(){
		try {
			ActionContext.getContext().put("dsList", deeResourceManager.findAllJdbcDs());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取数据源异常："+e.getLocalizedMessage());
		}
		return INPUT;
	}
	
	//nType 类别  1：数据库;2:NC库;3：组织机构;4：A8元数据
	public String selectSonDs(){
//		dsId = "-534670898896024784";
		List<TreeNode> nList = null;
		DeeResourceBean dr = null;
		try{
			switch(Integer.parseInt(nType)){
			case 1:
				dr = deeResourceManager.get(nId);
				if(dr == null)
					return ajaxForwardError("获取到资源信息!");
				nList = dsTreeManager.getDSAllColumns(dr, id);
				break;
			case 2:
				nList = dsTreeManager.getNCAllColumns(id);
				break;
			case 3:
				nList = dsTreeManager.getA8ORGAllColumns(id);
				break;
			case 4:
				dr = deeResourceManager.get(nId);
				if(dr == null)
					return ajaxForwardError("获取到资源信息!");
				nList = dsTreeManager.getA8MetaAllColumns(dr, nForm,id);
				break;
			}
			
			if(nList == null || nList.size() == 0)
				return ajaxForwardError("获取没有子节点！");
			jsonData = dsTreeManager.treeList2Json(nList);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取子节点异常："+e.getLocalizedMessage());
		}
		return ajaxForwardSuccess("获取子节点成功！");
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNId() {
		return nId;
	}

	public void setNId(String nId) {
		this.nId = nId;
	}

	public String getNType() {
		return nType;
	}

	public void setNType(String nType) {
		this.nType = nType;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getNForm() {
		return nForm;
	}

	public void setNForm(String nForm) {
		this.nForm = nForm;
	}	
}
