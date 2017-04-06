package com.seeyon.dee.configurator.dao;

import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

public interface FlowTypeDao extends BaseDao<FlowTypeBean, String> {
    
    /**
     * 是否包含子节点
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    public boolean hasChildFlowType(String flowTypeId) throws SystemException;
    
    /**
     * 同一节点下是否存在同名
     * 
     * @param parentId
     * @param name
     * @param id
     * @return
     */
    public boolean hasTheSameName(String parentId, String name, String id) throws SystemException;
}
