package com.seeyon.dee.configurator.mgr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.dee.configurator.dao.FlowDao;
import com.seeyon.dee.configurator.dao.FlowTypeDao;
import com.seeyon.dee.configurator.mgr.FlowTypeMgr;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.mgr.impl.BaseMgrImpl;
import dwz.framework.exception.SystemException;

@Service("flowTypeMgr")
public class FlowTypeMgrImpl extends BaseMgrImpl<FlowTypeBean, String> implements FlowTypeMgr {

    @Autowired
    @Qualifier("flowDao")
    private FlowDao flowDao;
    
    private FlowTypeDao flowTypeDao;
    
    @Autowired
    @Qualifier("flowTypeDao")
    @Override
    public void setBaseDao(BaseDao<FlowTypeBean, String> baseDao) {
        super.baseDao = baseDao;
        this.flowTypeDao = (FlowTypeDao)baseDao;
    }

    /**
     * 是否有子节点
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    @Override
    public boolean hasChildFlowType(String flowTypeId) throws SystemException {
        return flowTypeDao.hasChildFlowType(flowTypeId);
    }

    /**
     * 是否有任务
     * 
     * @param flowTypeId
     * @return
     * @throws SystemException
     */
    @Override
    public boolean hasFlow(String flowTypeId) throws SystemException {
        return flowDao.countByFlowTypeId(flowTypeId);
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
        return flowTypeDao.hasTheSameName(parentId, name, id);
    }
}
