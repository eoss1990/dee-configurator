package com.seeyon.dee.configurator.action.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.WSCommonWriter;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;

/**
 * A8无流程表单
 * 
 * @author Zhang.Fubing
 * @date 2013-5-21下午02:45:47
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class A8CommonWsAction extends BaseAction {
    
    private static final long serialVersionUID = -8424010277969228585L;

    private static Logger logger = Logger.getLogger(A8CommonWsAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    @Autowired
    @Qualifier("dsTreeManager")
    private DSTreeManager dsTreeManager;
    
    private static final String A8COMMONWS_VIEW = "a8commonws";
    private static final String A8_USERNAME = "service-admin";
    private static final String A8_INTERFACENAME = "formService";
    private static final String A8_XMLNS = "http://impl.form.services.v3x.seeyon.com";
    private static final String A8_METHODNAME = "importBusinessFormData";
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private String selFunc; //增加类型替代resourceType保存
    private List<KeyValue> items;
    private List<KeyValue> sqlItems;
    private String jsonData;            //返回业务子树json数据
    private List<DeeResourceBean> dbList;
    
    private WSCommonWriter writerBean;
    private DeeResourceBean bean;
    
    private String dsId;
    
    /**
     * 保存
     * 
     * @return
     */
    public String save() {
        if (writerBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        
        if (!toDeeResource()) {
            return ajaxForwardError(Constants.PARAMSCHECKMSG);
        }
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(writerBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
//           tmpBean.setResource_template_id(resourceType);
            tmpBean.setResource_template_id(selFunc);
            bean.setDeeResourceTemplate(tmpBean);
            
            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        } else  {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDr(writerBean);
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
     * @return
     */
    public String view() {
        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && 
                    bean.getDeeResourceTemplate() != null)
                {
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    writerBean = (WSCommonWriter) new ConvertDeeResourceBean(bean).getResource();
                    toItems();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            initItems();
            writerBean = new WSCommonWriter();
            writerBean.setUserName(A8_USERNAME);
            writerBean.setInterfaceName(A8_INTERFACENAME);
            writerBean.setXmlns(A8_XMLNS);
            writerBean.setMethodName(A8_METHODNAME);
        }
        return A8COMMONWS_VIEW;
    }
    
    /**
     * 选择A8数据库
     * 
     * @return
     */
    public String selectAllA8Dbs() {
        try {
            dbList = dsTreeManager.getA8MetaAllDbs();
            for (DeeResourceBean drb : dbList) {
                drb.setDr(new ConvertDeeResourceBean(drb).getResource());
            }
        } catch (SystemException e) {
            e.printStackTrace();
            return ajaxForwardError("异常！");
        }
        
        JsonConfig jsonConfig = new JsonConfig();  
        jsonConfig.setJsonPropertyFilter( new PropertyFilter(){  
            public boolean apply( Object source, String name, Object value ){  
                // return true to skip name
                boolean s = source instanceof DeeResourceTemplateBean ||
                source instanceof FlowSubBean;
                return s;
            }  
        }); 
        this.jsonData = JSONSerializer.toJSON(dbList, jsonConfig).toString();
        return ajaxForwardSuccess("成功！");
    }
    
    public String selectDs(){
        List<TreeNode> nList = new ArrayList<TreeNode>();
        
        try {
            DeeResourceBean dr = deeResourceManager.get(dsId);
            if (dr == null) {
                return ajaxForwardError("获取到资源信息!");
            }
            
            if (Integer.parseInt(dr.getDeeResourceTemplate().getResource_template_id()) == DeeResourceEnum.A8MetaDatasource.ordinal()){
                //获取A8元数据
                nList.addAll(dsTreeManager.getA8MetaAllTables(dr));
            } else {
                //获取数据源
                nList.addAll(dsTreeManager.getDSAllTables(dr));
            }
            if (nList.size() == 0) {
                return ajaxForwardError("获取表节点失败！");
            }
            jsonData = dsTreeManager.treeList2Json(nList);
        } catch (SystemException e) {
            return ajaxForwardError("获取表异常："+e.getLocalizedMessage());
        }
        return ajaxForwardSuccess("获节点成功！");
    }
    
    /**
     * 获取FlowSub对象
     * 
     * @param drb
     * @return
     */
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
     * 转换到model的"参数"和"外键关联"
     */
    private boolean toDeeResource() {
        boolean exist = true;
        Map<String, String> paraMap = new LinkedHashMap<String, String>();
        
        if (items != null) {
            for (KeyValue item : items) {
                if (item != null) {
                    paraMap.put(item.getKey(), item.getValue());
                }
            }
        }
        if (sqlItems != null) {
            for (KeyValue item : sqlItems) {
                if (paraMap.containsKey("#"+item.getKey())) {
                    exist = false;
                    break;
                }
                if (item != null) {
                    paraMap.put("#" + item.getKey(), item.getValue());
                }
            }
        }
        writerBean.setMap(orderCommonMap(paraMap));
        
        return exist;
    }
    
    /**
     * 转换到UI的"参数"和"外键关联"
     */
    private void toItems() {
        items = new ArrayList<KeyValue>();
        sqlItems = new ArrayList<KeyValue>();
        KeyValue keyValue = null;
        if (writerBean != null) {
            removeKey(writerBean);
            for (Map.Entry<String, String> entry : writerBean.getMap()
                    .entrySet()) {
                if ("".equals(entry.getKey()))
                    continue;
                if ("#".equalsIgnoreCase(entry.getKey().substring(0, 1))) {
                    keyValue = new KeyValue(entry.getKey().substring(1), entry.getValue());
                    sqlItems.add(keyValue);
                } else {
                    keyValue = new KeyValue(entry.getKey(), entry.getValue());
                    items.add(keyValue);
                }
            }
        }
    }
    
    /**
     * 初始化"参数"
     */
    private void initItems() {
        items = new ArrayList<KeyValue>();
        
        KeyValue keyValue = new KeyValue("loginName", "");
        items.add(keyValue);
        keyValue = new KeyValue("formCode", "");
        items.add(keyValue);
        keyValue = new KeyValue("valiFieldAry", "");
        items.add(keyValue);
    }
    
    /**
     * 移除WSCommonWriter 对象里map的两个数据：token，data
     * 
     * @date 2012-05-14
     * @author dkywolf
     * @param writerbean WSCommonWriter对象
     * @return
     */
    private WSCommonWriter removeKey(WSCommonWriter writerbean){
        Map<String, String> map = writerbean.getMap();
        map.remove("token");
        map.remove("data");
        writerbean.setMap(map);
        return writerbean;
    }
    
    /**
     * 排序map里的值，因为a8的webservice需要的值是有顺序的
     * 
     * @date 2012-3-8
     * @author liuls
     * @param map Map对象
     * @return
     */
    private Map<String, String> orderCommonMap(Map<String, String> map){
        Map<String, String> linkedMap = new LinkedHashMap<String, String>();
        linkedMap.put("token", map.get("token") == null ? "" : map.get("token"));
        for (Map.Entry<String,String> ey : map.entrySet()) {
            if ("formCode".equals(ey.getKey())) {
                linkedMap.put(ey.getKey(), ey.getValue());
                linkedMap.put("data", map.get("data") == null ? "" : map.get("data"));
            } else {
                linkedMap.put(ey.getKey(), ey.getValue());
            }
        }
        return linkedMap;
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

    public List<KeyValue> getItems() {
        return items;
    }

    public void setItems(List<KeyValue> items) {
        this.items = items;
    }

    public WSCommonWriter getWriterBean() {
        return writerBean;
    }

    public void setWriterBean(WSCommonWriter writerBean) {
        this.writerBean = writerBean;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public List<KeyValue> getSqlItems() {
        return sqlItems;
    }

    public void setSqlItems(List<KeyValue> sqlItems) {
        this.sqlItems = sqlItems;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public List<DeeResourceBean> getDbList() {
        return dbList;
    }

    public void setDbList(List<DeeResourceBean> dbList) {
        this.dbList = dbList;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

	public String getSelFunc() {
		return selFunc;
	}

	public void setSelFunc(String selFunc) {
		this.selFunc = selFunc;
	}

}
