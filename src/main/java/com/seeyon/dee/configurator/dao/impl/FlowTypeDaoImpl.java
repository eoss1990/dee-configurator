package com.seeyon.dee.configurator.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.FlowTypeDao;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;
import dwz.framework.exception.SystemException;

@Repository("flowTypeDao")
public class FlowTypeDaoImpl extends BaseDaoImpl<FlowTypeBean, String> implements FlowTypeDao {

    /**
     * 是否包含子节点
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    @Override
    public boolean hasChildFlowType(String flowTypeId) throws SystemException {
        String hql = "select count(*) from FlowTypeBean fb where fb.PARENT_ID = :flowTypeId";
        
        Query query = getSession().createQuery(hql);
        query.setParameter("flowTypeId", flowTypeId);
        
        Long count = (Long)query.uniqueResult();
        return count > 0;
    }

    /**
     * 同一节点下是否存在同名
     * 
     * @param parentId
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean hasTheSameName(String parentId, String name, String id) throws SystemException {
        String hql = "select count(*) from FlowTypeBean fb where fb.PARENT_ID = :parentId and fb.FLOW_TYPE_NAME=:name";
        if (StringUtils.isNotBlank(id)) {
            hql += " and fb.FLOW_TYPE_ID != " + id;
        }
        
        Query query = getSession().createQuery(hql);
        query.setParameter("parentId", parentId);
        query.setParameter("name", name);

        Long count = (Long)query.uniqueResult();
        return count > 0;
    }

}
