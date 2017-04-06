package com.seeyon.dee.configurator.action;

import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.ActionContext;
import com.seeyon.dee.configurator.mgr.FlowBaseMgr;
import com.seeyon.dee.configurator.mgr.FlowManagerMgr;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;
import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;

public class DeeRestSrvAction extends BaseAction {

	/**
	 * Rest服务管理
	 */
	private static final long serialVersionUID = -6247811600885590853L;
	private final static Log log = LogFactory.getLog(DeeRestSrvAction.class);
	
	@Autowired
    @Qualifier("flowManager")
	private FlowManagerMgr flowMgr;
	
	@Autowired
    @Qualifier("flowBaseMgr")
	private FlowBaseMgr flowBaseMgr;

	private String flowId;  //任务ID
	private String jsonData;  //返回参数数据

	//查询rest服务地址
	public String flowRestAdr(){
		
		return INPUT;
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
		return "bringBack";
	}
	
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	
	public String getRestSrvPath(){
		JSONObject jObj = new JSONObject();
		String srvPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		srvPath = srvPath + "/rest/flow/" + flowId;
		jObj.put("path", srvPath);
		jsonData = jObj.toString();
		return ajaxForwardSuccess("获取地址成功！");
	}

	public String selectOneFlow(){
		try {
			List<ParameterBean> fbList = flowBaseMgr.findParamsByFlowId(flowId);
			if(fbList == null)
				return ajaxForwardSuccess("获取参数成功！");
			JSONObject jObj = null;
			JSONArray jsList = new JSONArray();
			for(ParameterBean fb:fbList){
				if(fb == null)
					continue;
				jObj = new JSONObject();
				jObj.put("paramName", fb.getPARA_NAME());
				jsList.add(jObj);
			}
			jsonData = jsList.toString();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			return ajaxForwardError("获取参数异常："+e.getLocalizedMessage());
		}
		return ajaxForwardSuccess("获取参数成功！");
	}
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getJsonData() {
		return jsonData;
	}

}
