package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.FlowModuleDao;
import com.seeyon.v3x.dee.common.db.flow.model.FlowModuleBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("flowModuleDao")
public class FlowModuleDaoImpl extends BaseDaoImpl<FlowModuleBean, String> implements FlowModuleDao {

}
