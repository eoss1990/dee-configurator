package com.seeyon.dee.configurator.dao;

import com.seeyon.v3x.dee.common.db.codelib.model.CodeLibBean;
import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

import java.util.List;

/**
 * @author zhangfb
 */
public interface CodeLibDao extends BaseDao<CodeLibBean, String> {
    /**
     * 包名是否存在
     *
     * @param pkgName 包名
     * @return true：存在，false：不存在
     * @throws SystemException
     */
    boolean existPkg(String pkgName) throws SystemException;

    /**
     * 包下面的类名是否存在
     *
     * @param pkgName   包名
     * @param className 类名
     * @param id        排除自身
     * @return true：存在，false：不存在
     * @throws SystemException
     */
    boolean existClass(String pkgName, String className, String id) throws SystemException;

    /**
     * 查找以指定名称开头的所有记录
     *
     * @param likePkgName 指定包名
     * @return
     * @throws SystemException
     */
    List<CodeLibBean> listByPkgName(String likePkgName) throws SystemException;
}
