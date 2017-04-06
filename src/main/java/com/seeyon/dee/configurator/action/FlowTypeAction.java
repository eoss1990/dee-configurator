package com.seeyon.dee.configurator.action;

import java.util.Collection;

import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.seeyon.dee.configurator.dto.FlowTypeDto;
import com.seeyon.dee.configurator.mgr.FlowBaseMgr;
import com.seeyon.dee.configurator.mgr.FlowManagerMgr;
import com.seeyon.dee.configurator.mgr.FlowTypeMgr;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;

import dwz.framework.common.action.BaseAction;
import dwz.framework.exception.SystemException;

public class FlowTypeAction extends BaseAction {
    
    private static final long serialVersionUID = 199938396723761788L;
    
    private static Logger logger = Logger.getLogger(FlowTypeAction.class.getName());
    
    @Autowired
    @Qualifier("flowTypeMgr")
    private FlowTypeMgr flowTypeMgr;

    @Autowired
    @Qualifier("flowBaseMgr")
    private FlowBaseMgr flowBaseMgr;
    
    private FlowTypeDto flowType;
    private FlowTypeBean flowTypeBean;
    private String jsonData;
    private static final String ROOT_PARENT_ID = "-1";
    
    public String addFlowType() {
        return INPUT;
    }
    
    public String getAllFlowType() {
        try {
            Collection<FlowTypeBean> ftList = flowBaseMgr.findAll();
            this.jsonData = JSONSerializer.toJSON(ftList).toString();
            return ajaxForwardSuccess("success");
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            return ajaxForwardError("error");
        }
    }
    
    public String editFlowType() {
        try {
            flowTypeBean = flowTypeMgr.get(flowType.getId());
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }
        return INPUT;
    }
    
    /**
     * 根据ID获取任务类别
     * 
     * @return
     */
    public String getFlowTypeById() {
        try {
            flowTypeBean = flowTypeMgr.get(flowType.getId());
            this.jsonData = JSONSerializer.toJSON(flowTypeBean).toString();
            return ajaxForwardSuccess("成功");
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }
        return ajaxForwardError("失败");
    }
    
    public String saveFlowType() {
        if (flowType == null) {
            return ajaxForwardError("参数错误");
        }
        if (StringUtils.isBlank(flowType.getName())) {
            return ajaxForwardError("名称不能为空");
        }
        if (StringUtils.isNotBlank(flowType.getId())) {
            // 修改
            try {
                FlowTypeBean flowTypeBean = flowTypeMgr.get(flowType.getId());
                
                if (!ROOT_PARENT_ID.equals(flowTypeBean.getPARENT_ID()) &&
                    flowTypeMgr.hasTheSameName(flowTypeBean.getPARENT_ID(), flowType.getName(), flowType.getId())) {
                    return ajaxForwardError("同级节点不允许重名！");
                }
                
                flowTypeBean.setFLOW_TYPE_NAME(flowType.getName());
                flowTypeBean.setFLOW_TYPE_ORDER(flowType.getSort());
                flowTypeMgr.update(flowTypeBean);
                return ajaxForwardSuccess("修改成功");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常");
            }
        } else if (StringUtils.isNotBlank(flowType.getParentId())) {
            try {
                if (flowTypeMgr.hasTheSameName(flowType.getParentId(), flowType.getName(), null)) {
                    return ajaxForwardError("同级节点不允许重名！");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常！");
            }
            
            // 新增
            FlowTypeBean flowTypeBean = new FlowTypeBean();
            flowTypeBean.setFLOW_TYPE_ID(UuidUtil.uuid());
            flowTypeBean.setFLOW_TYPE_NAME(flowType.getName());
            flowTypeBean.setPARENT_ID(flowType.getParentId());
            flowTypeBean.setFLOW_TYPE_ORDER(flowType.getSort());
            try {
                flowTypeMgr.save(flowTypeBean);
                return ajaxForwardSuccess("新增成功", flowTypeBean.getFLOW_TYPE_ID());
            } catch (SystemException e) {
                logger.error(e.getMessage(), e);
                return ajaxForwardError("异常");
            }
        } 
        return ajaxForwardError("error");
    }
    
    public String deleteFlowType() {
        if (flowType == null || StringUtils.isBlank(flowType.getId())) {
            return ajaxForwardError("参数错误");
        }
        
        try {
            FlowTypeBean flowTypeBean = flowTypeMgr.get(flowType.getId());
            if (flowTypeBean == null) {
                return ajaxForwardError("该分类不存在");
            }
            if (flowTypeMgr.hasChildFlowType(flowType.getId())) {
                return ajaxForwardError("请先删除子类，再删除该分类");
            }
            if (flowTypeMgr.hasFlow(flowType.getId())) {
                return ajaxForwardError("请先删除包含的任务，再删除该分类");
            }
            flowTypeMgr.delete(flowType.getId());
            return ajaxForwardSuccess("删除成功");
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            return ajaxForwardError("异常");
        }
    }
    
    public FlowTypeDto getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowTypeDto flowType) {
        this.flowType = flowType;
    }

    public FlowTypeBean getFlowTypeBean() {
        return flowTypeBean;
    }

    public void setFlowTypeBean(FlowTypeBean flowTypeBean) {
        this.flowTypeBean = flowTypeBean;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}
