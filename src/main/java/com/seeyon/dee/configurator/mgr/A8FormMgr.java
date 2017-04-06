package com.seeyon.dee.configurator.mgr;

import java.util.List;

import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.v3x.dee.datasource.JDBCDataSource;

import dwz.framework.exception.SystemException;

public interface A8FormMgr {

    /**
     * 获取流程模板的sql
     * @return
     */
    public String getFlowSql();
    
    /**
     * 获取基础信息的sql
     * @return
     */
    public String getBaseSql();
    
    /**
     * 获取信息管理的sql
     * @return
     */
    public String getNewsSql();
    
    /**
     * 获取流程表单列表
     * @param ds
     * @param dsId
     * @return
     * @throws SystemException
     */
    public List<TreeNode> getFlowFormList3(JDBCDataSource ds, 
            String dsId) throws SystemException;
    
    /**
     * 获取基础信息或信息管理列表
     * @param typeId
     * @param ds
     * @param formIds
     * @param dsId
     * @return
     * @throws Exception
     */
    public List<TreeNode> getNewsOrBaseTables(final String typeId,
            JDBCDataSource ds, String formIds, final String dsId) throws Exception;
    
    /**
     * 根据表名，获取字段
     * @param ds
     * @param formId
     * @param tableName
     * @return
     * @throws Exception
     */
    public List<TreeNode> getTemplateTableByFormIds(JDBCDataSource ds,
            String formId, final String tableName) throws Exception;
}
