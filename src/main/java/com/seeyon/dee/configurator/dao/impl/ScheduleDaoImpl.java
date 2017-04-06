package com.seeyon.dee.configurator.dao.impl;

import org.springframework.stereotype.Repository;

import com.seeyon.dee.configurator.dao.ScheduleDao;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;

import dwz.framework.common.dao.impl.BaseDaoImpl;

@Repository("scheduleDao")
public class ScheduleDaoImpl extends BaseDaoImpl<ScheduleBean, String> implements ScheduleDao {

}
