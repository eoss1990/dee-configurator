package com.seeyon.dee.configurator.mgr;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import dwz.framework.exception.SystemException;

import java.util.List;

/**
 * 表单回填管理器
 *
 * @author zhangfb
 */
public interface A8FormWriteBackWriterMgr {
    /**
     * 选择所有A8数据源
     *
     * @return A8数据源列表
     * @throws dwz.framework.exception.SystemException
     */
    public List<DeeResourceBean> selectAllA8Dbs() throws SystemException;
}
