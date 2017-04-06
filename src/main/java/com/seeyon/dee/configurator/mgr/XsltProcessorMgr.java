package com.seeyon.dee.configurator.mgr;

import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.exception.SystemException;

/**
 * XSLT格式转换管理
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午12:12:42
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public interface XsltProcessorMgr {
    
    /**
     * 根据资源ID获取downloadbean
     * 
     * @param resourceId
     * @return
     * @throws SystemException
     */
    public DownloadBean getDownloadBeanByResourceId(String resourceId) throws SystemException;
    
    /**
     * 新增转换器
     * 
     * @param drb
     * @param sub
     * @param dBean
     * @throws SystemException
     */
    public void saveAdapter(DeeResourceBean drb, FlowSubBean sub, DownloadBean dBean) throws SystemException;
    
    /**
     * 修改转换器
     * 
     * @param drb
     * @param dBean
     * @param downFlag 0:新增或修改，1:删除
     * @throws SystemException
     */
    public void updateAdapter(DeeResourceBean drb, DownloadBean dBean, int downFlag) throws SystemException;
}
