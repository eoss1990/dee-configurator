package com.seeyon.dee.configurator.action.adapter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.opensymphony.xwork2.ActionContext;
import com.seeyon.v3x.dee.bean.CustomAdapterBean;
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
 * 自定义配置适配器
 * @author yangyu
 *
 */
public class CustomAdapter extends BaseAction{

	private static final long serialVersionUID = -221991438908737875L;
	
	private static Logger logger = Logger.getLogger(CustomAdapter.class.getName());
	
	@Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String CUSTOM_VIEW = "custom";
    private String flowId;          // flow标识
    private String resourceId;      // 资源ID --> 修改时使用
    private int sort;               // 排序号   --> 新增时使用
    private String resourceType;
    private CustomAdapterBean customBean;
    private DeeResourceBean bean;
    
    /**
     * 保存或修改自定义配置
     * 
     * @return
     */
    public String save() {
        if (customBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
            bean.setDeeResourceTemplate(tmpBean);
            bean.setDr(customBean);
            
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
                drb.setDis_name(bean.getDis_name());
                drb.setDr(customBean);

                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,drb.getFlowSub().getSort(),flowId);
                return ajaxForwardSuccess("修改成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardSuccess("异常！");
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
                CustomAdapterBean cBean = new CustomAdapterBean();
                cBean.setCustomXml(bean.getResource_code());
                bean.setDr(cBean);
                ActionContext.getContext().put("cBean", cBean);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return CUSTOM_VIEW;
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

	public CustomAdapterBean getCustomBean() {
		return customBean;
	}

	public void setCustomBean(CustomAdapterBean customBean) {
		this.customBean = customBean;
	}

	public DeeResourceBean getBean() {
		return bean;
	}

	public void setBean(DeeResourceBean bean) {
		this.bean = bean;
	}
}
