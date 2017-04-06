package com.seeyon.dee.configurator.mgr.impl;

import com.seeyon.dee.configurator.mgr.RestMgr;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.a8rest.util.RestUtil;
import com.seeyon.v3x.dee.util.rest.CTPRestClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Service("restMgr")
public class RestMgrImpl implements RestMgr {
    @Override
    public String deeSelectOrgAccountId(String address,
                                        String adminUserName,
                                        String adminPassword, String currentId) throws TransformException {
        CTPRestClient client = RestUtil.createCtpClient(address, adminUserName, adminPassword);

        JSONArray jsList = new JSONArray();
        List<Map> retResult = client.get("orgAccounts", List.class, MediaType.APPLICATION_JSON);
        for (Map map : retResult) {
            JSONObject jObj = new JSONObject();
            String id = map.get("id").toString();
            String pId = map.get("superior").toString();
            String name = map.get("name").toString();
            if (currentId != null && currentId.equals(id)) {
                jObj.put("checked", true);
            }
            if ("-1".equals(pId)) {
                jObj.put("nocheck", "true");
            }
            jObj.put("id", id);
            jObj.put("pId", pId);
            jObj.put("name", name);
            jsList.add(jObj);
        }

        return jsList.toString();
    }

    @Override
    public String deeSelectDepartmentId(String address,
                                        String adminUserName, String adminPassword,
                                        String currentId) throws TransformException {
        CTPRestClient client = RestUtil.createCtpClient(address, adminUserName, adminPassword);

        JSONArray jsList = new JSONArray();
        List<Map> retResult = client.get("orgAccounts", List.class, MediaType.APPLICATION_JSON);
        for (Map map : retResult) {
            JSONObject jObj = new JSONObject();
            String id = map.get("id").toString();
            String pId = map.get("superior").toString();
            String name = map.get("name").toString();
            if ("-1".equals(pId)) {
                jObj.put("nocheck", "true");
            }
            jObj.put("id", id);
            jObj.put("pId", pId);
            jObj.put("name", name);
            jsList.add(jObj);

            List<Map> departments = client.get("/orgDepartments/all/" + id, List.class, MediaType.APPLICATION_JSON);
            for (Map deptMap : departments) {
                jObj = new JSONObject();
                String deptId = deptMap.get("id").toString();
                String deptPid = deptMap.get("superior").toString();
                String deptName = deptMap.get("name").toString();
                if (currentId != null && currentId.equals(deptId)) {
                    jObj.put("checked", true);
                }
                jObj.put("id", deptId);
                jObj.put("pId", deptPid);
                jObj.put("name", deptName);
                jsList.add(jObj);
            }
        }

        return jsList.toString();
    }

    @Override
    public String deeSelectDepartmentIdNotOrgAccount(String address,
                                        String adminUserName, String adminPassword,
                                        String currentId) throws TransformException {
        CTPRestClient client = RestUtil.createCtpClient(address, adminUserName, adminPassword);

        JSONArray jsList = new JSONArray();
        List<Map> retResult = client.get("orgAccounts", List.class, MediaType.APPLICATION_JSON);
        for (Map map : retResult) {
            JSONObject jObj = new JSONObject();
            String id = map.get("id").toString();
            String pId = map.get("superior").toString();
            String name = map.get("name").toString();
            /*if ("-1".equals(pId)) {*/
                jObj.put("nocheck", "true");
            /*}*/
            jObj.put("id", id);
            jObj.put("pId", pId);
            jObj.put("name", name);
            jsList.add(jObj);

            List<Map> departments = client.get("/orgDepartments/all/" + id, List.class, MediaType.APPLICATION_JSON);
            for (Map deptMap : departments) {
                jObj = new JSONObject();
                String deptId = deptMap.get("id").toString();
                String deptPid = deptMap.get("superior").toString();
                String deptName = deptMap.get("name").toString();
                if (currentId != null && currentId.equals(deptId)) {
                    jObj.put("checked", true);
                }
                jObj.put("id", deptId);
                jObj.put("pId", deptPid);
                jObj.put("name", deptName);
                jsList.add(jObj);
            }
        }

        return jsList.toString();
    }

    @Override
    public String deeSelectOrgLevelId(String address,
                                      String adminUserName, String adminPassword,
                                      String currentId) throws TransformException {
        CTPRestClient client = RestUtil.createCtpClient(address, adminUserName, adminPassword);

        JSONArray jsList = new JSONArray();
        List<Map> retResult = client.get("orgAccounts", List.class, MediaType.APPLICATION_JSON);
        for (Map map : retResult) {
            JSONObject jObj = new JSONObject();
            String id = map.get("id").toString();
            String pId = map.get("superior").toString();
            String name = map.get("name").toString();
            String code = map.get("code").toString();
            jObj.put("id", id);
            jObj.put("pId", pId);
            jObj.put("name", name);
            jObj.put("nocheck", "true");
            jsList.add(jObj);

            if ("group".equals(code)) {
                continue;
            }

            List<Map> orgLevels = client.get("/orgLevels/all/" + id, List.class, MediaType.APPLICATION_JSON);
            for (Map orgLevelMap : orgLevels) {
                jObj = new JSONObject();
                String orgLevelId = orgLevelMap.get("id").toString();
                String orgLevelPid = orgLevelMap.get("orgAccountId").toString();
                String orgLevelName = orgLevelMap.get("name").toString();
                if (currentId != null && currentId.equals(orgLevelId)) {
                    jObj.put("checked", true);
                }
                jObj.put("id", orgLevelId);
                jObj.put("pId", orgLevelPid);
                jObj.put("name", orgLevelName);
                jsList.add(jObj);
            }
        }

        return jsList.toString();
    }

    @Override
    public String deeSelectPostId(String address,
                                  String adminUserName, String adminPassword,
                                  String currentId) throws TransformException {
        CTPRestClient client = RestUtil.createCtpClient(address, adminUserName, adminPassword);

        JSONArray jsList = new JSONArray();
        List<Map> retResult = client.get("orgAccounts", List.class, MediaType.APPLICATION_JSON);
        for (Map map : retResult) {
            JSONObject jObj = new JSONObject();
            String id = map.get("id").toString();
            String pId = map.get("superior").toString();
            String name = map.get("name").toString();
            String code = map.get("code").toString();
            jObj.put("id", id);
            jObj.put("pId", pId);
            jObj.put("name", name);
            jObj.put("nocheck", "true");
            jsList.add(jObj);

            if ("group".equals(code)) {
                continue;
            }

            List<Map> posts = client.get("/orgPosts/all/" + id, List.class, MediaType.APPLICATION_JSON);
            for (Map postMap : posts) {
                jObj = new JSONObject();
                String postId = postMap.get("id").toString();
                String postPid = postMap.get("orgAccountId").toString();
                String postName = postMap.get("name").toString();
                if (currentId != null && currentId.equals(postId)) {
                    jObj.put("checked", true);
                }
                jObj.put("id", postId);
                jObj.put("pId", postPid);
                jObj.put("name", postName);
                jsList.add(jObj);
            }
        }

        return jsList.toString();
    }
}
