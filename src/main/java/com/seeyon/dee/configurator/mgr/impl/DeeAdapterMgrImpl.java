package com.seeyon.dee.configurator.mgr.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.SimpleDateFormat;
import com.seeyon.dee.configurator.dao.DeeResourceDao;
import com.seeyon.dee.configurator.dao.FlowDao;
import com.seeyon.dee.configurator.dao.FlowSubDao;
import com.seeyon.dee.configurator.mgr.DeeAdapterMgr;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowSubBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import dwz.framework.exception.SystemException;

@Service("deeAdapterMgr")
public class DeeAdapterMgrImpl implements DeeAdapterMgr {
    
    @Autowired
    @Qualifier("flowSubDao")
    FlowSubDao flowSubDao;
    
    @Autowired
    @Qualifier("flowDao")
    FlowDao flowDao;
    
    @Autowired
    @Qualifier("deeResourceDao")
    DeeResourceDao deeResourceDao;
    
    /**
     * 新增适配器
     * 
     * @param drb Dee资源bean
     * @param sub
     * @throws SystemException
     */
    @Override
    public void saveAdapter(DeeResourceBean drb, FlowSubBean sub) throws SystemException {
        if (drb.getResource_id() == null || 
            "".equals(drb.getResource_id().trim())) {
            drb.setResource_id(UuidUtil.uuid());
            if(drb.getResource_name() == null || "".equals(drb.getResource_name().trim())) {
                drb.setResource_name(drb.getResource_id());
            }
        }
        drb.setResource_code(drb.getDr().toXML(drb.getResource_id()));
        
        String flowId = sub.getFlow().getFLOW_ID();
        FlowBean fb = flowDao.get(flowId);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        fb.setEXT4(str);
        flowDao.update(fb);
        deeResourceDao.save(drb);
        flowSubDao.save(sub);
    }
    /**
     * 复制任务后添加适配器
     * 
     * @param drb Dee资源bean
     * @param sub
     * @throws SystemException
     */
    public void copynsaveAdapter(DeeResourceBean drb, FlowSubBean sub) throws SystemException {
        deeResourceDao.save(drb);
        flowSubDao.save(sub);
    }

    /**
     * 复制任务添加映射配置信息
     *
     * @param drb Dee资源bean
     * @param drb
     * @throws SystemException
     */
    public void saveMappingInfo(DeeResourceBean drb) throws  SystemException{
        deeResourceDao.save(drb);
    }

    /**
     * 修改适配器
     * 
     * @param drb Dee资源bean
     * @throws SystemException
     */
    @Override
    public void updateAdapter(DeeResourceBean drb) throws SystemException {
        drb.setResource_code(drb.getDr().toXML(drb.getResource_id()));
        String flowId = flowSubDao.getFlowIdBySourceId(drb.getResource_id());
        FlowBean fb = flowDao.get(flowId);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        fb.setEXT4(str);
        flowDao.update(fb);
        deeResourceDao.update(drb);
    }

    /**
     * 根据resourceId获取templateId
     * 
     * @param resourceId
     * @return
     * @throws SystemException
     */
    @Override
    public String getTemplateIdByResourceId(String resourceId) throws SystemException {
        return deeResourceDao.getTemplateIdByResourceId(resourceId);
    }

    @Override
    public String getAdapterNameById(String adapterId) throws SystemException {
        DeeResourceBean drb = deeResourceDao.get(adapterId);
        return drb.getDis_name();
    }
}
