package com.seeyon.dee.configurator.mgr.impl;

import com.seeyon.dee.configurator.dao.RedoDao;
import com.seeyon.dee.configurator.dao.SyncDao;
import com.seeyon.dee.configurator.mgr.RedoMgr;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.TransformFactory;
import com.seeyon.v3x.dee.common.db.redo.dao.DEERedoDAO;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBeanLog;
import com.seeyon.v3x.dee.common.db.redo.util.SyncState;
import com.seeyon.v3x.dee.config.EngineConfig;
import com.seeyon.v3x.dee.context.AdapterKeyName;
import com.seeyon.v3x.dee.context.Flow;
import com.seeyon.v3x.dee.datasource.XMLDataSource;
import com.seeyon.v3x.dee.util.FileUtil;

import dwz.framework.common.dao.BaseDao;
import dwz.framework.common.mgr.impl.BaseMgrImpl;
import dwz.framework.common.pagination.Page;
import dwz.framework.common.pagination.PageUtil;
import dwz.framework.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("redoMgr")
public class RedoMgrImpl extends BaseMgrImpl<RedoBean, String> implements RedoMgr {

    @Autowired
    @Qualifier("syncDao")
    private SyncDao syncDao;
    
    private RedoDao redoDao;

    private DEERedoDAO reDao =new DEERedoDAO();
    
    @Autowired
    @Qualifier("redoDao")
    @Override
    public void setBaseDao(BaseDao<RedoBean, String> baseDao) {
        super.baseDao = baseDao;
        this.redoDao = (RedoDao)baseDao;
    }
    
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
    @Override
    public Page<SyncBean> listSync(String hql, String hqlCount, int pageNum, int numPerPage, Object[] params) throws SystemException {
        Integer count = syncDao.countByQuery(hqlCount);
        List<SyncBean> items = syncDao.listAll(hql, pageNum, numPerPage, params);
        return PageUtil.getPage(pageNum, numPerPage, items, count);
    }
    
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
    @Override
    public Page<SyncBeanLog> listSyncLog(String sql, String sqlCount, int pageNum, int numPerPage) throws SystemException {
        Integer count = syncDao.querySqlCount(sqlCount);
        List<SyncBeanLog> items = syncDao.listAllLog(sql, pageNum, numPerPage);
        return PageUtil.getPage(pageNum, numPerPage, items, count);
    }

    /**
     * 查询同步历史总记录数
     * 
     * @return
     * @throws SystemException
     */
    @Override
    public int listSyncCount() throws SystemException {
        return syncDao.countAll();
    }
    
    /**
     * 根据同步ID，查询重发列表
     * 
     * @param syncId 同步ID
     * @return
     * @throws SystemException
     */
    @Override
    public List<RedoBean> listRedoBySync(String syncId) throws SystemException {
        String hql = "from RedoBean rb where rb.syncBean.sync_id = ?";
        Object[] params = new Object[1];
        params[0] = syncId;
        return redoDao.listAll(hql, params);
    }

    /**
     * 删除重发记录
     * 
     * @param redoIds 重发ID
     * @throws SystemException
     */
    @Override
    public int delRedo(String redoIds) throws SystemException {
        if (redoIds == null) {
            return 0;
        }
        String ids = StringUtils.trimAllWhitespace(redoIds);
        String[] idArray = ids.split(",");
        
        RedoBean redoBean = redoDao.get(idArray[0]);
        String syncId = null;
        if (redoBean != null && redoBean.getSyncBean() != null) {
            syncId = redoBean.getSyncBean().getSync_id();
            if (syncId == null) {
                return 0;
            }
        }
        // 删除重发记录
        int delNum = redoDao.delRedos(idArray);
        
        Object[] params = new Object[1];
        params[0] = syncId;
        int count = redoDao.countByQuery("select count(*) from RedoBean rb where rb.syncBean.sync_id=?", params);
        if (count < 1) {
            // 如果重发表已删除所有关联记录，删除同步记录
            syncDao.delete(syncId);
        }
        
        return delNum;
    }

    /**
     * 删除同步历史
     * 
     * @param syncIds 同步ID
     * @throws SystemException
     */
    @Override
    public int delSync(String syncIds) throws SystemException {
        AdapterKeyName adapterKeyName = AdapterKeyName.getInstance();
        if (syncIds == null) {
            return 0;
        }
        
        Set<String> redoSet = new HashSet<String>();
        
        // 去掉字符串中的空格
        String ids = StringUtils.trimAllWhitespace(syncIds);
        String[] syncArray = ids.split(",");
        
    	String path = "";
    	String fid = "";
        for (String syncId : syncArray) {
        	SyncBean syncBean = syncDao.get(syncId);
        	fid = syncBean.getFlow().getFLOW_ID();
        	path = adapterKeyName.getDeeHome() + "flowLogs" + File.separator + adapterKeyName.getFlowMap().get(fid) + "_" + fid + File.separator;
            path = path + syncId + ".properties";
            File file = new File(path);
            if (file.exists()){
            	file.delete();
            }
        	
        	Collection<RedoBean> redoBeans = redoDao.listRedoBySyncId(syncId);
            for (RedoBean redoBean : redoBeans) {
                if (redoBean != null) {
                    redoSet.add(redoBean.getRedo_id());
                }
            }
        }
        
        String[] redoArray = redoSet.toArray(new String[0]);
        redoDao.delRedos(redoArray);
        // 删除同步历史记录
        return syncDao.delSyncs(syncArray);
    }

    /**
     * 执行重发
     * 
     * @param redoIds 重发ID
     * @return
     * @throws Exception
     */
    @Override
    public boolean executeRedo(String redoId) throws Exception {
        String syncId = "";
		RedoBean redoBean = redoDao.get(redoId);
		if(redoBean.getSyncBean().getSync_state() != 0){
			return false;
		}
		try {
			redoBean.setFlow_id(redoBean.getSyncBean().getFlow().getFLOW_ID());
			executeRedo(redoBean);
			redoDao.clear();
		} catch (TransformException e) {
			redoDao.clear();
			throw new TransformException(e);
		}
        return true;
    }

    /**
     * 根据bean执行redo
     *
     * @param bean
     * @throws TransformException
     */
    private void executeRedo(RedoBean bean) throws TransformException {
        if (bean == null)
            return;
        DEEClient client = new DEEClient();
        try {
            Flow flow = (Flow) client.lookup(bean.getFlow_id());
            bean.getPara().add("oldSyscId", bean.getSyncBean().getSync_id());
            if (flow != null)
                flow.redo(new XMLDataSource(bean.getDoc_code()).parse(), bean.getWriter_name(), bean.getPara(), EngineConfig.getInstance().parse(), bean.getRedo_id());
            else
                throw new TransformException("转换任务:" + bean.getFlow_id() + "不存在！");
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            throw new TransformException(e);
        }
    }
}
