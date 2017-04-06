package com.seeyon.dee.configurator.action.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.SrProcessorBean;
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
 * WebService action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-14下午10:50:06
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class StaticRestProcessorAction extends BaseAction {

    private static final long serialVersionUID = -7592660749248084296L;
    
    private static Logger logger = Logger.getLogger(WsprocessorAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String SRP_VIEW = "staticrestprocesser";
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private List<KeyValue> headers;
    private List<KeyValue> bodys;
    
    private SrProcessorBean srBean;
    private DeeResourceBean bean;

    /**
     * 保存
     * 
     * @return
     */
    public String save() {
    	if (headers == null) {
    		headers = new ArrayList<KeyValue>();
    	}
    	if (bodys == null) {
    		bodys = new ArrayList<KeyValue>();
    	}

    	if (srBean == null || bean == null) {
    		return ajaxForwardError("系统错误！");
    	}
    	String isA8 = request.getParameter("isA8");
    	srBean.setIsA8(isA8);

    	if (!toDeeResource()) {
    		return ajaxForwardError(Constants.PARAMSCHECKMSG);
    	}
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(srBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
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
                drb.setDr(srBean);
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
                    srBean = (SrProcessorBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return SRP_VIEW;
    }
    
    /**
     * 转换
     * 
     * @return true，没有重复项；false，有重复项
     */
    public boolean toDeeResource() {
        boolean exist = true;;
        Map<String, Object> headersMap = new LinkedHashMap<String, Object>();
        
        for (KeyValue item : headers) {
			if (item == null) {
				continue;
			}
            if (headersMap.containsKey(item.getKey())) {
                exist = false;
                break;
            }
            headersMap.put(item.getKey(), item.getValue());
        }
        srBean.setHeaders(headersMap);
        
        Map<String, Object> bodysMap = new LinkedHashMap<String, Object>();
        
        for (KeyValue item : bodys) {
			if (item == null) {
				continue;
			}
            if (bodysMap.containsKey(item.getKey())) {
                exist = false;
                break;
            }
            bodysMap.put(item.getKey(), item.getValue());
        }
        srBean.setBodys(bodysMap);
        
        return exist;
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

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<KeyValue> getHeaders() {
		return headers;
	}

	public void setHeaders(List<KeyValue> headers) {
		this.headers = headers;
	}

	public List<KeyValue> getBodys() {
		return bodys;
	}

	public void setBodys(List<KeyValue> bodys) {
		this.bodys = bodys;
	}

	public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

	public SrProcessorBean getSrBean() {
		return srBean;
	}

	public void setSrBean(SrProcessorBean srBean) {
		this.srBean = srBean;
	}
}
