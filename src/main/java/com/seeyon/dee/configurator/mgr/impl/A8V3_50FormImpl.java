package com.seeyon.dee.configurator.mgr.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.A8FormMgr;
import com.seeyon.v3x.dee.datasource.JDBCDataSource;
import com.seeyon.v3x.dee.datasource.JDBCDataSource.ResultSetCallback;

import dwz.framework.exception.SystemException;

public class A8V3_50FormImpl implements A8FormMgr {

    private static final Log log = LogFactory.getLog(A8V3_50FormImpl.class);
    
    private static final String FLOW_SQL = "select 3 as id ,-1 as parent_id,'流程模版' as name,'no' as flag from v3x_org_account"
        + " union select id,3,name,'no' from v3x_org_account" 
        + " union select c.id,a.id as parent_id,c.name, 'no' from v3x_templete_category c left join v3x_org_account a on c.org_account_id=a.id where (c.type=4 or c.type=0)"
        + " union select id,category_id as parent_id,subject,'yes' from v3x_templete where category_type=4";
    
    private static final String BASE_SQL = "select 4 as id ,-1 as parent_id,'基础信息' as name,'no' as flag  from v3x_org_account"
        + " union select id,4,name ,'no' from v3x_org_account"
        + " union select c.id,a.id as parent_id,c.name ,'no' from v3x_templete_category c left join v3x_org_account a on c.org_account_id=a.id where (c.type=4 or c.type=0)"
        + " union select id,ref_apptypeid,appname ,'yes' from form_appmain where form_type=3";

    private static final String NEWS_SQL = "select 5 as id ,-1 as parent_id,'信息管理' as name,'no' as flag  from v3x_org_account"
        + " union select id,5,name ,'no' from v3x_org_account"
        + " union select c.id,a.id as parent_id,c.name ,'no' from v3x_templete_category c left join v3x_org_account a on c.org_account_id=a.id where (c.type=4 or c.type=0)"
        + " union select id,ref_apptypeid,appname ,'yes' from form_appmain where form_type=2";
    
    @Override
    public String getFlowSql() {
        return FLOW_SQL;
    }

    @Override
    public String getBaseSql() {
        return BASE_SQL;
    }

    @Override
    public String getNewsSql() {
        return NEWS_SQL;
    }

