package com.seeyon.dee.configurator.action.adapter;

import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.RestProcessorBean;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.dee.configurator.mgr.RestMgr;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.a8rest.RestServiceManager;
import com.seeyon.v3x.dee.common.a8rest.model.RestBodyBean;
import com.seeyon.v3x.dee.common.a8rest.model.RestFunctionBean;
import com.seeyon.v3x.dee.common.a8rest.model.RestParam;
import com.seeyon.v3x.dee.common.a8rest.model.RestServiceBean;
import com.seeyon.v3x.dee.common.a8rest.util.RestUtil;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Rest Action
 *
 * @author zhangfb
 */
public class RestProcessorAction extends BaseAction {
    private static Logger logger = Logger.getLogger(RestProcessorAction.class.getName());

    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;

    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;

    @Autowired
    @Qualifier("restMgr")
    private RestMgr restMgr;

    @Autowired
    private RestServiceManager restServiceManager;

    private String flowId;              // flow标识
    private String resourceId;          // 资源ID      -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private String selInterface; //增加类型替代resourceType保存

    private RestProcessorBean restProcessorBean;
    private DeeResourceBean bean;

    private List<RestServiceBean> serviceBeans;      // 服务列表，初始的时候加载
    private Integer serviceId;           // 服务ID
    private Integer functionId;          // 方法ID
    private String jsonData;             // json数据
    private List<String> responseTypes;  // 响应类型列表
    private String retFlag;              // 返回类型
    private String mapingData;           // 响应类型
    private List<RestParam> restParams;  // Rest参数列表
    private List<KeyValue> keyItems;
    private String isShowTab;              //是否显示外键表

    private String id;
    private String address;
    private String adminUserName;
    private String adminPassword;

    /**
     * 保存
     *
     * @return 成功或失败
     */
    public String save() {
        if (restProcessorBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }

        //批量发起流程表单
        if (restProcessorBean != null && Integer.valueOf("201").equals(restProcessorBean.getFunctionId())){
            if (keyItems == null || keyItems.size() < 1) {
                return ajaxForwardError("请设置外键！");
            }
        }

        fromRestParams();

        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(restProcessorBean);

            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
//          tmpBean.setResource_template_id(resourceType);
            tmpBean.setResource_template_id(selInterface);
            bean.setDeeResourceTemplate(tmpBean);

            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        } else {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDr(restProcessorBean);
                drb.setDis_name(bean.getDis_name());
                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,drb.getFlowSub().getSort(),flowId);
                return ajaxForwardSuccess("修改成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        }
    }

    /**
     * 查看
     *
     * @return 查看页面
     */
    public String view() {
        serviceBeans = restServiceManager.getServiceBeans();

        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && bean.getDeeResourceTemplate() != null) {
                	if(resourceType == null){
                		//根据类型区分来源或是目标
                		resourceType = "36".equals(bean.getDeeResourceTemplate().getResource_template_id())?"1001":"1002";
                	}
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    restProcessorBean = (RestProcessorBean) new ConvertDeeResourceBean(bean).getResource();
                    toRestParams();
                    this.retFlag = isMergeToDocument(restProcessorBean.getServiceId(),
                            restProcessorBean.getFunctionId()).toString();
                    this.isShowTab = isShowTable(restProcessorBean.getServiceId(), restProcessorBean.getFunctionId()).toString();
                    RestFunctionBean restFunctionBean = restServiceManager.getFunctionBean(
                            restProcessorBean.getServiceId(), restProcessorBean.getFunctionId());
                    String[] array = restFunctionBean.getResponseType().split("\\|");
                    this.responseTypes = Arrays.asList(array);
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
        return "restprocessor";
    }

