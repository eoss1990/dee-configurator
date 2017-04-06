package com.seeyon.dee.configurator.dao.impl;

import java.util.Collection;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.RedoDao;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("redoDao")
public class RedoDaoImpl extends BaseDaoImpl<RedoBean, String> implements RedoDao {

    /**
     * 根据同步ID，获取重发列表
     * 
     * @param syncId 同步ID
     * @return
     * @throws SystemException
     */
    @Override
    public Collection<RedoBean> listRedoBySyncId(String syncId) throws SystemException {
        String hql = "from RedoBean rb where rb.syncBean.sync_id=?";
        Object[] params = new Object[1];
        params[0] = syncId;
        return this.listAll(hql, 0, 99999, params);
    }

    /**
     * 批量删除
     * 
     * @param ids
     * @throws SystemException
     */
    @Override
    public int delRedos(String[] ids) throws SystemException {
        if (ids.length > 0) {
            Query query = getSession().createQuery("delete RedoBean rb where rb.redo_id in (:ids)")
                .setParameterList("ids", ids);
            return query.executeUpdate();
        }
        return 0;
    }

}
