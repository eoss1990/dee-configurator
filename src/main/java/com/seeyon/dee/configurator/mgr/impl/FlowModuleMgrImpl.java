package com.seeyon.dee.configurator.mgr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.seeyon.dee.configurator.dao.FlowModuleDao;
import com.seeyon.dee.configurator.mgr.FlowModuleMgr;
import com.seeyon.v3x.dee.common.db.flow.model.FlowModuleBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.mgr.impl.BaseMgrImpl;

@Service("flowModuleMgr")
public class FlowModuleMgrImpl extends BaseMgrImpl<FlowModuleBean, String> implements FlowModuleMgr {

    private FlowModuleDao flowModuleDao;
    
    @Autowired
    @Qualifier("flowModuleDao")
    @Override
    public void setBaseDao(BaseDao<FlowModuleBean, String> baseDao) {
        super.baseDao = baseDao;
        this.flowModuleDao = (FlowModuleDao)baseDao;
    }

}