    /**
     * 根据服务ID，查询出方法列表，并放在json中
     *
     * @return 成功/失败
     */
    public String listFunctionsByServiceId() {
        try {
            List<RestFunctionBean> functionBeans = restServiceManager.listFunctionsByServiceId(serviceId);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object source, String name, Object value) {
                    // return true to skip name
                    return source instanceof RestBodyBean || source instanceof RestServiceBean;
                }
            });
            JSON json = JSONSerializer.toJSON(functionBeans, jsonConfig);
            this.jsonData = json.toString();

            return this.ajaxForwardSuccess("success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return this.ajaxForwardError("failure");
    }

    /**
     * 刷新方法
     *
     * @return 成功/失败
     */
    public String refreshByFunction() {
        try {
            List<RestParam> restParams = restServiceManager.listParams(serviceId, functionId);

            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object source, String name, Object value) {
                    // return true to skip name
                    return source instanceof RestServiceBean;
                }
            });

            JSON json = JSONSerializer.toJSON(restParams, jsonConfig);
            this.jsonData = json.toString();
            this.retFlag = isMergeToDocument(serviceId, functionId).toString();
            this.isShowTab = isShowTable(serviceId, functionId).toString();
            this.mapingData = restServiceManager.getFunctionBean(serviceId, functionId).getResponseType();

            return this.ajaxForwardSuccess("success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return this.ajaxForwardError("failure");
    }

    /**
     * 选择单位
     *
     * @return
     */
    public String deeSelectOrgAccountId() {
        try {
            this.jsonData = restMgr.deeSelectOrgAccountId(address, adminUserName, adminPassword, id);
            return this.ajaxForwardSuccess("成功！");
        } catch (TransformException e) {
            logger.error(e.getLocalizedMessage(), e);
            return this.ajaxForwardError(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return this.ajaxForwardError(e.getMessage());
        }
    }

    /**
     * 选择部门
     *
     * @return
     */
    public String deeSelectDepartmentId() {
        try {
            this.jsonData = restMgr.deeSelectDepartmentId(address, adminUserName, adminPassword, id);
            return this.ajaxForwardSuccess("成功！");
        } catch (TransformException e) {
            logger.error(e.getLocalizedMessage(), e);
            return this.ajaxForwardError(e.getMessage());
        }
    }

    /**
     * 选择部门，不包括单位
     *
     * @return
     */
    public String deeSelectDepartmentIdNotOrgAccount() {
        try {
            this.jsonData = restMgr.deeSelectDepartmentIdNotOrgAccount(address, adminUserName, adminPassword, id);
            return this.ajaxForwardSuccess("成功！");
        } catch (TransformException e) {
            logger.error(e.getLocalizedMessage(), e);
            return this.ajaxForwardError(e.getMessage());
        }
    }

    /**
     * 选择岗位
     *
     * @return
     */
    public String deeSelectPostId() {
        try {
            this.jsonData = restMgr.deeSelectPostId(address, adminUserName, adminPassword, id);
            return this.ajaxForwardSuccess("成功！");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return this.ajaxForwardError(e.getMessage());
        }
    }

    /**
     * 选择职务级别
     *
     * @return
     */
    public String deeSelectOrgLevelId() {
        try {
            this.jsonData = restMgr.deeSelectOrgLevelId(address, adminUserName, adminPassword, id);
            return this.ajaxForwardSuccess("成功！");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return this.ajaxForwardError(e.getMessage());
        }
    }

    private FlowSubBean getFlowSubBean(DeeResourceBean drb) {
        FlowSubBean sub = new FlowSubBean();
        FlowBean flowBean = new FlowBean();
        flowBean.setFLOW_ID(flowId);

        sub.setFlow_sub_id(UuidUtil.uuid());
        sub.setFlow(flowBean);
        sub.setDeeResource(drb);
        sub.setSort(sort);

        return sub;
    }

    /**
     * 转换restParams列表到restProcessorBean参数列表
     */
    private void fromRestParams() {
        Map<String, String> paraMap = new LinkedHashMap<String, String>();
        if (restParams != null) {
            for (RestParam item : restParams) {
                if (item != null) {
                    paraMap.put(item.getParamName(), item.toString());
                }
            }
        }
        restProcessorBean.setParamMap(paraMap);
        Map<String, String> keyMap = new LinkedHashMap<String, String>();
        if (keyItems != null) {
            for (KeyValue item : keyItems) {
                if(item == null || StringUtils.isBlank(item.getKey())) continue;
                keyMap.put(item.getKey(),item.getValue());
            }
        }
        restProcessorBean.setKeyMap(keyMap);
    }

    /**
     * 转换restProcessorBean参数列表到restParams列表<br/>
     * 参数列表由字符串格式“paramId|paramName|paramType|paramValue”组成
     */
    private void toRestParams() {
        if (restParams == null) {
            restParams = new ArrayList<RestParam>();
        }

        if (restProcessorBean.getParamMap() != null) {
            for (Map.Entry<String, String> entry : restProcessorBean.getParamMap().entrySet()) {
                RestParam restParam = RestUtil.parseToRestParam(entry.getValue());
                if (restParam != null) {
                    restParams.add(restParam);
                }
            }
        }
        if (keyItems == null){
            keyItems = new ArrayList<KeyValue>();
        }
        if (restProcessorBean.getKeyMap() != null) {
            for (Map.Entry<String, String> entry : restProcessorBean.getKeyMap().entrySet()) {
                if (entry == null) continue;
                KeyValue kv = new KeyValue(entry.getKey(),entry.getValue());
                keyItems.add(kv);
            }
        }
    }

    /**
     * isMergeToDocument
     *
     * @param serviceId  服务ID
     * @param functionId 方法ID
     * @return true：合并，false：不合并
     */
    private Boolean isMergeToDocument(Integer serviceId, Integer functionId) {
        RestFunctionBean restFunctionBean = restServiceManager.getFunctionBean(serviceId, functionId);
        return restFunctionBean.getDealMethod() != null && !"".equals(restFunctionBean.getDealMethod());
    }
    /**
     * 是否显示外键表
     *
     * @param serviceId  服务ID
     * @param functionId 方法ID
     * @return true：显示，false：不显示
     */
    private Boolean isShowTable(Integer serviceId, Integer functionId) {
        RestFunctionBean restFunctionBean = restServiceManager.getFunctionBean(serviceId, functionId);
        return "true".equalsIgnoreCase(restFunctionBean.getShowTab());
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public String getRetFlag() {
        return retFlag;
    }

    public void setRetFlag(String retFlag) {
        this.retFlag = retFlag;
    }

    public String getMapingData() {
        return mapingData;
    }

    public void setMapingData(String mapingData) {
        this.mapingData = mapingData;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public RestProcessorBean getRestProcessorBean() {
        return restProcessorBean;
    }

    public void setRestProcessorBean(RestProcessorBean restProcessorBean) {
        this.restProcessorBean = restProcessorBean;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public List<RestServiceBean> getServiceBeans() {
        return serviceBeans;
    }

    public void setServiceBeans(List<RestServiceBean> serviceBeans) {
        this.serviceBeans = serviceBeans;
    }

    public List<RestParam> getRestParams() {
        return restParams;
    }

    public void setRestParams(List<RestParam> restParams) {
        this.restParams = restParams;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getSelInterface() {
		return selInterface;
	}

	public void setSelInterface(String selInterface) {
		this.selInterface = selInterface;
	}

    public List<KeyValue> getKeyItems() {
        return keyItems;
    }

    public void setKeyItems(List<KeyValue> keyItems) {
        this.keyItems = keyItems;
    }

    public String getIsShowTab() {
        return isShowTab;
    }

    public void setIsShowTab(String isShowTab) {
        this.isShowTab = isShowTab;
    }
}
