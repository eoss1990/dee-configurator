package com.seeyon.dee.configurator.action.adapter;

import com.seeyon.v3x.dee.bean.A8FormWriteBackWriterBean;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.dee.configurator.dto.FormWriteDto;
import com.seeyon.dee.configurator.dto.TreeNode;
import com.seeyon.dee.configurator.mgr.A8FormWriteBackWriterMgr;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A8表单回填Action
 *
 * @author zhangfb
 */
public class A8FormWriteBackWriterAction extends BaseAction {
    private static Logger logger = Logger.getLogger(A8FormWriteBackWriterAction.class.getName());

    @Autowired
    @Qualifier("a8FormWriteBackWriterMgr")
    A8FormWriteBackWriterMgr a8FormWriteBackWriterMgr;

    @Autowired
    @Qualifier("dsTreeManager")
    private DSTreeManager dsTreeManager;

    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;

    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;

    private String flowId;              // flow标识
    private String resourceId;          // 资源ID      -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;

    private A8FormWriteBackWriterBean writerBean;
    private DeeResourceBean bean;

    private List<FormWriteDto> formWriteDtos;

    private List<DeeResourceBean> dbList;
    private String jsonData;  //返回业务子树json数据
    private String dsId;
    private String id;
    private String nId;
    private String nType;
    private String nForm;

    /**
     * 保存
     *
     * @return 成功或失败
     */
    public String save() {
        if (writerBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }

        fromWriteBackDtos();

        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setRef_id(writerBean.getDataSource());
            bean.setDr(writerBean);

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
                drb.setDr(writerBean);
                drb.setDis_name(bean.getDis_name());
                bean.setRef_id(writerBean.getDataSource());
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
        try {
            dbList = a8FormWriteBackWriterMgr.selectAllA8Dbs();
        } catch (SystemException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && bean.getDeeResourceTemplate() != null) {
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    writerBean = (A8FormWriteBackWriterBean) new ConvertDeeResourceBean(bean).getResource();
                    toWriteBackDtos();
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }

        return "a8form_writeback_writer";
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

    /**
     * 从dto的list转换成Map
     */
    private void fromWriteBackDtos() {
        Map<String, String> paraMap = new LinkedHashMap<String, String>();
        if (formWriteDtos != null) {
            for (FormWriteDto item : formWriteDtos) {
                if (item != null) {
                    paraMap.put(item.getId() + "|" + item.getName(), item.getValue());
                }
            }
        }
        writerBean.setFieldMap(paraMap);
    }

    /**
     * 从map转换成dto的list
     */
    private void toWriteBackDtos() {
        if (formWriteDtos == null) {
            formWriteDtos = new ArrayList<FormWriteDto>();
        }

        if (writerBean.getFieldMap() != null) {
            for (Map.Entry<String, String> entry : writerBean.getFieldMap().entrySet()) {
                FormWriteDto formWriteDto = new FormWriteDto();
                String key = entry.getKey();
                String[] array = key.split("\\|");
                formWriteDto.setId(array[0]);
                formWriteDto.setName(array[1]);
                formWriteDto.setValue(entry.getValue());
                formWriteDtos.add(formWriteDto);
            }
        }
    }

    /**
     * 载入表单数据
     *
     * @return
     */
    public String selectDs() {
        List<TreeNode> nList;
        try {
            DeeResourceBean dr = deeResourceManager.get(dsId);
            nList = dsTreeManager.getA8MetaAllTables(dr);
            jsonData = dsTreeManager.treeList2Json(nList);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            return ajaxForwardError("获取表异常：" + e.getLocalizedMessage());
        }
        return ajaxForwardSuccess("获节点成功！");
    }

    /**
     * 异步载入表单数据
     *
     * @return
     */
    public String selectOneDs() {
        try {
            DeeResourceBean dr = deeResourceManager.get(nId);
            if (dr == null) {
                return ajaxForwardError("获取到资源信息!");
            }
            List<TreeNode> nList = dsTreeManager.getA8MetaAllColumns(dr, nForm, id);
            if (nList == null || nList.size() == 0) {
                return ajaxForwardError("获取没有子节点！");
            }
            jsonData = dsTreeManager.treeList2Json(nList);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            return ajaxForwardError("获取子节点异常：" + e.getLocalizedMessage());
        }
        return ajaxForwardSuccess("获取子节点成功！");
    }

    public List<DeeResourceBean> getDbList() {
        return dbList;
    }

    public List<FormWriteDto> getFormWriteDtos() {
        return formWriteDtos;
    }

    public void setFormWriteDtos(List<FormWriteDto> formWriteDtos) {
        this.formWriteDtos = formWriteDtos;
    }

    public void setDbList(List<DeeResourceBean> dbList) {
        this.dbList = dbList;
    }

    public A8FormWriteBackWriterBean getWriterBean() {
        return writerBean;
    }

    public void setWriterBean(A8FormWriteBackWriterBean writerBean) {
        this.writerBean = writerBean;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
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

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getNId() {
        return nId;
    }

    public void setNId(String nId) {
        this.nId = nId;
    }

    public String getNForm() {
        return nForm;
    }

    public void setNForm(String nForm) {
        this.nForm = nForm;
    }

    public String getNType() {
        return nType;
    }

    public void setNType(String nType) {
        this.nType = nType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
