package com.seeyon.dee.configurator.dao;

import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

/**
 * 同步Dao
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午12:30:45
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public interface SyncDao extends BaseDao<SyncBean, String> {
    
    /**
     * 批量删除
     * 
     * @param ids
     * @throws SystemException
     */
    public int delSyncs(String[] ids) throws SystemException;
}
