package com.seeyon.dee.configurator.mgr;

import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.exception.SystemException;

/**
 * Dee适配器管理
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午12:12:33
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public interface DeeAdapterMgr {

    /**
     * 新增适配器
     * 
     * @param drb Dee资源bean
     * @param sub
     * @throws SystemException
     */
    public void saveAdapter(DeeResourceBean drb, FlowSubBean sub) throws SystemException;
    
    /**
     * 复制任务后新增适配器
     * 
     * @param drb Dee资源bean
     * @param sub
     * @throws SystemException
     */
    public void copynsaveAdapter(DeeResourceBean drb, FlowSubBean sub) throws SystemException;
    /**
     * 复制任务添加映射配置信息
     *
     * @param drb Dee资源bean
     * @param drb
     * @throws SystemException
     */
    public void saveMappingInfo(DeeResourceBean drb) throws  SystemException;
    /**
     *
     * 修改适配器
     * 
     * @param drb Dee资源bean
     * @throws SystemException
     */
    public void updateAdapter(DeeResourceBean drb) throws SystemException;
    
    /**
     * 根据resourceId获取templateId
     * 
     * @param resourceId
     * @return
     * @throws SystemException
     */
    public String getTemplateIdByResourceId(String resourceId) throws SystemException;

    /**
     * 根据适配器ID取名称
     * @param adapterId
     * @return
     * @throws SystemException
     */
    public String getAdapterNameById(String adapterId)  throws SystemException;
}
