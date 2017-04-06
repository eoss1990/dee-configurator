package com.seeyon.dee.configurator.action.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.SapWSProcessorBean;
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
 * sap ws适配器
 * @author yangyu
 *
 */
public class SapWsProcessorAction extends BaseAction{

	private static final long serialVersionUID = 5459270063071511107L;
	private static Logger logger = Logger.getLogger(SapWsProcessorAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String WS_VIEW = "sapws";
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private List<KeyValue> items;
    private SapWSProcessorBean wsBean;
    private DeeResourceBean bean;
    
    /**
     * 保存
     * 
     * @return
     */
    public String save() {
        
        if (wsBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        
        if(items != null)
        {
        	if(!checkParams(items))
        	return ajaxForwardError("参数名称重复，修改后再保存！");
        	toDeeResource();
        }else
        {
        	Map<String, Object> paraMap = new LinkedHashMap<String, Object>();
        	wsBean.setParameter(paraMap);
        }
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(wsBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
            bean.setDeeResourceTemplate(tmpBean);
            
            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！",infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        } else {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDr(wsBean);
                drb.setDis_name(bean.getDis_name());
                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,sort,flowId);
                return ajaxForwardSuccess(getText("修改成功！"),infor);
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
                    wsBean = (SapWSProcessorBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return WS_VIEW;
    }
    
    public void toDeeResource() {
        Map<String, Object> paraMap = new LinkedHashMap<String, Object>();
	    for (KeyValue item : items) {
			if (item == null) {
				continue;
			}
			if (paraMap.containsKey(item.getKey())) {
				break;
			}
			paraMap.put(item.getKey(), item.getValue());
	    }
        wsBean.setParameter(paraMap);
    }
    
    public FlowSubBean getFlowSubBean(DeeResourceBean drb) {
        FlowSubBean sub = new FlowSubBean();
        FlowBean flowBean = new FlowBean();
        flowBean.setFLOW_ID(flowId);
        
        sub.setFlow_sub_id(UuidUtil.uuid());
        sub.setFlow(flowBean);
        sub.setDeeResource(drb);
        sub.setSort(sort);
        
        return sub;
    }
    
    public boolean checkParams(List<KeyValue> items)
    {
    	for(int i=0;i<items.size();i++)
    	{
    		for(int j=i+1;j<items.size();j++)
    		{
    			if(items.get(i).getKey().trim().equals(items.get(j).getKey().trim()))
    			{
    				return false;
    			}
    		}
    	}
    	return true;
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

	public SapWSProcessorBean getWsBean() {
		return wsBean;
	}

	public void setWsBean(SapWSProcessorBean wsBean) {
		this.wsBean = wsBean;
	}

	public DeeResourceBean getBean() {
		return bean;
	}

	public void setBean(DeeResourceBean bean) {
		this.bean = bean;
	}
    
    
    
}
