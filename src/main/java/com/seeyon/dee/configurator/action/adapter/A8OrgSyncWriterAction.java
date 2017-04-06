package com.seeyon.dee.configurator.action.adapter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.v3x.dee.bean.OrgSyncWriterBean;
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
 * 组织机构同步
 * 
 * @author Zhang.Fubing
 * @date 2013-5-20下午02:28:25
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class A8OrgSyncWriterAction extends BaseAction {

    private static final long serialVersionUID = 3981409557309257881L;
    private static Logger logger = Logger.getLogger(A8OrgSyncWriterAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    private static final String WRITER_VIEW = "org_sync";
    private static final String ORG_USERNAME = "service-admin";
    private static final String ORG_INTERFACENAME = "organizationDataService";
    private static final String ORG_XMLNS = "http://impl.organization.services.v3x.seeyon.com";
    private static final String ORG_METHODNAME = "importData";
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private String selSrv; //增加类型替代resourceType保存
    
    private OrgSyncWriterBean writerBean;
    private DeeResourceBean bean;
    
    /**
     * 保存
     * 
     * @return
     */
    public String save() {
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(writerBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
//           tmpBean.setResource_template_id(resourceType);
            tmpBean.setResource_template_id(selSrv);
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
                    writerBean = (OrgSyncWriterBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            writerBean = new OrgSyncWriterBean();
            writerBean.setUserName(ORG_USERNAME);
            writerBean.setInterfaceName(ORG_INTERFACENAME);
            writerBean.setXmlns(ORG_XMLNS);
            writerBean.setMethodName(ORG_METHODNAME);
        }
        return WRITER_VIEW;
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

    public OrgSyncWriterBean getWriterBean() {
        return writerBean;
    }

    public void setWriterBean(OrgSyncWriterBean writerBean) {
        this.writerBean = writerBean;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

	public String getSelSrv() {
		return selSrv;
	}

	public void setSelSrv(String selSrv) {
		this.selSrv = selSrv;
	}
}
