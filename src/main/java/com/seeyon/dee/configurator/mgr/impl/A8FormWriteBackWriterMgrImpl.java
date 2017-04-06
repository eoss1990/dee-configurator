package com.seeyon.dee.configurator.mgr.impl;

import com.seeyon.v3x.dee.bean.ConvertDeeResourceBean;
import com.seeyon.dee.configurator.mgr.A8FormWriteBackWriterMgr;
import com.seeyon.dee.configurator.mgr.DSTreeManager;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import dwz.framework.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 表单回填管理器
 *
 * @author zhangfb
 */
@Service("a8FormWriteBackWriterMgr")
public class A8FormWriteBackWriterMgrImpl implements A8FormWriteBackWriterMgr {
    @Autowired
    @Qualifier("dsTreeManager")
    private DSTreeManager dsTreeManager;

    @Override
    public List<DeeResourceBean> selectAllA8Dbs() throws SystemException {
        try {
            List<DeeResourceBean> dbList = dsTreeManager.getA8MetaAllDbs();
            for (DeeResourceBean drb : dbList) {
                drb.setDr(new ConvertDeeResourceBean(drb).getResource());
            }
            return dbList;
        } catch (SystemException e) {
            throw e;
        }
    }
}
