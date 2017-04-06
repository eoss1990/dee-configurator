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

import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.A8FormMgr;
import com.seeyon.v3x.dee.datasource.JDBCDataSource;
import com.seeyon.v3x.dee.datasource.JDBCDataSource.ResultSetCallback;

import dwz.framework.exception.SystemException;

public class A8V5_0FormImpl implements A8FormMgr {

    private static final Log log = LogFactory.getLog(A8V5_0FormImpl.class);
    
    private static final String FLOW_SQL = "select 3 as id ,-1 as parent_id,'流程模版' as name,'no' as flag from org_unit where type='Account'"
        + " union select id,3,name,'no' from org_unit where type='Account'"
        + " union select c.id,a.id as parent_id,c.name, 'no' from ctp_template_category c left join org_unit a on c.org_account_id=a.id where (c.TYPE=1 or c.TYPE=2)"
        + " union select id, category_id as parent_id, name, 'yes' from form_definition where form_type = '1' and use_flag = '1'";

    private static final String BASE_SQL = "select 4 as id ,-1 as parent_id,'基础信息' as name,'no' as flag  from org_unit where type='Account'"
        + " union select id,4,name ,'no' from org_unit where type='Account'"
        + " union select c.id,a.id as parent_id,c.name ,'no' from ctp_template_category c left join org_unit a on c.org_account_id=a.id where (c.TYPE=1 or c.TYPE=2)"
        + " union select id, category_id as parent_id, name, 'yes' from form_definition where form_type = '3' and use_flag = '1'";

    private static final String NEWS_SQL = "select 5 as id ,-1 as parent_id,'信息管理' as name,'no' as flag  from org_unit where type='Account'"
        + " union select id,5,name ,'no' from org_unit where type='Account'"
        + " union select c.id,a.id as parent_id,c.name ,'no' from ctp_template_category c left join org_unit a on c.org_account_id=a.id where (c.TYPE=1 or c.TYPE=2)"
        + " union select id, category_id as parent_id, name, 'yes' from form_definition where form_type = '2' and use_flag = '1'";

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
    public List<TreeNode> getFlowFormList3(JDBCDataSource ds, String dsId) throws SystemException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        List<TreeNode> targetTableList = new LinkedList<TreeNode>();
        String sql = "select ID from form_definition where use_flag = '1' and form_type = '1'";
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String formID = rs.getString("ID");
                if (StringUtils.isBlank(formID)) continue;
                sb.append(formID).append(",");
            }
            if (sb.length() < 1) return targetTableList;
            String formIDs = sb.toString().substring(0,sb.length()-1);
            targetTableList.addAll(getFlowTables3(ds, formIDs, "", dsId));
        } catch(Exception e) {
            SystemException err = new SystemException(e.getLocalizedMessage(), e);
            throw err;
        } finally {
            try{
                closeConn(conn,stmt,rs);
            } catch(SystemException e) {
                throw e;
            }
        }
        return targetTableList;
    }

    private void closeConn(Connection conn,Statement stmt,ResultSet rs) throws SystemException {
        try{
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch(SQLException e) {
            SystemException err = new SystemException(e.getLocalizedMessage(), e);
            throw err;
        }
    }
    @Override
    public List<TreeNode> getNewsOrBaseTables(final String typeId, 
            JDBCDataSource ds, String formIds, final String dsId) throws Exception {
        String sql = "select id, field_info from form_definition where use_flag = '1' and id in ("+formIds+")";

        return ds.executeQuery(sql, new ResultSetCallback<List<TreeNode>>() {
            public List<TreeNode> execute(ResultSet rs2) throws SQLException {
                List<TreeNode> targetTableList = new LinkedList<TreeNode>();
                
                while (rs2.next()) {
                    String id = rs2.getString(1);
                    String xml = rs2.getString(2);
                    paraseNewsOrBaseTablesElement(typeId, xml, targetTableList, id, dsId);
                }
                rs2.close();
                
                return targetTableList;
            }
        });
    }

    @Override
    public List<TreeNode> getTemplateTableByFormIds(JDBCDataSource ds, 
            String formId, final String tableName) throws Exception {
        String sql = "select id, field_info from form_definition where use_flag = '1' and id in ("+formId+")";

        return ds.executeQuery(sql, new ResultSetCallback<List<TreeNode>>() {
            public List<TreeNode> execute(ResultSet rs2) throws SQLException {
                List<TreeNode> targetTableList = new LinkedList<TreeNode>();

                while (rs2.next()) {
                    String id = rs2.getString(1);
                    String xml = rs2.getString(2);
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

    private List<TreeNode> getFlowTables3(JDBCDataSource ds,
            String formIds, final String parentId, final String dsId)
            throws Exception {
        String sql = "select id, field_info from form_definition where use_flag = '1' and id in ("+formIds+")";

        return ds.executeQuery(sql, new ResultSetCallback<List<TreeNode>>() {
            public List<TreeNode> execute(ResultSet rs2) throws SQLException {
                List<TreeNode> targetTableList = new LinkedList<TreeNode>();
                
                while (rs2.next()) {
                    String id = rs2.getString(1);
                    String xml = rs2.getString(2);
                    paraseFlowTablesElement3(xml, targetTableList, id, id, dsId);
                }
                rs2.close();
                
                return targetTableList;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void paraseFlowTablesElement3(String xml, 
            List<TreeNode> targetTableList, String parentId, 
            String formId, String dsId) {
        try {
            org.dom4j.Document doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement();                 // 获取根节点
            List<Element> tablesElem = rootElt.selectNodes("/TableList/Table");  // 读取所有的table
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
            e.printStackTrace();
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
            String parentId, String dsId) {
        try{
            org.dom4j.Document doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> tablesElem = rootElt.selectNodes("/TableList/Table");  // 读取所有的table
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
        Element tableElem = (Element) rootElt.selectSingleNode("/TableList/Table[@name='"+ tableName.trim() + "']");  // 读取选择的表的信息
        if (tableElem == null) {
            return;
        }
        List<Element> eles = (List<Element>)tableElem.selectNodes("FieldList/Field");
        for (Element columnElem : eles) {
            targetTableList.add(putTreeNode(columnElem.attributeValue("name"),columnElem.attributeValue("display"),formId,"4","false"));
        }
        if ("master".equals(tableElem.attributeValue("tabletype"))) {
            //主表中增加一项流程发起人登录名
            targetTableList.add(putTreeNode("senderLoginName", "流程发起人登录名", formId, "4", "false"));
        }
    }

    //设置节点
    private TreeNode putTreeNode(String nodeId,String nodeName,String pId,String nodeType,String hasDynChild){
        TreeNode col = new TreeNode();
        col.setNodeId(nodeId);
        if (StringUtils.isBlank(nodeName)) {     //如果显示名称没有就用英文名称代替
            col.setNodeName(nodeId);
        } else {
            col.setNodeName(nodeName);
        }
        col.setNodePId(pId);
        col.setNodeUrl("");
        col.setNodeType(nodeType);
        col.setHasDynChild(hasDynChild);
        return col;
    }
}
