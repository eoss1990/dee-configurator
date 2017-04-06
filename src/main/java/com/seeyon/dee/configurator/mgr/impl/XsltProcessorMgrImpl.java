package com.seeyon.dee.configurator.mgr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.dee.configurator.dao.DownloadDao;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.XsltProcessorMgr;
import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.exception.SystemException;

@Service("xsltProcessorMgr")
public class XsltProcessorMgrImpl implements XsltProcessorMgr {

    @Autowired
    @Qualifier("downloadDao")
    private DownloadDao downloadDao;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    /**
     * 根据资源ID获取downloadbean
     * 
     * @param resourceId
     * @return
     * @throws SystemException
     */
    @Override
    public DownloadBean getDownloadBeanByResourceId(String resourceId) throws SystemException {
        return downloadDao.getByResourceId(resourceId);
    }

    /**
     * 新增转换器
     * 
     * @param drb
     * @param sub
     * @param dBean
     * @throws SystemException
     */
    @Override
    public void saveAdapter(DeeResourceBean drb, FlowSubBean sub, DownloadBean dBean) throws SystemException {
        deeAdapterMgr.saveAdapter(drb, sub);
        if (dBean != null) {
            downloadDao.save(dBean);
        }
    }

    /**
     * 修改转换器
     * 
     * @param drb
     * @param dBean
     * @param downFlag 0:新增或修改，1:删除
     * @throws SystemException
     */
    @Override
    public void updateAdapter(DeeResourceBean drb, DownloadBean dBean, int downFlag) throws SystemException {
        deeAdapterMgr.updateAdapter(drb);
        if (downFlag == 0) {
            downloadDao.saveOrUpdate(dBean);
        } else if (downFlag == 1) {
            downloadDao.deleteByResourceId(drb.getResource_id());
        }
    }
}
