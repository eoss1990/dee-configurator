package com.seeyon.dee.configurator.mgr.impl;

import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.dee.configurator.dto.A8EnumTreeNode;
import com.seeyon.dee.configurator.mgr.A8EnumMgr;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.v3x.dee.common.a8version.A8VersionConstants;
import com.seeyon.v3x.dee.common.a8version.A8VersionManager;
import com.seeyon.v3x.dee.common.base.util.DBUtil;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.util.SourceUtil;
import com.seeyon.v3x.dee.datasource.JDBCDataSource;
import com.seeyon.v3x.dee.resource.DbDataSource;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DEEClientUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A8枚举管理器实现类
 *
 * @author zhangfb
 */
@Service("a8EnumMgr")
public class A8EnumManagerImpl implements A8EnumMgr {

    public static final String A8_VERSION_V3_50 = "V3.50";
    public static final String A8_VERSION_V5_0 = "V5.0";
    public static final String A8_VERSION_V5_0SP1 = "V5.0SP1";


    private static final String ENUM_SQL = "select 1 as id, -1 as parent_id, '单位枚举' as name, -11 as enumtype from ctp_enum\n" +
            "union\n" +
            "SELECT id, 1 as parent_id, name, -1 as enumtype from org_unit WHERE TYPE = 'Account' AND IS_GROUP = '0'\n" +
            "UNION\n" +
            "SELECT id, ORG_ACCOUNT_ID as parent_id, ENUMNAME as name, enumtype from ctp_enum where (ENUMTYPE = '0' or ENUMTYPE = '3') and ORG_ACCOUNT_ID is not NULL and PARENT_ID = '0'\n" +
            "UNION\n" +
            "SELECT id, parent_id,ENUMNAME as name, enumtype from ctp_enum where (ENUMTYPE = '0' or ENUMTYPE = '3') and ORG_ACCOUNT_ID is not NULL and PARENT_ID != '0'\n" +
            "UNION\n" +
            "select 0 as id, -1 as parent_id, '公共枚举' as name, -11 as enumtype from ctp_enum\n" +
            "UNION\n" +
            "SELECT id, parent_id, ENUMNAME as name, enumtype from ctp_enum where (ENUMTYPE = '0' or ENUMTYPE = '3') and ORG_ACCOUNT_ID is NULL";

    @Autowired
    @Qualifier("dsTreeManager")
    private DSTreeManager dsTreeManager;

    @Override
    public List<A8EnumTreeNode> getA8EnumTreeNodes(DeeResourceBean dr) throws SystemException {
        return getA8EnumTreeNodes(dr, ENUM_SQL);
    }

    /**
     * 获取A8枚举树节点
     *
     * @param dr  数据源
     * @param sql sql语句
     * @return java.util.List&lt;A8EnumTreeNode&gt;
     * @throws SystemException
     */
    private List<A8EnumTreeNode> getA8EnumTreeNodes(DeeResourceBean dr, String sql) throws SystemException {
        List<A8EnumTreeNode> list = new ArrayList<A8EnumTreeNode>();

        if (dr == null) {
            return list;
        }

        try {
            DbDataSource ds = DEEClientUtil.lookupDbDataSource(dr.getResource_name());
            String a8Version = DBUtil.getA8VersionStr(ds.getConnection());

            if (a8Version == null ||
                    A8VersionManager.getInstance().lessThan(a8Version, A8VersionConstants.V5_0SP2)) {
                throw new SystemException("枚举导入导出只支持A8V5.0SP2及以上！");
            }

            Statement stmt = ds.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                A8EnumTreeNode node = new A8EnumTreeNode();
                node.setId(rs.getString("id"));
                node.setParentId(rs.getString("parent_id"));
                node.setName(rs.getString("name"));
                node.setEnumType(rs.getString("enumtype"));
                list.add(node);
            }
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }

        return list;
    }

    /**
     * 树列表转换为json数据
     *
     * @param tnList 树列表
     * @return json字符串
     */
    @Override
    public String treeList2JsonForExport(List<A8EnumTreeNode> tnList) {
        JSONObject jObj;
        JSONArray jsList = new JSONArray();
        String jsonData;

        for (A8EnumTreeNode tNode : tnList) {
            jObj = new JSONObject();
            jObj.put("id", tNode.getId());
            jObj.put("pId", tNode.getParentId());
            jObj.put("name", tNode.getName());
            jObj.put("enumtype", tNode.getEnumType());
            if ("-1".equals(tNode.getParentId())) {
                jObj.put("isParent", "true");
            }
            if ("1".equals(tNode.getId())) {
                jObj.put("nocheck", "true");
            }
            if ("3".equals(tNode.getEnumType())) {
                jObj.put("icon", "/styles/management/themes/css/ztree_img/diy/enumtype.png");
            } else if ("0".equals(tNode.getEnumType())) {
                jObj.put("icon", "/styles/management/themes/css/ztree_img/diy/enum.png");
            }
            jsList.add(jObj);
        }
        jsonData = jsList.toString();
        return jsonData;
    }

    /**
     * 树列表转换为json数据
     *
     * @param tnList 树列表
     * @return json字符串
     */
    @Override
    public String treeList2JsonForImport(List<A8EnumTreeNode> tnList) {
        JSONObject jObj;
        JSONArray jsList = new JSONArray();
        String jsonData;

        for (A8EnumTreeNode tNode : tnList) {
            jObj = new JSONObject();
            jObj.put("id", tNode.getId());
            jObj.put("pId", tNode.getParentId());
            jObj.put("name", tNode.getName());
            jObj.put("enumtype", tNode.getEnumType());
            if ("-1".equals(tNode.getParentId())) {
                jObj.put("isParent", "true");
            }
            if ("1".equals(tNode.getId()) || "1".equals(tNode.getParentId()) || "0".equals(tNode.getId())) {
                jObj.put("nocheck", "true");
            }
            if ("3".equals(tNode.getEnumType())) {
                jObj.put("nocheck", "true");
                jObj.put("icon", "/styles/management/themes/css/ztree_img/diy/enumtype.png");
            } else if ("0".equals(tNode.getEnumType())) {
                jObj.put("icon", "/styles/management/themes/css/ztree_img/diy/enum.png");
            }
            jsList.add(jObj);
        }
        jsonData = jsList.toString();
        return jsonData;
    }

    /**
     * 选择所有A8数据源
     *
     * @return A8数据源列表
     * @throws SystemException
     */
    @Override
    public List<DeeResourceBean> selectAllA8Dbs() throws SystemException {
        try {
            List<DeeResourceBean> dbList = dsTreeManager.getA8MetaAllDbs();
            for (DeeResourceBean drb : dbList) {
                drb.setDr(new ConvertDeeResourceBean(drb).getResource());
            }
            return dbList;
        } catch (SystemException e) {
            throw e;
        }
    }
}
