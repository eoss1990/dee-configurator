package com.seeyon.dee.configurator.action.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.bean.XSLTProcessorBean;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.dee.configurator.mgr.XsltProcessorMgr;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;

import com.seeyon.v3x.dee.util.Constants;
import dwz.framework.common.action.BaseAction;
import com.seeyon.v3x.dee.util.DataChangeUtil;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;

public class XsltProcessorAction extends BaseAction {

    private static final long serialVersionUID = 7125587564359387938L;
    
    private static Logger logger = Logger.getLogger(XsltProcessorAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("xsltProcessorMgr")
    private XsltProcessorMgr xsltProcessorMgr;
    
    private static final String XSLT_VIEW = "xslt";
    
    private String flowId;              // flow标识
    private String resourceId;          // 资源ID  -->修改时使用
    private int sort;                   // 排序号      -->新增时使用
    private String resourceType;
    private String xslChgType;
    private String download_id;
    private String xsl;
    private String file_name;
    
    private String a8FormFlowContent;
    private String a8FormNoflowContent;
    private String a8OrgInputContent;
    
    private DownloadBean downBean;
    private DeeResourceBean bean;

    /**
     * 保存
     * 
     * @return
     */
    public String save() {
        if (bean == null) {
            return ajaxForwardError("系统错误！");
        }
        
        String xslFileName = file_name;
        
        boolean userDefinedFlag = false;    // 默认为"非用户自定义"

        if ("0".equals(xslChgType)) {
            xslFileName = Constants.A8_FORM_FLOW;
        } else if ("1".equals(xslChgType)) {
            xslFileName = Constants.A8_FORM_NOFLOW;
        } else if ("2".equals(xslChgType)) {
            xslFileName = Constants.A8_ORGINPUT;
        } else {
            userDefinedFlag = true;
            if (StringUtils.isEmpty(xslFileName)) {
                xslFileName = UuidUtil.uuid() + ".xsl";
            }
        }
        
        DownloadBean dBean = null;
        if (userDefinedFlag) {              // 用户自定义XSLT
            dBean = new DownloadBean();
            dBean.setDOWNLOAD_ID(StringUtils.isBlank(download_id) ? UuidUtil.uuid() : download_id);
            dBean.setCONTENT(xsl);
            dBean.setFILENEME(xslFileName);
            if (StringUtils.isNotBlank(resourceId)) {
                DeeResourceBean refResource = new DeeResourceBean();
                refResource.setResource_id(resourceId);
                dBean.setRefResource(refResource);
            }
        }
        
        XSLTProcessorBean xsltBean = new XSLTProcessorBean();
        xsltBean.setName(resourceId);
        xsltBean.setXsl(xslFileName);
        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            xsltBean.setName(bean.getResource_id());
            bean.setDr(xsltBean);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
            bean.setDeeResourceTemplate(tmpBean);

            try {
                if (userDefinedFlag) {
                    dBean.setRefResource(bean);
                    // 写入自定义xslt
                    saveXsltFile(xslFileName);
                }
                xsltProcessorMgr.saveAdapter(bean, getFlowSubBean(bean), dBean);
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
                drb.setDr(xsltBean);
                if (userDefinedFlag) {
                    xsltProcessorMgr.updateAdapter(drb, dBean, 0);
                    // 写入自定义xslt
                    saveXsltFile(xslFileName);
                } else {
                    xsltProcessorMgr.updateAdapter(drb, dBean, 1);
                }
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
        xslChgType = "0";
        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                downBean = xsltProcessorMgr.getDownloadBeanByResourceId(resourceId);
                if (bean != null)
                {
                    if (bean.getResource_code().contains(Constants.A8_FORM_FLOW)) {
                        xslChgType = "0";
                    } else if (bean.getResource_code().contains(Constants.A8_FORM_NOFLOW)) {
                        xslChgType = "1";
                    } else if (bean.getResource_code().contains(Constants.A8_ORGINPUT)) {
                        xslChgType = "2";
                    } else {
                        xslChgType = "3";
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        a8FormFlowContent = loadA8FormContent(Constants.A8_FORM_FLOW);
        a8FormNoflowContent = loadA8FormContent(Constants.A8_FORM_NOFLOW);
        /*a8OrgInputContent = loadA8FormContent(Constants.A8_ORGINPUT);*/
        return XSLT_VIEW;
    }
    
    /**
     * 载入表单的xslt
     * 
     * @return
     */
    private String loadA8FormContent(String fileName) {
        String deeHome = DataChangeUtil.getProperty(Constants.DEE_HOME);;
        
        // 生成文件路径
        String filePath = deeHome + File.separator + 
                          "conf" + File.separator + fileName;
        
        BufferedReader input = null;
        StringBuffer contents = new StringBuffer();
        try {
            File file = new File(filePath);
            input = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
            return contents.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
    
    /**
     * 写入xslt文件
     * 
     * @param xslFileName 文件名
     * @throws IOException IO异常
     */
    private void saveXsltFile(String xslFileName) throws IOException {
        // DEE_HOME环境变量
        String deeHome = DataChangeUtil.getProperty(Constants.DEE_HOME);
        // xslt文件路径
        String xslFilePath = deeHome + File.separator + "conf" + File.separator + xslFileName;
        File file = new File(xslFilePath);

        if (!file.exists()) file.createNewFile();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(xsl.getBytes("utf-8"));
        
        fos.close();
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

    public String getXslChgType() {
        return xslChgType;
    }

    public void setXslChgType(String xslChgType) {
        this.xslChgType = xslChgType;
    }

    public DownloadBean getDownBean() {
        return downBean;
    }

    public void setDownBean(DownloadBean downBean) {
        this.downBean = downBean;
    }

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public String getXsl() {
        return xsl;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDownload_id() {
        return download_id;
    }

    public void setDownload_id(String download_id) {
        this.download_id = download_id;
    }

    public String getA8FormFlowContent() {
        return a8FormFlowContent;
    }

    public void setA8FormFlowContent(String a8FormFlowContent) {
        this.a8FormFlowContent = a8FormFlowContent;
    }

    public String getA8FormNoflowContent() {
        return a8FormNoflowContent;
    }

    public void setA8FormNoflowContent(String a8FormNoflowContent) {
        this.a8FormNoflowContent = a8FormNoflowContent;
    }

    public String getA8OrgInputContent() {
        return a8OrgInputContent;
    }

    public void setA8OrgInputContent(String a8OrgInputContent) {
        this.a8OrgInputContent = a8OrgInputContent;
    }
}
