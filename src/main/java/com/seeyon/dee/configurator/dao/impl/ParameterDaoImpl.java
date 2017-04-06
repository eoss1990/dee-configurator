package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.ParameterDao;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("parameterDao")
public class ParameterDaoImpl extends BaseDaoImpl<ParameterBean, String> implements ParameterDao {

}
