package com.seeyon.dee.configurator.mgr.impl;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.a8version.A8VersionConstants;
import com.seeyon.v3x.dee.common.a8version.A8VersionManager;
import com.seeyon.v3x.dee.resource.DbDataSource;
import dwz.framework.util.DEEClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.dee.configurator.dao.DeeResourceDao;
import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.A8FormMgr;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.v3x.dee.TransformFactory;
import com.seeyon.v3x.dee.common.base.util.DBUtil;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.common.db.resource.util.SourceUtil;
import com.seeyon.v3x.dee.datasource.JDBCDataSource;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.mgr.impl.BaseMgrImpl;
import dwz.framework.exception.SystemException;
import com.seeyon.v3x.dee.util.DataChangeUtil;

@Service("dsTreeManager")
public class DSTreeManagerImpl extends BaseMgrImpl<DeeResourceBean,String> implements
		DSTreeManager {
    private static final Log log = LogFactory.getLog(DSTreeManagerImpl.class);

	private DeeResourceDao deeResourceDao;
	
	private static final String NCFILENAME = "NCTable.xml"; // 需要写入表信息的xml文件
	private static final String A8ORGFILENAME = "A8OrgTable.xml"; // 需要写入表信息的xml文件
	
	//nodeType 类别  1：数据库;2:NC库;3：组织机构;4：A8元数据
    @Autowired
    @Qualifier("deeResourceDao")
	public void setBaseDao(BaseDao<DeeResourceBean, String> baseDao) {
		// TODO Auto-generated method stub
        super.baseDao = baseDao;
        this.deeResourceDao = (DeeResourceDao)baseDao;
	}

	/**
	 * 根据数据源Id获取所有表
	 */
	@Override
	public List<TreeNode> getDSAllTables(DeeResourceBean dr) throws SystemException {
		// TODO Auto-generated method stub
//		DeeResourceBean dr = deeResourceDao.get(dsId);
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		ResultSet rs2 = null;
		ResultSet rs1 = null;
		ResultSet rs3 = null;
		List<TreeNode> tableList = new LinkedList<TreeNode>();
		try{
			if(dr == null) {
				return null;
			}
			DbDataSource dataSource= DEEClientUtil.lookupDbDataSource(dr.getResource_name());

			conn = dataSource.getConnection();
			st = conn.createStatement();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String dbName = dbMetaData.getDatabaseProductName();
			String dsName = conn.getCatalog();

			if(dsName == null || "Oracle".equals(dbName)){
				//获取不到数据库名，这里指oracle数据库类型,通过urljdbc:oracle:thin:@10.5.4.100:1521:ORCL
				String url = dbMetaData.getURL()==null?"":dbMetaData.getURL();
				String[] newUrl = url.split(":");
				dsName = newUrl[newUrl.length-1];

				if(dsName.contains("/")){
					String[] urlBackup = dsName.split("/");
					dsName = urlBackup[urlBackup.length-1];
				}
			}
			//ds
			String parentId = UuidUtil.uuid();
			TreeNode dbNode = new TreeNode();
			String driver = parentId + ":"+ conn.getMetaData().getDriverName();
			dbNode.setNodePId("-1");
			dbNode.setNodeId(driver);
			dbNode.setNodeName(dr.getDis_name());
			dbNode.setNodeType("1");
			dbNode.setHasDynChild("true");
			dbNode.setNodeNoCheck("true");
			tableList.add(dbNode);

			String sonId = UuidUtil.uuid();
			if(!"DM DBMS".equals(dbName) && !dbName.contains("DB2")){
				dbNode = new TreeNode();
				dbNode.setNodePId(parentId);
				dbNode.setNodeId(sonId);
				dbNode.setNodeName(dsName);
				dbNode.setNodeType("1");
				dbNode.setHasDynChild("true");
				dbNode.setNodeNoCheck("true");
				tableList.add(dbNode);
			}

			//如果是oracle数据库，改用查询方式获取该用户下的所有表，排除系统表。
			if( "Oracle".equals(dbName)){
				String userName = dbMetaData.getUserName();
				String sql = "select 1 as id,2 as name,table_name from All_tables where owner = '"+userName.toUpperCase()+"'";
				rs = st.executeQuery(sql);
			}else if("DM DBMS".equals(dbName) || dbName.contains("DB2") ){
				rs2 = dbMetaData.getSchemas();
			}
			else{
				rs = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
			}
			String tableName ="";
			List<String> modelList = new ArrayList<String>();
			List<String> filter = new ArrayList<String>();
			List<String> filterMode = new ArrayList<String>();

			//构建DB2树结构
			if(dbName.contains("DB2")){
				//List<TreeNode> db2List = buildDb2Tree(rs2,dataSource,tableList);
				try{
					while(rs2.next()){
						String modelName = rs2.getString(1);
						modelList.add(modelName);
						rs1 = dbMetaData.getTables(conn.getCatalog(), modelName, null, new String[]{"TABLE"});
						while (rs1.next()){
							if(rs1.getString(3) != null || !"".equals(rs1.getString(3))){
								filter.add(modelName);
							}
						}
					}
					for (int i = 0;i<filter.size();i++){
						if(!filter.get(i).equals("SYSTOOLS") &&!filterMode.contains(filter.get(i))){
							filterMode.add(filter.get(i));
						}
					}
					int i = 0;
					for (String modelName : filterMode) {
						++i;
						String GrandsonId = UuidUtil.uuid();
						String typeName = "";
						dbNode = new TreeNode();
						typeName = modelName;
						dbNode.setNodePId(parentId);
						dbNode.setNodeId(GrandsonId);
						dbNode.setNodeName(typeName);
						dbNode.setNodeType("1");
						dbNode.setHasDynChild("true");
						dbNode.setNodeNoCheck("true");
						tableList.add(dbNode);
						rs3 = dbMetaData.getTables(conn.getCatalog(), modelName, null, new String[]{"TABLE"});
						while (rs3.next()){
							dbNode = new TreeNode();
							tableName = rs3.getString(3);
							dbNode.setNodePId(GrandsonId);
							dbNode.setNodeId(tableName);
							dbNode.setNodeName(tableName);
							dbNode.setNodeUrl(dr.getResource_id());
							dbNode.setNodeType("1");
							dbNode.setHasDynChild("true");
							tableList.add(dbNode);
						}
					}
				}catch(Exception e){
					throw new SystemException(e);
				}
			}//构建达梦数据库树结
			else if("DM DBMS".equals(dbName)){
				while(rs2.next()){
					String modelName = rs2.getString(1);
					modelList.add(modelName);
					rs1 = dbMetaData.getTables(conn.getCatalog(), modelName, null, new String[]{"TABLE"});
					while (rs1.next()){
						if(rs1.getString(3) != null || !"".equals(rs1.getString(3))){
							filter.add(modelName);
						}
					}
				}
				for (int i = 0;i<filter.size();i++){
					if(!filter.get(i).equals("SYSDBA") &&!filterMode.contains(filter.get(i))){
						filterMode.add(filter.get(i));
					}
				}
				int i = 0;
				for (String modelName : filterMode) {
					++i;
					String GrandsonId = UuidUtil.uuid();
					String typeName = "";
					dbNode = new TreeNode();
					typeName = modelName;
					dbNode.setNodePId(parentId);
					dbNode.setNodeId(GrandsonId);
					dbNode.setNodeName(typeName);
					dbNode.setNodeType("1");
					dbNode.setHasDynChild("true");
					dbNode.setNodeNoCheck("true");
					tableList.add(dbNode);
					rs3 = dbMetaData.getTables(conn.getCatalog(), modelName, null, new String[]{"TABLE"});
					while (rs3.next()){
						dbNode = new TreeNode();
						tableName = rs3.getString(3);
						dbNode.setNodePId(GrandsonId);
						dbNode.setNodeId(tableName);
						dbNode.setNodeName(tableName);
						dbNode.setNodeUrl(dr.getResource_id());
						dbNode.setNodeType("1");
						dbNode.setHasDynChild("true");
						tableList.add(dbNode);
					}
				}

			}else{
				while(rs.next()){
					dbNode = new TreeNode();
					tableName = rs.getString(3);
					dbNode.setNodePId(sonId);
					dbNode.setNodeId(tableName);
					dbNode.setNodeName(tableName);
					dbNode.setNodeUrl(dr.getResource_id());
					dbNode.setNodeType("1");
					dbNode.setHasDynChild("true");
					tableList.add(dbNode);
				}


			}

		}
		catch(Exception e){
			throw new SystemException(e);
		}
		finally{
			try{
				if (null != rs) {
					rs.close();
				}
				if	(null != rs1){
					rs1.close();
				}
				if	(null != rs2){
					rs2.close();
				}
				if	(null != rs3){
					rs3.close();
				}
				if (null != st) {
					st.close();
				}
				if (null != conn) {
					conn.close();
				}
			}
			catch(SQLException e){
				throw new SystemException(e);
			}
		}
		return tableList;
	}

	/** 
	 * 根据数据源Id和表名获取该表所有字段
	 */
	@Override
	public List<TreeNode> getDSAllColumns(DeeResourceBean dr, String tableName)
			throws SystemException {
		// TODO Auto-generated method stub
//		DeeResourceBean dr = deeResourceDao.get(dsId);
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		boolean db2 = false;
		List<TreeNode> colList = new LinkedList<TreeNode>();
		try{
			if(dr == null)
				return null;
			DbDataSource ds = DEEClientUtil.lookupDbDataSource(dr.getResource_name());
			
			conn = ds.getConnection();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String dbName = dbMetaData.getDatabaseProductName();
			String dsName = conn.getCatalog();
			if(dsName == null || "Oracle".equals(dbName)){
				//dsName为空的时候可能是oracle,也可能是达梦，也可能是db2
				if(dbName.contains("DB2")){
					String[] schema = tableName.split(":");
					db2 = true;
					st = conn.createStatement();
					String sql = "select NAME from sysibm.syscolumns where TBCREATOR='"+ schema[1] +"' and tbname='"+schema[0]+"'";
					rs = st.executeQuery(sql);
				}else{
					st = conn.createStatement();
					String sql = "select COLUMN_NAME from USER_TAB_COLUMNS where TABLE_NAME='"+tableName+"'";
					rs = st.executeQuery(sql);
				}

			}
			else
				rs = dbMetaData.getColumns(null, null, tableName, null);
			//ds
			TreeNode dbNode = null;
			String colName = "";

			if(db2){
				while(rs.next()){
					dbNode = new TreeNode();
					colName = rs.getString("NAME");
					dbNode.setNodePId(tableName);
					dbNode.setNodeId(colName);
					dbNode.setNodeName(colName);
					dbNode.setNodeUrl("");
					dbNode.setNodeType("1");
					dbNode.setHasDynChild("false");
					colList.add(dbNode);
				}
			}else {
				while(rs.next()){
					dbNode = new TreeNode();
					colName = rs.getString("COLUMN_NAME");
					dbNode.setNodePId(tableName);
					dbNode.setNodeId(colName);
					dbNode.setNodeName(colName);
					dbNode.setNodeUrl("");
					dbNode.setNodeType("1");
					dbNode.setHasDynChild("false");
					colList.add(dbNode);
				}
			}

		}
		catch(Exception e){
			SystemException err = new SystemException(e.getLocalizedMessage());
			throw err;
		}
		finally{
			try{
				if (null != rs) {
					rs.close();
				}
				if (null != st) {
					st.close();
				}
				if (null != conn) {
					conn.close();
				}
				}
				catch(SQLException e){
					SystemException err = new SystemException(e.getLocalizedMessage());
					throw err;
				}
		}
		return colList;
	}

	/** 
	 * 将TreeNode转换成json
	 */
	@Override
	public String treeList2Json(List<TreeNode> tnList) throws SystemException {
		// TODO Auto-generated method stub
		JSONObject jObj = null;
		JSONArray jsList = new JSONArray();
        String jsonData = ""; 
        for(TreeNode tNode:tnList){
        	jObj = new JSONObject();
        	jObj.put("id", tNode.getNodeId());
        	jObj.put("pId", tNode.getNodePId());
        	jObj.put("name", tNode.getNodeName());
        	jObj.put("nId", tNode.getNodeUrl()==null?"":tNode.getNodeUrl());
        	if(tNode.getFormId() != null && !"".equals(tNode.getFormId()))
            	jObj.put("nForm", tNode.getFormId());
        	jObj.put("nType", tNode.getNodeType());
        	jObj.put("isParent", tNode.getHasDynChild());
        	if("true".equals(tNode.getNodeNoCheck()))
        		jObj.put("nocheck", "true");
        	jsList.add(jObj);
        }
        jsonData = jsList.toString();
//        System.out.println(jsonData);  
        return jsonData;  
	}

	/**
	 * @description 获取org数据库表描述文件地址，默认在dee_home下，如果没有，则在web工程的根目录/config下寻找
	 * @date 2012-11-6
	 * @author dkywolf
	 * @return
	 */
	private String getConfigPath(String fileName){
		String filePath = TransformFactory.getInstance().getHomeDirectory()+File.separator+fileName; // 需要写入表信息的xml文件
		File f = new File(filePath);
		if(!f.exists()){
			return DataChangeUtil.getRealPath("/") + "config/"+fileName;
		}
		return filePath;
    }

	/**
	 * @description 获取NC下所有表
	 * @date 2013-5-15
	 * @author dkywolf
	 * @return
	 */
	@Override
	public List<TreeNode> getNCAllTables() throws SystemException {
		// TODO Auto-generated method stub
		   List<TreeNode> tableList = new LinkedList<TreeNode>();
		   try {
			   File file = new File(this.getConfigPath(NCFILENAME));
			   SAXReader reader = new SAXReader();
			   if (file.exists()) {
				   Document document = reader.read(file);// 读取XML文件
				   Element rootElt = document.getRootElement();// 得到根节点
				   List<Element> tablesElementList = null;
				   tablesElementList = (List<Element>) rootElt.selectNodes("/Metadata/Tables");
				   TreeNode rootNode = new TreeNode();
				   rootNode.setNodeId(rootElt.attributeValue("name"));
				   rootNode.setNodeName(rootElt.attributeValue("display"));
				   rootNode.setNodePId("-1");
				   rootNode.setNodeUrl("");
				   rootNode.setNodeType("2");
				   rootNode.setHasDynChild("true");
				   rootNode.setNodeNoCheck("true");
				   tableList.add(rootNode);
				   TreeNode tables = null;
				   TreeNode subTables = null;
				   for (Element tablesElem : tablesElementList) {
					   tables = new TreeNode();
					   String tableName = tablesElem.attributeValue("name");
				       tables.setNodeId(tableName);
				       tables.setNodeName(tablesElem.attributeValue("display"));
				       tables.setNodePId(rootElt.attributeValue("name"));
				       tables.setNodeUrl("");
				       tables.setNodeType("2");
				       tables.setHasDynChild("true");
				       tables.setNodeNoCheck("true");
				       // 将tables节点解析成普通的table存放到list中
				       tableList.add(tables);
				       Element subElem = (Element) rootElt.selectSingleNode("/Metadata/Tables[@name='"+ tableName.trim() + "']");
				       // 判断为空就跳出该循环
				       if (subElem == null) {
				    	   continue;
				       }
				       for (Element subTableElem : (List<Element>) subElem.selectNodes("Table")) {
				    	   subTables = new TreeNode();
				    	   String tableTypeValue = "主表";
				    	   String tableType = subTableElem.attributeValue("tabletype");
				    	   if("".equals(tableType) || "master".equals(tableType)){
				    	   }else{
				    		   tableTypeValue = "从表";
				    	   }
				    	   subTables.setNodeName(tableTypeValue);
				    	   subTables.setNodeId(subTableElem.attributeValue("name"));
				    	   subTables.setNodePId(tableName);
				    	   subTables.setNodeUrl("");
				    	   subTables.setNodeType("2");
				    	   subTables.setHasDynChild("true");
				    	   tableList.add(subTables);
				       }
				   }
			   }
		   } catch (Exception e) {
				throw new SystemException("解析" + this.getConfigPath(NCFILENAME) + "文件出错了：", e);
		   }
		   return tableList;
	}

	/**
	 * @description 获取组织机构下所有表
	 * @date 2013-5-15
	 * @author dkywolf
	 * @return
	 */
	@Override
	public List<TreeNode> getA8ORGAllTables() throws SystemException {
		// TODO Auto-generated method stub
		   List<TreeNode> tableList = new LinkedList<TreeNode>();
		   try {
			   File file = new File(this.getConfigPath(A8ORGFILENAME));
			   SAXReader reader = new SAXReader();
			   if (file.exists()) {
				   Document document = reader.read(file);// 读取XML文件
				   Element rootElt = document.getRootElement();// 得到根节点
				   List<Element> tablesElementList = null;
				   tablesElementList = (List<Element>) rootElt.selectNodes("/Metadata/Tables");
				   TreeNode rootNode = new TreeNode();
				   rootNode.setNodeId(rootElt.attributeValue("name"));
				   rootNode.setNodeName(rootElt.attributeValue("display"));
				   rootNode.setNodePId("-1");
				   rootNode.setNodeUrl("");
				   rootNode.setNodeType("3");
				   rootNode.setHasDynChild("true");
				   rootNode.setNodeNoCheck("true");
				   tableList.add(rootNode);
				   TreeNode tables = null;
				   TreeNode subTables = null;
				   for (Element tablesElem : tablesElementList) {
					   tables = new TreeNode();
					   String tableName = tablesElem.attributeValue("name");
				       tables.setNodeId(tableName);
				       tables.setNodeName(tablesElem.attributeValue("display"));
				       tables.setNodePId(rootElt.attributeValue("name"));
				       tables.setNodeUrl("");
				       tables.setNodeType("3");
				       tables.setHasDynChild("true");
				       tables.setNodeNoCheck("true");
				       // 将tables节点解析成普通的table存放到list中
				       tableList.add(tables);
				       Element subElem = (Element) rootElt.selectSingleNode("/Metadata/Tables[@name='"+ tableName.trim() + "']");
				       // 判断为空就跳出该循环
				       if (subElem == null) {
				    	   continue;
				       }
				       for (Element subTableElem : (List<Element>) subElem.selectNodes("Table")) {
				    	   subTables = new TreeNode();
				    	   subTables.setNodeName(subTableElem.attributeValue("display"));
				    	   subTables.setNodeId(subTableElem.attributeValue("name"));
				    	   subTables.setNodePId(tableName);
				    	   subTables.setNodeUrl("");
				    	   subTables.setNodeType("3");
				    	   subTables.setHasDynChild("true");
				    	   tableList.add(subTables);
				       }
				   }
			   }
		   } catch (Exception e) {
				throw new SystemException("解析" + this.getConfigPath(A8ORGFILENAME) + "文件出错了：", e);
		   }
		   return tableList;
	}

	@Override
	public List<TreeNode> getA8MetaAllTables(DeeResourceBean dr) throws SystemException {
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		List<TreeNode> targetTableList = new LinkedList<TreeNode>();
		try{
			if (dr == null) {
				return null;
			}

	        DbDataSource checkDS = DEEClientUtil.lookupDbDataSource(dr.getResource_name());
	        // 获取A8版本
	        String version = DBUtil.getA8VersionStr(checkDS.getConnection());
	        // 根据A8版本，获取A8表单管理器
	        A8FormMgr a8FormMgr = getA8FormMgrByVersion(version);
	        String flowSql = a8FormMgr.getFlowSql();
	        String baseSql = a8FormMgr.getBaseSql();
	        String newsSql = a8FormMgr.getNewsSql();

            Properties properties = new Properties();
            String path = this.getClass().getResource("/TemplateResource_zh_CN.properties").getPath();
            properties.load(new FileInputStream(path));

	        JDBCDataSource ds = (JDBCDataSource)DEEClientUtil.lookupDbDataSource(dr.getResource_name());
			
			conn = ds.getConnection();
			st = conn.createStatement();
			String flag = "";
			List<String> templateIdList = new LinkedList<String>();
			// 流程
			rs = st.executeQuery(flowSql);
			String templateId = "";
			String parentId = "";
			while (rs.next()) {
				TreeNode tn = new TreeNode();
				templateId = rs.getString("id");
				parentId = rs.getString("parent_id");
				if (parentId == null || "".equals(parentId.trim()))
					continue;
				tn.setNodeId(templateId+"3");
				tn.setNodePId("-1".equals(parentId)?"-1":parentId+"3");
				tn.setNodeName(getPropertiesValue(properties, rs.getString("name")));
				tn.setNodeUrl(dr.getResource_id());
				tn.setNodeType("4");
				flag = rs.getString("flag");
				if ("yes".equals(flag)) {
					//集合所有有表单的模版id
					templateIdList.add(templateId);
					tn.setHasDynChild("true");
				}
				tn.setNodeNoCheck("true");
				targetTableList.add(tn);
			}
			if (templateIdList.size() > 0) {
				//根据流程模版id集合获取所有对应流程模版下面的表单表
				List<TreeNode> targetTableList1 = a8FormMgr.getFlowFormList3(ds, dr.getResource_id());
				//将两个集合，组成一个List<TreeNode>为一棵树展现
				targetTableList.addAll(targetTableList1);
			}
			// 基础信息
			rs = st.executeQuery(baseSql);
			templateIdList = new LinkedList<String>();
			while (rs.next()) {
				TreeNode tn = new TreeNode();
				templateId = rs.getString("id");
				parentId = rs.getString("parent_id");
				if (parentId == null || "".equals(parentId.trim()))
					continue;
				tn.setNodeId(templateId+"4");
				tn.setNodePId("-1".equals(parentId)?"-1":parentId+"4");
				tn.setNodeName(getPropertiesValue(properties, rs.getString("name")));
				tn.setNodeUrl(dr.getResource_id());
				tn.setNodeType("4");
				flag = rs.getString("flag");
				if ("yes".equals(flag)) {
					//集合所有有表单的模版id
					templateIdList.add(templateId);
					tn.setHasDynChild("true");
				}
				tn.setNodeNoCheck("true");
				targetTableList.add(tn);
			}
			if (templateIdList.size()>0) {
				//根据基础信息或者信息管理下面的表单表
				List<TreeNode> targetTableList1 = a8FormMgr.getNewsOrBaseTables("4",ds,templateIdList.toString().replace("[", "").replace("]", ""),dr.getResource_id());
				//将两个集合，组成一个List<TreeNode>为一棵树展现
				targetTableList.addAll(targetTableList1);
			}
			// 信息管理
			rs = st.executeQuery(newsSql);
			templateIdList = new LinkedList<String>();
			while (rs.next()) {
				TreeNode tn = new TreeNode();
				templateId = rs.getString("id");
				parentId = rs.getString("parent_id");
				if (parentId == null || "".equals(parentId.trim()))
					continue;
				tn.setNodeId(templateId+"5");
				tn.setNodePId("-1".equals(parentId)?"-1":parentId+"5");
				tn.setNodeName(getPropertiesValue(properties, rs.getString("name")));
				flag = rs.getString("flag");
				tn.setNodeUrl(dr.getResource_id());
				tn.setNodeType("4");
				if ("yes".equals(flag)) {
					//集合所有有表单的模版id
					templateIdList.add(templateId);
					tn.setHasDynChild("true");
				}
				tn.setNodeNoCheck("true");
				targetTableList.add(tn);
			}
			if (templateIdList.size()>0) {
				//根据基础信息或者信息管理下面的表单表
				List<TreeNode> targetTableList1 = a8FormMgr.getNewsOrBaseTables("5",ds,templateIdList.toString().replace("[", "").replace("]", ""),dr.getResource_id());
				//将两个集合，组成一个List<TreeNode>为一棵树展现
				targetTableList.addAll(targetTableList1);
			}
		} catch (Exception e) {
			SystemException err = new SystemException(e.getLocalizedMessage());
			throw err;
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != st) {
				    st.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				SystemException err = new SystemException(e.getLocalizedMessage());
				throw err;
			}
		}
		return targetTableList;
	}

	@Override
    public List<DeeResourceBean> getA8MetaAllDbs() throws SystemException {
	    String hql = "from DeeResourceBean db where db.deeResourceTemplate.resource_template_id=? order by db.create_time desc";
	    Object[] params = {DeeResourceEnum.A8MetaDatasource.ordinal()+""};
	    return deeResourceDao.listAll(hql, params);
    }

	@Override
	public List<TreeNode> getNCAllColumns(String tableName)
			throws SystemException {
		// TODO Auto-generated method stub
		try{
			return getFieldsByTablefromXML(tableName,NCFILENAME);
		}
		catch(Exception e){
			SystemException err = new SystemException(e.getLocalizedMessage());
			throw err;
		}
	}

	@Override
	public List<TreeNode> getA8ORGAllColumns(String tableName)
			throws SystemException {
		// TODO Auto-generated method stub
		try{
			return getFieldsByTablefromXML(tableName,A8ORGFILENAME);
		}
		catch(Exception e){
			SystemException err = new SystemException(e.getLocalizedMessage());
			throw err;
		}
	}
	/**
	 * @description 根据一个数据表名从xml文件获取该表所有的字段
	 * @date 2012-11-06
	 * @author dkywolf
	 * @param filePath
	 *            数据库描述文件路径
	 * @param tablename
	 *            表名
	 * @return list
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<TreeNode> getFieldsByTablefromXML(String tableName,String fileType) throws Exception {
		File file = new File(this.getConfigPath(fileType));
		SAXReader reader = new SAXReader();
		List<TreeNode> list = new LinkedList<TreeNode>();
		if (file.exists()) {
			Document document = reader.read(file);// 读取XML文件
			Element rootElt = document.getRootElement();// 得到根节点
			Element tableElem = (Element) rootElt.selectSingleNode("/Metadata/Tables/Table[@name='"+ tableName.trim() + "']");
			if (tableElem == null) {
				return null;
			}
			String display = "";
			TreeNode col = null;
			for (Element columnElem : (List<Element>) tableElem.selectNodes("Field")) {
				col = new TreeNode();
	 			 col.setNodeId(columnElem.attributeValue("name"));
	 			 display=columnElem.attributeValue("display");
	 			 if(display==null||"".equals(display)){ //如果显示名称没有就用英文名称代替
	 				col.setNodeName(columnElem.attributeValue("name"));
	 			 }else{
	  				col.setNodeName(display);
	 			 }
	 			 col.setNodePId(tableName);
	 			 col.setNodeUrl("");
	 			 col.setNodeType(NCFILENAME.equals(fileType)?"2":"3");
	 			 col.setHasDynChild("false");
				list.add(col);
			}
		}
		return list;
	}

	/**
     * 根据表获取字段
     */
	@Override
	public List<TreeNode> getA8MetaAllColumns(DeeResourceBean dr,String formId, String tableName)
			throws SystemException {
		try {
            DbDataSource checkDS = DEEClientUtil.lookupDbDataSource(dr.getResource_name());
            String version = DBUtil.getA8VersionStr(checkDS.getConnection());
            A8FormMgr a8FormMgr = getA8FormMgrByVersion(version);
            
			return a8FormMgr.getTemplateTableByFormIds(getDS(dr),formId,tableName);
		} catch(Exception e) {
			SystemException err = new SystemException(e.getLocalizedMessage());
			throw err;
		}
	}
	
	private JDBCDataSource getDS(DeeResourceBean dr) throws SystemException{
//		DeeResourceBean dr = deeResourceDao.get(dsId);
		try{
			if(dr == null)
				return null;
			return (JDBCDataSource)DEEClientUtil.lookupDbDataSource(dr.getResource_name());
		}
		catch(Exception e){
			SystemException err = new SystemException(e.getLocalizedMessage());
			throw err;
		}
	}

	/**
	 * 根据A8版本号，获取A8表单管理器
	 * @param version
	 * @return
	 */
	private static A8FormMgr getA8FormMgrByVersion(String version) {
	    A8FormMgr a8FormMgr = null;

        try {
            if (A8VersionManager.getInstance().greatEqualThan(version, A8VersionConstants.V3_50) &&
                    A8VersionManager.getInstance().lessThan(version, A8VersionConstants.V5_0)) {
                a8FormMgr = new A8V3_50FormImpl();
            } else if (A8VersionManager.getInstance().greatEqualThan(version, A8VersionConstants.V5_0)) {
                a8FormMgr = new A8V5_0FormImpl();
            }
        } catch (TransformException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        if (a8FormMgr == null) {
            a8FormMgr = new A8V3_50FormImpl();
        }

        return a8FormMgr;
	}

    private String getPropertiesValue(Properties properties, String key) {
        if (properties != null) {
            String value = properties.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return key;
    }
}
