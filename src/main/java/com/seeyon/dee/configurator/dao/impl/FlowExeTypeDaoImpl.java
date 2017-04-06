package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.FlowExeTypeDao;
import com.seeyon.v3x.dee.common.db.flow.model.FlowExeTypeBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("flowExeTypeDao")
public class FlowExeTypeDaoImpl extends BaseDaoImpl<FlowExeTypeBean, String> implements FlowExeTypeDao {

}
