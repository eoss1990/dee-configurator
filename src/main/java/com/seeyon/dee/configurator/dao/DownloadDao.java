package com.seeyon.dee.configurator.dao;

import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

public interface DownloadDao extends BaseDao<DownloadBean, String> {
    
    /**
     * 根据资源ID，获取download
     * 
     * @param resourceId 资源ID
     * @return
     * @throws SystemException
     */
    public DownloadBean getByResourceId(String resourceId) throws SystemException;
    
    /**
     * 根据资源ID，删除download
     * 
     * @param resourceId 资源ID
     * @return
     * @throws SystemException
     */
    public int deleteByResourceId(String resourceId) throws SystemException;
}
