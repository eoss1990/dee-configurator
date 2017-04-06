package com.seeyon.dee.configurator.dao;

import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

public interface FlowSubDao extends BaseDao<FlowSubBean, String> {

	public String getFlowIdBySourceId(String sql) throws SystemException;
}
