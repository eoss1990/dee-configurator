package com.seeyon.dee.configurator.mgr;

import java.util.List;

import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBeanLog;

import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.common.pagination.Page;
import dwz.framework.exception.SystemException;

/**
 * 异常管理
 * 
 * @author Zhang.Fubing
 * @date 2013-5-16下午12:23:33
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public interface RedoMgr extends BaseMgr<RedoBean, String> {
    
    /**
     * 分页查询同步历史
     * 
     * @param hql hql语句
     * @param hqlCount hqlCount语句
     * @param pageNum 第几页
     * @param numPerPage 每页记录数
     * @param params 参数
     * @return 分页结果
     * @throws SystemException
     */
    Page<SyncBean> listSync(String hql, String hqlCount, int pageNum, int numPerPage, Object[] params) throws SystemException;
    
    /**
     * 分页查询异常信息
     * 
     * @param hql hql语句
     * @param hqlCount hqlCount语句
     * @param pageNum 第几页
     * @param numPerPage 每页记录数
     * @return 分页结果
     * @throws SystemException
     */
    Page<SyncBeanLog> listSyncLog(String sql, String sqlCount, int pageNum, int numPerPage) throws SystemException;
    
    /**
     * 查询同步历史总记录数
     * 
     * @return
     * @throws SystemException
     */
    public int listSyncCount() throws SystemException;
    
    /**
     * 根据同步ID，查询重发列表
     * 
     * @param syncId 同步ID
     * @return
     * @throws SystemException
     */
    public List<RedoBean> listRedoBySync(String syncId) throws SystemException;
    
    /**
     * 删除重发记录
     * 
     * @param redoId 重发ID
     * @throws SystemException
     */
    public int delRedo(String redoIds) throws SystemException;
    
    /**
     * 删除同步历史
     * 
     * @param syncId 同步ID
     * @throws SystemException
     */
    public int delSync(String syncIds) throws SystemException;
    
    /**
     * 执行重发
     * 
     * @param redoIds 重发ID
     * @return
     * @throws Exception
     */
    public boolean executeRedo(String redoIds) throws Exception;
}
