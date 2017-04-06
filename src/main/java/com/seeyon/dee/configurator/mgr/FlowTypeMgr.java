package com.seeyon.dee.configurator.mgr;

import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;

import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.exception.SystemException;

public interface FlowTypeMgr extends BaseMgr<FlowTypeBean, String> {
    
    /**
     * 是否有子节点
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    public boolean hasChildFlowType(String flowTypeId) throws SystemException;
    
    /**
     * 是否有任务
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    public boolean hasFlow(String flowTypeId) throws SystemException;
    
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
