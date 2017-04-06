package com.seeyon.dee.configurator.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.SyncDao;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("syncDao")
public class SyncDaoImpl extends BaseDaoImpl<SyncBean, String> implements SyncDao {

    /**
     * 批量删除
     * 
     * @param ids
     * @throws SystemException
     */
    @Override
    public int delSyncs(String[] ids) throws SystemException {
        if (ids.length > 0) {
            Query query = getSession().createQuery("delete SyncBean rb where rb.sync_id in (:ids)")
                .setParameterList("ids", ids);
            return query.executeUpdate();
        }
        return 0;
    }
}
