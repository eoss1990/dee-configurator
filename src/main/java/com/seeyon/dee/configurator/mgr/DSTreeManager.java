package com.seeyon.dee.configurator.mgr;

import java.util.List;

import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.exception.SystemException;

public interface DSTreeManager extends BaseMgr<DeeResourceBean,String> {
	/*********根据数据源获取表以及字段 ***************/
	public List<TreeNode> getDSAllTables(DeeResourceBean dr) throws SystemException;
	public List<TreeNode> getDSAllColumns(DeeResourceBean dr,String tableName) throws SystemException;
	public List<TreeNode> getNCAllTables() throws SystemException;
	public List<TreeNode> getNCAllColumns(String tableName) throws SystemException;
	public List<TreeNode> getA8ORGAllTables() throws SystemException;
	public List<TreeNode> getA8ORGAllColumns(String tableName) throws SystemException;
	public List<DeeResourceBean> getA8MetaAllDbs() throws SystemException;
	public List<TreeNode> getA8MetaAllTables(DeeResourceBean dr) throws SystemException;
	public List<TreeNode> getA8MetaAllColumns(DeeResourceBean dr,String formId, String tableName) throws SystemException;
	public String treeList2Json(List<TreeNode> tnList) throws SystemException;
}
