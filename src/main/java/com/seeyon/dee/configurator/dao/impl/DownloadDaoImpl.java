package com.seeyon.dee.configurator.dao.impl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.DownloadDao;
import com.seeyon.v3x.dee.common.db.download.model.DownloadBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("downloadDao")
public class DownloadDaoImpl extends BaseDaoImpl<DownloadBean, String> implements DownloadDao {

    private static Logger logger = Logger.getLogger(DownloadDaoImpl.class.getName());
    
    /**
     * 根据资源ID，获取download
     * 
     * @param resourceId 资源ID
     * @return
     * @throws SystemException
     */
    @Override
    public DownloadBean getByResourceId(String resourceId) throws SystemException {
        String hql = "from DownloadBean d where d.refResource.resource_id=?";

        try {
            Query query = getSession().createQuery(hql);
            query.setParameter(0, resourceId);
            
            return (DownloadBean)query.uniqueResult();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    /**
     * 根据资源ID，删除download
     * 
     * @param resourceId 资源ID
     * @return
     * @throws SystemException
     */
    @Override
    public int deleteByResourceId(String resourceId) throws SystemException {
        String hql = "delete from DownloadBean d where d.refResource.resource_id=?";
        
        try {
            Query query = getSession().createQuery(hql);
            query.setParameter(0, resourceId);
            
            return query.executeUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

}
