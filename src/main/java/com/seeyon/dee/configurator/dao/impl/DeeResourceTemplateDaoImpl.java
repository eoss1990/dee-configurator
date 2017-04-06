package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.DeeResourceTemplateDao;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("deeResourceTemplateDao")
public class DeeResourceTemplateDaoImpl extends BaseDaoImpl<DeeResourceTemplateBean, String> implements DeeResourceTemplateDao {

}
