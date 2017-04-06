package com.seeyon.dee.configurator.mgr.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.seeyon.dee.configurator.dao.ScheduleDao;
import com.seeyon.dee.configurator.mgr.ScheduleMgr;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;

import dwz.framework.exception.SystemException;
import dwz.framework.util.DeeList;

@Service("scheduleMgr")
public class ScheduleMgrImpl implements ScheduleMgr {
	
	@Autowired
    @Qualifier("scheduleDao")
	private ScheduleDao ScheduleDao;

	@Override
	public Collection<ScheduleBean> searchScheduleBean(String orderField,
			int startIndex, int count, String keyWords) throws SystemException {
		// TODO Auto-generated method stub
		String hql = null;
		ArrayList<ScheduleBean> eaList = new ArrayList<ScheduleBean>();
		DeeList ScheduleBeanList = new DeeList();
		if (keyWords != null && !keyWords.equals(""))
		{
			hql = ScheduleBeanList.createQueryString("ScheduleBean", false, orderField, "dis_name", keyWords,"create_time");
		}
		else
		{		
			hql = ScheduleBeanList.createQueryString("ScheduleBean", false, orderField,"create_time");
		}
		
//		String hql = "select a from FlowBean as a order by a.CREATE_TIME ";
		Collection<ScheduleBean> ScheduleBean = ScheduleDao.findByQuery(hql, startIndex, count);
		for(ScheduleBean sdb: ScheduleBean)
		{
			eaList.add(sdb);
		}
		
		return eaList;
	}

	@Override
	public Integer searchScheduleBeanNum(String className,String fieldName,String fieldValue) throws SystemException {
		// TODO Auto-generated method stub
		DeeList ScheduleBeanList = new DeeList();
		String hql = ScheduleBeanList.createQueryString(className, true, null, fieldName, fieldValue, null);
		Number totalCount = this.ScheduleDao.countByQuery(hql);
		return totalCount.intValue();
	}

	@Override
	public ScheduleBean getScheduleBeanByPk(String id) throws SystemException {
		// TODO Auto-generated method stub
		return ScheduleDao.get(id);
	}

	@Override
	public void save(ScheduleBean sdb) throws SystemException {
		// TODO Auto-generated method stub
		String uuid = UuidUtil.uuid();
		sdb.setSchedule_id(uuid);
		sdb.setSchedule_name(uuid);
		generateScheduleCode(sdb);
		ScheduleDao.save(sdb);
		
	}

	@Override
	public void update(ScheduleBean sdb) throws SystemException {
		// TODO Auto-generated method stub
		generateScheduleCode(sdb);
		ScheduleDao.update(sdb);
	} 
	
	public void generateScheduleCode(ScheduleBean bean){
		StringBuffer code = new StringBuffer();
		String cron ="";
		
		String quartzCode = bean.getQuartz_code();
		if(quartzCode!=null){
			String[] arr = quartzCode.split(",");
			if(arr.length>1){
				if("0".equalsIgnoreCase(arr[0])){
					// 秒 分 小时 月内日期 月 周内日期 年（可选字段）
					int count = Integer.parseInt(arr[1]);
					int qty = Integer.parseInt(arr[2]);
					if(qty==1){// 分钟
						cron ="0 0/" + count + " * * * ? ";
					}else if(qty==2){// 小时
						cron ="0 0 0/" + count + " * * ? ";
					}else if(qty==3){// 天
						cron ="0 0 0/" + (count*24) + " * * ? ";
					}else if(qty==4){// 周
						cron ="0 0 0/" + (count*24*7) + " * * ? ";
					}else if(qty==5){// 月
						cron ="0 0 0 1 1/" + count + " ? ";
					}
				}
				else{
					String[] weekArr = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
					if("3".equalsIgnoreCase(arr[1])){ //月
						cron ="0 " + arr[4] + " " + arr[3] + " " + arr[2] + " * ?";
					}
					else if("2".equalsIgnoreCase(arr[1])){ //周
						int weekInt = Integer.parseInt(arr[2]);
						cron ="0 " + arr[4] + " " + arr[3] + " ? * " + weekArr[weekInt-1];
					}
					else if("1".equalsIgnoreCase(arr[1])){//天
						cron ="0 " + arr[4] + " " + arr[3] + " * * ?";
					}
				}
			}
		}
		code.append("<schedule name=\"").append(bean.getSchedule_id()).append("\" class=\"com.seeyon.v3x.dee.schedule.QuartzReport\">");
		
		code.append("<property name=\"flow\" value=\"").append(bean.getFlow_id()).append("\"  />");
		code.append("<property name=\"quartzTime\" value=\"").append(cron).append("\" />");
		code.append("</schedule>");
		bean.setSchedule_code(code.toString());
	}

	@Override
	public void delete(String id) throws SystemException {
		// TODO Auto-generated method stub
		ScheduleDao.delete(id);
	}

}
