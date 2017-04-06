package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.ResourceTypeDao;
import com.seeyon.v3x.dee.common.db.code.model.ResourceTypeBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("resourceTypeDao")
public class ResourceTypeDaoImpl extends BaseDaoImpl<ResourceTypeBean, String> implements ResourceTypeDao {

}
