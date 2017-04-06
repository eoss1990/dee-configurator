package com.seeyon.dee.configurator.dao;

import java.util.Collection;

import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

/**
 * 重发Dao
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午12:31:07
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public interface RedoDao extends BaseDao<RedoBean, String> {
    
    /**
     * 根据同步ID，获取重发列表
     * 
     * @param syncId 同步ID
     * @return
     * @throws SystemException
     */
    public Collection<RedoBean> listRedoBySyncId(String syncId) throws SystemException;

    /**
     * 批量删除
     * 
     * @param ids
     * @throws SystemException
     */
    public int delRedos(String[] ids) throws SystemException;
}
