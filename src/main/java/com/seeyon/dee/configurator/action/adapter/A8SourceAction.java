package com.seeyon.dee.configurator.action.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.bean.A8WSWriter;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;

/**
 * A8流程表单
 * 
 * @author Zhang.Fubing
 * @date 2013-5-21下午02:46:11
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class A8SourceAction extends BaseAction {
    
    private static final long serialVersionUID = 4391467656157647694L;
    private static Logger logger = Logger.getLogger(A8SourceAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String A8SOURCE_VIEW = "a8source";
    private static final String A8_USERNAME = "service-admin";
    private static final String A8_INTERFACENAME = "BPMService";
    private static final String A8_XMLNS = "http://impl.flow.services.v3x.seeyon.com";
    private static final String A8_METHODNAME = "launchFormCollaboration";
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;  //
    private String selFunc; //增加类型替代resourceType保存
    private List<KeyValue> items;
    
    private A8WSWriter writerBean;
    private DeeResourceBean bean;
    
    /**
     * 保存
     * 
     * @return
     */
    public String save() {
        if (writerBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        
        toDeeResource();
        
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
                    writerBean = (A8WSWriter) new ConvertDeeResourceBean(bean).getResource();
                    removeKey(writerBean);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            writerBean = new A8WSWriter();
            writerBean.setUserName(A8_USERNAME);
            writerBean.setInterfaceName(A8_INTERFACENAME);
            writerBean.setXmlns(A8_XMLNS);
            writerBean.setMethodName(A8_METHODNAME);
            
            Map<String, String> sqlMap = new LinkedHashMap<String, String>();
            sqlMap.put("senderLoginName", "");
            sqlMap.put("templateCode", "");
            sqlMap.put("subject", "");
            sqlMap.put("attachments", "");
            sqlMap.put("param", "");
            sqlMap.put("relateDoc", "");
            
            writerBean.setMap(sqlMap);
        }
        return A8SOURCE_VIEW;
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
    
    public void toDeeResource() {
        Map<String, String> paraMap = new LinkedHashMap<String, String>();
        for (KeyValue item : items) {
            if (item != null) {
                paraMap.put(item.getKey(), item.getValue());
            }
        }
        writerBean.setMap(orderMap(paraMap));
    }
    
    /**
     * 排序map里的值，因为a8的webservice需要的值是有顺序的
     * 
     * @date 2012-3-8
     * @author liuls
     * @param map Map对象
     * @return
     */
    private Map<String, String> orderMap(Map<String, String> map){
        Map<String, String> linkedMap = new LinkedHashMap<String, String>();
        String[] key = {"token", "senderLoginName", "templateCode", "subject", "data", "attachments", "param"};
        for (String st : key) {
            linkedMap.put(st, map.get(st) == null ? "" : map.get(st));
            map.remove(st);
        }
        linkedMap.putAll(map);
        return linkedMap;
    }
    
    /**
     * 移除A8WSWriter 对象里map的两个数据：token，data
     * 
     * @date 2012-3-8
     * @author liuls
     * @param writerbean A8WSWriter对象
     * @return
     */
    private A8WSWriter removeKey(A8WSWriter writerbean){
        Map<String, String> map = writerbean.getMap();
        map.remove("token");
        map.remove("data");
        writerbean.setMap(map);
        return writerbean;
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

    public A8WSWriter getWriterBean() {
        return writerBean;
    }

    public void setWriterBean(A8WSWriter writerBean) {
        this.writerBean = writerBean;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public List<KeyValue> getItems() {
        return items;
    }

    public void setItems(List<KeyValue> items) {
        this.items = items;
    }

	public String getSelFunc() {
		return selFunc;
	}

	public void setSelFunc(String selFunc) {
		this.selFunc = selFunc;
	}

}
