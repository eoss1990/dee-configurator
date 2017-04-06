package com.seeyon.dee.configurator.mgr;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;

import dwz.framework.exception.SystemException;

public interface ScheduleMgr {
	
	public Collection<ScheduleBean> searchScheduleBean(String orderField,int startIndex, int count ,String keyWords) throws SystemException;

	public Integer searchScheduleBeanNum(String className,String fieldName,String fieldValue) throws SystemException;
	
	public ScheduleBean getScheduleBeanByPk(String id) throws SystemException;
	
	@Transactional
	public void save(ScheduleBean sdb) throws SystemException;
	
	@Transactional
	public void update(ScheduleBean sdb) throws SystemException;
	
	@Transactional
	public void delete(String id) throws SystemException;
}
