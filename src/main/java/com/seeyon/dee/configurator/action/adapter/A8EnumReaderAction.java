package com.seeyon.dee.configurator.action.adapter;

import com.seeyon.v3x.dee.bean.A8EnumReaderBean;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.dee.configurator.dto.A8EnumTreeNode;
import com.seeyon.dee.configurator.mgr.A8EnumMgr;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * A8枚举值导出
 *
 * @author zhangfb
 */
public class A8EnumReaderAction extends BaseAction {

    private static Logger logger = Logger.getLogger(A8EnumReaderAction.class.getName());

    @Autowired
    @Qualifier("a8EnumMgr")
    A8EnumMgr a8EnumMgr;

    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;

    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;

    private static final String A8ENUMREADER_VIEW = "a8enumreader";
    private static final String A8_USERNAME = "service-admin";
    private static final String A8_INTERFACENAME = "enumService";
    private static final String A8_XMLNS = "http://impl.enums.services.v3x.seeyon.com";
    private static final String A8_METHODNAME = "exportEnumData";

    private String flowId;              // flow标识
    private String resourceId;          // 资源ID      -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    
    private String selInterface; //增加类型替代resourceType保存

    private String jsonData;            //返回业务子树json数据
    private String dsId;

    private List<DeeResourceBean> dbList;

    private A8EnumReaderBean a8EnumReaderBean;
    private DeeResourceBean bean;

    /**
     * 保存
     *
     * @return
     */
    public String save() {
        if (a8EnumReaderBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }

        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setRef_id(a8EnumReaderBean.getDataSource());
            bean.setDr(a8EnumReaderBean);

            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
//            tmpBean.setResource_template_id(resourceType);
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
        } else  {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDr(a8EnumReaderBean);
                drb.setDis_name(bean.getDis_name());
                drb.setRef_id(a8EnumReaderBean.getDataSource());
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
        try {
            dbList = a8EnumMgr.selectAllA8Dbs();
        } catch (SystemException e) {
            logger.info(e);
        }

        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && bean.getDeeResourceTemplate() != null)
                {
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    a8EnumReaderBean = (A8EnumReaderBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            a8EnumReaderBean = new A8EnumReaderBean();
            a8EnumReaderBean.setUserName(A8_USERNAME);
            a8EnumReaderBean.setInterfaceName(A8_INTERFACENAME);
            a8EnumReaderBean.setXmlns(A8_XMLNS);
            a8EnumReaderBean.setMethodName(A8_METHODNAME);
        }
        return A8ENUMREADER_VIEW;
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

    public String selectDs() {
        try {
            DeeResourceBean dr = deeResourceManager.get(dsId);
            if (dr == null) {
                return ajaxForwardError("获取到资源信息!");
            }

            if (Integer.parseInt(dr.getDeeResourceTemplate().getResource_template_id()) == DeeResourceEnum.A8MetaDatasource.ordinal()) {
                List<A8EnumTreeNode> list = a8EnumMgr.getA8EnumTreeNodes(dr);
                this.jsonData = a8EnumMgr.treeList2JsonForExport(list);
            }
        } catch (SystemException e) {
            return ajaxForwardError("获取资源异常： " + e.toString().replace("\n", ""));
        }
        return ajaxForwardSuccess("获节点成功！");
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

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public List<DeeResourceBean> getDbList() {
        return dbList;
    }

    public void setDbList(List<DeeResourceBean> dbList) {
        this.dbList = dbList;
    }

    public A8EnumReaderBean getA8EnumReaderBean() {
        return a8EnumReaderBean;
    }

    public void setA8EnumReaderBean(A8EnumReaderBean a8EnumReaderBean) {
        this.a8EnumReaderBean = a8EnumReaderBean;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

	public String getSelInterface() {
		return selInterface;
	}

	public void setSelInterface(String selInterface) {
		this.selInterface = selInterface;
	}
    
}
