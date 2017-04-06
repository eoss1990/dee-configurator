package com.seeyon.dee.configurator.action;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import com.opensymphony.xwork2.ActionContext;
import com.seeyon.dee.configurator.mgr.FlowManagerMgr;
import com.seeyon.dee.configurator.mgr.ScheduleMgr;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.schedule.QuartzManager;
/**
 * 定时器管理
 * @author yangyu
 *
 */
public class ScheduleAction extends BaseAction{

	private static final long serialVersionUID = 3620217458136738265L;
	private static Log log = LogFactory.getLog(ScheduleAction.class);
	@Autowired
    @Qualifier("scheduleMgr")
	private ScheduleMgr scheduleMgr;
	
	@Autowired
    @Qualifier("flowManager")
	private FlowManagerMgr flowMgr;
	
	private ScheduleBean bean;
	private int retFixed;
	private String ids;
	
	/**
	 * 定时器列表
	 * @return
	 */
	public String scheduleList()
	{
		try {
			int pageNum = this.getPageNum() > 0 ? this.getPageNum() - 1 : 0;
			int startIndex = pageNum * getNumPerPage();
			Collection<ScheduleBean> scheduleList = scheduleMgr.searchScheduleBean(realOrderField(),startIndex, getNumPerPage(),getKeywords());
			ActionContext.getContext().put("scheduleList", scheduleList);
			if(getKeywords()==null)
				this.setTotalCount(scheduleMgr.searchScheduleBeanNum("ScheduleBean","dis_name",""));
		    else
		    	this.setTotalCount(scheduleMgr.searchScheduleBeanNum("ScheduleBean","dis_name",getKeywords()));
			
		} catch (Throwable e) {
			/*ex.printStackTrace();*/
			log.error(e);
		}
		return "list";
	}
	
	/**
	 * 定时器详细配置页面回显
	 * @return
	 */
	public String detail()
	{
		try {
			String id = request.getParameter("uid");
			bean = id==null?new ScheduleBean():scheduleMgr.getScheduleBeanByPk(id);
			retFixed = getISFixed(bean.getQuartz_code());
		} catch (SystemException e) {
			log.error("请检查数据是否存在"+e.getMessage(),e);
		}
		return INPUT;
	}
	
	private int getISFixed(String dateCode){
		if(dateCode != null && "1".equalsIgnoreCase(dateCode.substring(0,1)))
			return 1;
		else
			return 0;
	}
	
	/**
	 * 适配器保存或修改
	 * @return
	 */
	public String save()
	{
		try
		{
			DEEClient client = new DEEClient();
			bean.setEnable("1".equals(request.getParameter("isEnable")));
			bean.setCreate_time(time());
			bean.setFlow_id(bean.getFlow().getFLOW_ID());
			if(bean.getSchedule_id()==null||bean.getSchedule_id().length()==0){
				
				scheduleMgr.save(bean);
				GenerationCfgUtil.getInstance().generationMainFile(GenerationCfgUtil.getDEEHome());
				client.refreshContext();
				QuartzManager.getInstance().refresh();
				return ajaxForwardSuccess("新建成功",bean.getSchedule_id());
				
			}else{
				
				scheduleMgr.update(bean);
				GenerationCfgUtil.getInstance().generationMainFile(GenerationCfgUtil.getDEEHome());
				client.refreshContext();
				QuartzManager.getInstance().refresh();
				return ajaxForwardSuccess("修改成功！");
			}
		}catch (TransformException e) {
			log.error(e.getMessage(),e);
			return ajaxForwardError("操作失败！");
		}catch (SchedulerException e) {
			log.error("引擎刷新上下文异常"+e.getLocalizedMessage());
			return ajaxForwardError("操作失败！");
		} catch (Throwable e) {
			log.error("引擎刷新上下文异常"+e.getLocalizedMessage());
			return ajaxForwardError("操作失败！");
		}
	}
	
	/**
	 * 查找带回任务列表
	 * @return
	 */
	public String deeList()
	{
		try {
			int pageNum = this.getPageNum() > 0 ? this.getPageNum() - 1 : 0;
			int startIndex = pageNum * getNumPerPage();
			Collection<FlowBean> flowList = flowMgr.searchFlowBean(realOrderField(),startIndex, getNumPerPage(),getKeywords());
			ActionContext.getContext().put("flowList", flowList);
			if(getKeywords()==null)
		    	setTotalCount(flowMgr.searchFlowBeanNum("FlowBean","DIS_NAME",""));
		    else
		    	setTotalCount(flowMgr.searchFlowBeanNum("FlowBean","DIS_NAME",getKeywords()));
		} catch (Throwable e) {
			/*ex.printStackTrace();*/
			log.error(e);
		}
		return "bringback";
	}
	
	/**
	 * 批量删除定时器
	 * @return
	 */
	public String deleteAll()
	{
		String[] sdbids = ids.split(",");
		try{
			for (String id : sdbids) {
				scheduleMgr.delete(id);			
			}
			GenerationCfgUtil.getInstance().generationMainFile(GenerationCfgUtil.getDEEHome());
			DEEClient client = new DEEClient();
			client.refreshContext();
			QuartzManager.getInstance().refresh();
			return ajaxForwardSuccess("删除成功！");
		} catch (TransformException e) {
			log.error(e.getMessage(),e);
			return ajaxForwardError("操作失败！");
		}catch (SchedulerException e) {
			log.error("引擎刷新上下文异常"+e.getLocalizedMessage());
			return ajaxForwardError("操作失败！");
		} catch (Throwable e) {
			log.error("引擎刷新上下文异常"+e.getLocalizedMessage());
			return ajaxForwardError("操作失败！");
		}
	}

	/**
	 * 获取系统时间
	 * @return 系统时间
	 */
	public String time()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间   
		return formatter.format(curDate);
	}
	
	public ScheduleBean getBean() {
		return bean;
	}

	public void setBean(ScheduleBean bean) {
		this.bean = bean;
	}

	public int getRetFixed() {
		return retFixed;
	}

	public void setRetFixed(int retFixed) {
		this.retFixed = retFixed;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
