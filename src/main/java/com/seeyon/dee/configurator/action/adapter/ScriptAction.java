package com.seeyon.dee.configurator.action.adapter;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.v3x.dee.bean.ScriptBean;
import com.seeyon.ctp.form.modules.engin.formula.base.function.FunctionUtil;
import com.seeyon.dee.configurator.mgr.CodeLibMgr;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.dee.configurator.mgr.DeeResourceManager;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceTemplateBean;
import com.seeyon.v3x.dee.context.GroovyScriptClosure;
import com.seeyon.v3x.dee.script.ScriptRunner;
import com.seeyon.v3x.dee.util.Constants;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;
import dwz.framework.util.DateUtil;
import dwz.framework.util.ProcessUtil;
import dwz.framework.util.functionUtil;

/**
 * 脚本action
 * 
 * @author Zhang.Fubing
 * @date 2013-5-14下午10:48:44
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class ScriptAction extends BaseAction {
    
    private static final long serialVersionUID = -2821794615141577116L;

    private static Logger logger = Logger.getLogger(ScriptAction.class.getName());
    
    @Autowired
    @Qualifier("deeResourceManager")
    private DeeResourceManager deeResourceManager;
    
    @Autowired
    @Qualifier("deeAdapterMgr")
    private DeeAdapterMgr deeAdapterMgr;
    
    @Autowired
    @Qualifier("codeLibMgr")
    private CodeLibMgr codeLibMgr;
    
    private static final String SCRIPT_VIEW = "script";
    
    private String flowId;          // flow标识
    private String resourceId;      // 资源ID --> 修改时使用
    private int sort;               // 排序号   --> 新增时使用
    private String resourceType;
    private String writerExist;
    
    private ScriptBean scriptBean;
    private DeeResourceBean bean;
    
    private String functionTree;
    
    public String getFunctionTree() {
		return functionTree;
	}

	public void setFunctionTree(String functionTree) {
		this.functionTree = functionTree;
	}

	/**
     * 保存脚本
     * 
     * @return
     */
    public String compile() {
        if (scriptBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        try {
        	String prefix = GroovyScriptClosure.getClosure();
			new ScriptRunner().complie(prefix + "\n" + scriptBean.getScript());
            //编译完后，将缓存清除重新加载
            GenerationCfgUtil.getInstance().refreshScript();
        } catch (Exception e) {
        	logger.error("脚本执行异常：" + e.getLocalizedMessage(), e);
        	return ajaxForwardError("脚本编译未通过：" + e.getLocalizedMessage());
        }
        return ajaxForwardSuccess("编译成功！", "编译成功！");
    }
	
	/**
     * 保存脚本
     * 
     * @return
     */
    public String save() {
        if (scriptBean == null || bean == null) {
            return ajaxForwardError("系统错误！");
        }
        if (resourceId == null || StringUtils.isBlank(resourceId)) {
            bean.setResource_id(UuidUtil.uuid());
            bean.setResource_name(bean.getResource_id());
            bean.setCreate_time(DateUtil.getSysTime());
            
            DeeResourceTemplateBean tmpBean = new DeeResourceTemplateBean();
            tmpBean.setResource_template_id(resourceType);
            bean.setDeeResourceTemplate(tmpBean);
            
            scriptBean.setName(bean.getResource_id());
            bean.setDr(scriptBean);
            
            try {
                deeAdapterMgr.saveAdapter(bean, getFlowSubBean(bean));
                String infor = ProcessUtil.toString(bean.getResource_id(),Constants.ADAPTER_SAVE,sort,flowId);
                return ajaxForwardSuccess("新增成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
        } else {
            try {
                DeeResourceBean drb = deeResourceManager.get(resourceId);
                drb.setDis_name(bean.getDis_name());
                
                scriptBean.setName(drb.getResource_id());
                drb.setDr(scriptBean);

                deeAdapterMgr.updateAdapter(drb);
                String infor = ProcessUtil.toString(resourceId,Constants.ADAPTER_UPDATE,drb.getFlowSub().getSort(),flowId);
                return ajaxForwardSuccess("修改成功！", infor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardSuccess("异常！");
            }
        }
    }
    
    /**
     * 查看
     * 
     * @return
     */
    public String view() {
        if (StringUtils.isNotBlank(resourceId)) {
            try {
                bean = deeResourceManager.get(resourceId);
                scriptBean = new ScriptBean(bean.getResource_code());
                bean.setDr(scriptBean);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        String tree = "";
        //数据functionTree
        List<Map<String,String>> systemFuns = null;
        List<Map<String,String>> userFuns = null;
       
        //系统函数
        systemFuns = functionUtil.getAllFuns();
       
        try {
        	//用户函数
        	userFuns =  codeLibMgr.getAllUserPackAndClass();
		} catch (SystemException e) {
			 logger.error(e.getMessage());
		}
        
        JSONArray js = new JSONArray();
        if(systemFuns!=null){
        	js.addAll(systemFuns);
		}
        if(userFuns!=null){
        	js.addAll(userFuns);
        }
        tree = js.toString();
         
        functionTree = functionUtil.string2Json(tree);
        return SCRIPT_VIEW;
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

    public DeeResourceBean getBean() {
        return bean;
    }

    public void setBean(DeeResourceBean bean) {
        this.bean = bean;
    }

    public ScriptBean getScriptBean() {
        return scriptBean;
    }

    public void setScriptBean(ScriptBean scriptBean) {
        this.scriptBean = scriptBean;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getWriterExist() {
        return writerExist;
    }

    public void setWriterExist(String writerExist) {
        this.writerExist = writerExist;
    }
}
