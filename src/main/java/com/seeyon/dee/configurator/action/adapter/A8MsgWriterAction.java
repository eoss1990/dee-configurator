package com.seeyon.dee.configurator.action.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.bean.A8MsgWriterBean;
import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.dee.configurator.dto.KeyValue;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;

public class A8MsgWriterAction extends BaseAction {
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1236462379239500839L;

	private static Logger logger = Logger.getLogger(A8MsgWriterAction.class.getName());

    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
	private static final String A8MSGWRITER = "a8msgwriter";
    private static final String A8_USERNAME = "service-admin";
    private static final String A8_INTERFACENAME = "messageService";
    private static final String A8_XMLNS = "http://impl.message.services.v3x.seeyon.com";
    private static final String A8_METHODNAME = "sendMessageByLoginName";
	
    private String flowId;          // flow标识
    private String resourceId;      // 资源ID --> 修改时使用
    private int sort;               // 排序号   --> 新增时使用
    private String resourceType;
    private String selSrv; //增加类型替代resourceType保存
    private List<KeyValue> items;
    private DeeResourceBean bean;
    private A8MsgWriterBean msgb;
    /**
     * 查看及编辑
     * 
     * @return
     */
    public String view() {
        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                if (bean != null && bean.getDeeResourceTemplate() != null){
                    bean.setResource_template_id(bean.getDeeResourceTemplate().getResource_template_id());
                    bean.setResource_template_name(bean.getDeeResourceTemplate().getResource_template_name());
                    msgb = (A8MsgWriterBean) new ConvertDeeResourceBean(bean).getResource();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        else{
        	msgb = new A8MsgWriterBean();
        	msgb.setUserName(A8_USERNAME);
        	msgb.setInterfaceName(A8_INTERFACENAME);
        	msgb.setXmlns(A8_XMLNS);
        	msgb.setMethodName(A8_METHODNAME);
        }
        return A8MSGWRITER;
    }
    
	private String chkVals(){
		String retMsg = "";
		if(bean.getDis_name() == null || "".equals(bean.getDis_name())){
			retMsg = "名称不能为空！";
			return retMsg;
		}
		if(msgb.getA8url() == null || "".equals(msgb.getA8url())){
			retMsg = "A8地址不能为空！";
			return retMsg;
		}
		if(msgb.getPassword() == null || "".equals(msgb.getPassword())){
			retMsg = "A8WS密码不能为空！";
			return retMsg;
		}
		if(msgb.getMsgContent() == null || "".equals(msgb.getMsgContent())){
			retMsg = "消息内容不能为空！";
			return retMsg;
		}
		Map<String, String> map = new LinkedHashMap<String, String>();
        if(items == null || items.size() < 1){
			retMsg = "消息接收人不能为空！";
			return retMsg;
        }
        int realLen = 0;
        for(KeyValue kv:items){
        	if(kv == null)
        		continue;
        	if(kv.getKey() == null || "".equals(kv.getKey())){
    			retMsg = "消息接收人不能为空！";
    			return retMsg;
        	}
        	realLen++;
        	map.put(kv.getKey(), kv.getValue());
        }
        if(realLen < 1){
			retMsg = "消息接收人不能为空！";
			return retMsg;
        }
        if(realLen != map.size()){
			retMsg = "有相同的消息接收人,不能保存！";
			return retMsg;
        }
        msgb.setMap(map);
		return retMsg;
	}
    /**
     * 保存
     * 
     * @return
     */
    public String save() {
		if(flowId == null || "".equals(flowId)){
			return ajaxForwardError("FlowId为空，不能作任何保存！");
		}
    	if (msgb == null || bean == null) {
    		return ajaxForwardError("系统错误！");
    	}
		String retMsg = chkVals();
		if(!"".equals(retMsg)){
			//数据校验
			return ajaxForwardError(retMsg);
		}        
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            bean.setDr(msgb);
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
//          tmpBean.setResource_template_id(resourceType);
           tmpBean.setResource_template_id(selSrv);
            bean.setDeeResourceTemplate(tmpBean);
            
            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("新增异常！");
            }
        } else {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDr(msgb);
                drb.setDis_name(bean.getDis_name());
                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,drb.getFlowSub().getSort(),flowId);
                return ajaxForwardSuccess("修改成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("修改异常！");
            }
        }
    }
    
    public FlowSubBean getFlowSubBean(DeeResourceBean drb) {
        FlowSubBean sub = new FlowSubBean();
        FlowBean flowBean = new FlowBean();
        flowBean.setFLOW_ID(flowId);
        
        sub.setFlow_sub_id(UuidUtil.uuid());
        sub.setFlow(flowBean);
        sub.setDeeResource(drb);
        sub.setSort(sort);
        
        return sub;
    }
    
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public DeeResourceBean getBean() {
		return bean;
	}
	public void setBean(DeeResourceBean bean) {
		this.bean = bean;
	}

	public A8MsgWriterBean getMsgb() {
		return msgb;
	}

	public void setMsgb(A8MsgWriterBean msgb) {
		this.msgb = msgb;
	}

	public List<KeyValue> getItems() {
		return items;
	}

	public void setItems(List<KeyValue> items) {
		this.items = items;
	}

	public String getSelSrv() {
		return selSrv;
	}

	public void setSelSrv(String selSrv) {
		this.selSrv = selSrv;
	}
}
