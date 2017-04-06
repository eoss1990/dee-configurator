package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.MetaFlowDao;
import com.seeyon.v3x.dee.common.db.metaflow.model.MetaFlowBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("metaFlowDao")
public class MetaFlowDaoImpl extends BaseDaoImpl<MetaFlowBean, String> implements MetaFlowDao {

}
