package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.FlowMetaDataDao;
import com.seeyon.v3x.dee.common.db.flow.model.FlowMetaDataBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("flowMetaDataDao")
public class FlowMetaDataDaoImpl extends BaseDaoImpl<FlowMetaDataBean, String> implements FlowMetaDataDao {

}
