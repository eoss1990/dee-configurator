package com.seeyon.dee.configurator.dao;

import com.seeyon.v3x.dee.common.db.codelib.model.CodePkgBean;
import dwz.framework.common.dao.BaseDao;
import dwz.framework.exception.SystemException;

import java.util.List;

/**
 * @author zhangfb
 */
public interface CodePkgDao extends BaseDao<CodePkgBean, String> {
    /**
     * 包名是否存在
     *
     * @param name 包名
     * @return true:是，false:不是
     */
    boolean containsName(String name) throws SystemException;

    List<CodePkgBean> getChildPkg(String parentPkg)throws SystemException;
    
    /**
     * 获取所有包
     * @return
     * @throws SystemException
     */
    List<CodePkgBean> getAllPkg()throws SystemException;

    /**
     * 是否包含子节点
     *
     * @param parentPkg
     * @return
     * @throws SystemException
     */
    public boolean containsChildPkg(String parentPkg) throws SystemException;
}