    @Override
    public List<TreeNode> getFlowFormList3(JDBCDataSource ds, 
            String dsId) throws SystemException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        List<TreeNode> targetTableList = new LinkedList<TreeNode>();
        String sql = "select id, body from v3x_templete";
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String parentId = rs.getString("id");
                String bodyXml = rs.getString("body");
                if (StringUtils.isNotBlank(bodyXml) && bodyXml.startsWith("<com")) {
                    Document doc = DocumentHelper.parseText(bodyXml);
                    // 获取根节点
                    Element rootElt = doc.getRootElement();
                    // 获取所有的table
                    Node formAPPElem = rootElt.selectSingleNode("//formApp");
                    if (formAPPElem != null) {
                        // 获取表单ID
                        String formID = formAPPElem.getText();
                        targetTableList.addAll(getFlowTables3(ds, formID, parentId, dsId));
                    }
                }
            }       
        } catch(Exception e) {
            SystemException err = new SystemException(e.getLocalizedMessage(), e);
            throw err;
        } finally {
            try{
                if (null != rs) {
                    rs.close();
                }
                if (null != stmt) {
                    stmt.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch(SQLException e) {
                SystemException err = new SystemException(e.getLocalizedMessage(), e);
                throw err;
            }
        }
        return targetTableList;
    }

    @Override
    public List<TreeNode> getNewsOrBaseTables(final String typeId, 
            JDBCDataSource ds, String formIds, final String dsId) throws Exception {
        String sql = "select id, datadefine from form_appmain where id in ("+formIds+")";
        
        return ds.executeQuery(sql, new ResultSetCallback<List<TreeNode>>() {
            public List<TreeNode> execute(ResultSet rs2) throws SQLException {
                String xml = "";
                String id = "";
                List<TreeNode> targetTableList = new LinkedList<TreeNode>();
                while (rs2.next()) {
                    id = rs2.getString(1);
                    xml = rs2.getString(2);
                    paraseNewsOrBaseTablesElement(typeId, xml, targetTableList, id, dsId);
                }
                rs2.close();
                return targetTableList;
            }
        });
    }
    
    @Override
    public List<TreeNode> getTemplateTableByFormIds(JDBCDataSource ds, String formId, final String tableName)
            throws Exception {
        String sql = "select id, datadefine from form_appmain where id = '"+formId+"'";
        
        return ds.executeQuery(sql, new ResultSetCallback<List<TreeNode>>() {
            public List<TreeNode> execute(ResultSet rs2) throws SQLException {
                String id = "";
                String xml = "";
                List<TreeNode> targetTableList = new LinkedList<TreeNode>();
                while (rs2.next()) {
                    id = rs2.getString(1);
                    xml = rs2.getString(2);
                    try {
                        paraseColsElement(xml, targetTableList, id, tableName);
                    } catch (DocumentException e) {
                        log.error(e.getMessage());
                    }
                }
                rs2.close();
                return targetTableList;
            }
        });
    }
    
    private List<TreeNode> getFlowTables3(JDBCDataSource ds, String formIds, final String parentId, final String dsId) throws Exception{
        String sql = "select id,datadefine from form_appmain where id in ("+formIds+")";
        
        return ds.executeQuery(sql, new ResultSetCallback<List<TreeNode>>() {
            public List<TreeNode> execute(ResultSet rs2) throws SQLException {
                String xml = "";
                String id = "";
                List<TreeNode> targetTableList = new LinkedList<TreeNode>();
                while (rs2.next()) {
                    id = rs2.getString(1);
                    xml = rs2.getString(2);
                    paraseFlowTablesElement3(xml, targetTableList, parentId, id, dsId);
                }
                rs2.close();
                return targetTableList;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void paraseFlowTablesElement3(String xml, List<TreeNode> targetTableList, String parentId, String formId, String dsId) {
        try {
            org.dom4j.Document doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> tablesElem = rootElt.selectNodes("/Define/DataDefine/TableList/Table");  // 读取所有的table
            String tabletype = "";
            TreeNode tn = null;
            for (Element singleTable:tablesElem) {
                tabletype = singleTable.attributeValue("tabletype");
                tn = new TreeNode();
                String tableTypeValue = "主表";
                if ("".equals(tabletype) || "master".equals(tabletype)) {
                } else {
                    tableTypeValue = "从表";
                }
                tn.setNodeName(tableTypeValue);
                tn.setNodeId(singleTable.attributeValue("name"));
                tn.setNodePId(parentId+"3");
                tn.setFormId(formId);
                tn.setNodeUrl(dsId);
                tn.setNodeType("4");
                tn.setHasDynChild("true");
                targetTableList.add(tn);
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
    
    /**
     * @description 解析表单内容从中获取表单表--基础信息、信息管理
     * @date 2012-09-28
     * @author dengxj
     */
    @SuppressWarnings("unchecked")
    private void paraseNewsOrBaseTablesElement(String typeId, 
            String xml, List<TreeNode> targetTableList, 
            String parentId, String dsId){
        try{
            org.dom4j.Document doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> tablesElem = rootElt.selectNodes("/Define/DataDefine/TableList/Table");  // 读取所有的table
            String tabletype = "";
            TreeNode tn = null;
            for (Element singleTable : tablesElem) {
                tabletype = singleTable.attributeValue("tabletype");
                tn = new TreeNode();
                String tableTypeValue = "主表";
                if ("".equals(tabletype) || "master".equals(tabletype)) {
                } else {
                    tableTypeValue = "从表";
                }
                tn.setNodeName(tableTypeValue);
                tn.setNodeId(singleTable.attributeValue("name"));
                tn.setNodePId(parentId+typeId);
                tn.setNodeUrl(dsId);
                tn.setFormId(parentId);
                tn.setNodeType("4");
                tn.setHasDynChild("true");
                targetTableList.add(tn);
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
    
    /**
     * 解析Elements，为根据表名查字段值所用
     * @param xml
     * @param targetTableList
     * @param formId
     * @param tableName
     * @throws DocumentException
     */
    @SuppressWarnings("unchecked")
    private void paraseColsElement(String xml, 
            List<TreeNode> targetTableList, String formId, 
            String tableName) throws DocumentException{
        Document doc = null;
        doc = DocumentHelper.parseText(xml);    // 将字符串转为XML
        Element rootElt = doc.getRootElement(); // 获取根节点
        Element tableElem = (Element) rootElt.selectSingleNode("/Define/DataDefine/TableList/Table[@name='"+ tableName.trim() + "']");  // 读取选择的表的信息
        if (tableElem == null) {
            return;
        }
        TreeNode col = null;
        String display = "";
        for (Element columnElem : (List<Element>)tableElem.selectNodes("FieldList/Field")) {
            col = new TreeNode();
            col.setNodeId(columnElem.attributeValue("name"));
            display = columnElem.attributeValue("display");
            
            if (StringUtils.isBlank(display)) {     //如果显示名称没有就用英文名称代替
                col.setNodeName(columnElem.attributeValue("name"));
            } else {
                col.setNodeName(display);
            }
            col.setNodePId(formId);
            col.setNodeUrl("");
            col.setNodeType("4");
            col.setHasDynChild("false");
            targetTableList.add(col);
        }
    }

}
