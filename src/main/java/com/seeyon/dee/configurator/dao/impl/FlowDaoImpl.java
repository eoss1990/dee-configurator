package com.seeyon.dee.configurator.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.FlowDao;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("flowDao")
public class FlowDaoImpl extends BaseDaoImpl<FlowBean, String> implements FlowDao {

    /**
     * 查询flowType下的记录数
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    @Override
    public boolean countByFlowTypeId(String flowTypeId) throws SystemException {
        String hql = "select count(*) from FlowBean fb where fb.flowType.FLOW_TYPE_ID = :flowTypeId";

        Query query = getSession().createQuery(hql);
        query.setParameter("flowTypeId", flowTypeId);

        Long count = (Long)query.uniqueResult();
        return count > 0;
    }


    @SuppressWarnings("unchecked")
    public List<String> findAllDisName(String sql) throws SystemException{
        Query query = getSession().createSQLQuery(sql);
        List<String> list=query.list();
        return list;
    }
}
