package com.seeyon.dee.configurator.mgr;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import dwz.framework.common.mgr.BaseMgr;
import dwz.framework.common.pagination.Page;
import dwz.framework.exception.SystemException;

import java.util.List;

/**
 * Rest管理器
 */
public interface RestMgr {
    /**
     * 查询所有单位信息
     *
     * @param address       A8地址
     * @param adminUserName 用户名
     * @param adminPassword 用户密码
     * @param currentId     当前ID
     * @return
     * @throws TransformException
     */
    String deeSelectOrgAccountId(String address,
                                 String adminUserName, String adminPassword,
                                 String currentId) throws TransformException;

    /**
     * 查询所有部门信息
     *
     * @param address       A8地址
     * @param adminUserName 用户名
     * @param adminPassword 用户密码
     * @param currentId     当前ID
     * @return
     * @throws TransformException
     */
    String deeSelectDepartmentId(String address,
                                 String adminUserName, String adminPassword,
                                 String currentId) throws TransformException;

    /**
     * 查询所有部门信息
     *
     * @param address       A8地址
     * @param adminUserName 用户名
     * @param adminPassword 用户密码
     * @param currentId     当前ID
     * @return
     * @throws TransformException
     */
    String deeSelectDepartmentIdNotOrgAccount(String address,
                                 String adminUserName, String adminPassword,
                                 String currentId) throws TransformException;

    /**
     * 查询所有职务级别信息
     *
     * @param address       A8地址
     * @param adminUserName 用户名
     * @param adminPassword 用户密码
     * @param currentId     当前ID
     * @return
     * @throws TransformException
     */
    String deeSelectOrgLevelId(String address,
                               String adminUserName, String adminPassword,
                               String currentId) throws TransformException;

    /**
     * 查询所有岗位信息
     *
     * @param address       A8地址
     * @param adminUserName 用户名
     * @param adminPassword 用户密码
     * @param currentId     当前ID
     * @return
     * @throws TransformException
     */
    String deeSelectPostId(String address,
                           String adminUserName, String adminPassword,
                           String currentId) throws TransformException;
}
