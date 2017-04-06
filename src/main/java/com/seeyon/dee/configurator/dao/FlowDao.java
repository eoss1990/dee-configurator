package com.seeyon.dee.configurator.dao;

import java.util.List;

import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

public interface FlowDao extends BaseDao<FlowBean, String> {
    
    /**
     * 查询flowType下的记录数
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    boolean countByFlowTypeId(String flowTypeId) throws SystemException;
    
    public List<String> findAllDisName(final String sql) throws SystemException;
}
