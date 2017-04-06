package com.seeyon.dee.configurator.mgr;

import com.seeyon.dee.configurator.dto.CodeLibDto;
import com.seeyon.dee.configurator.dto.CodePkgDto;
import com.seeyon.v3x.dee.common.db.codelib.model.CodeLibBean;

import dwz.framework.common.pagination.Page;
import dwz.framework.exception.SystemException;

import java.util.List;
import java.util.Map;

/**
 * @author zhangfb
 */
public interface CodeLibMgr {
    /**
     * 查询包名
     *
     * @return 包名列表
     * @throws SystemException
     */
    List<CodePkgDto> listPkg() throws SystemException;

    /**
     * 新增或修改包（全路径）
     *
     * @param oldPkgName 旧包
     * @param newPkgName 新包
     * @throws SystemException
     */
    void savePkg(String oldPkgName, String newPkgName) throws SystemException;

    /**
     * 删除包
     *
     * @param pkgName 包名
     * @throws SystemException
     */
    void deletePkg(String pkgName) throws SystemException;

    /**
     * 查询类
     *
     * @param hql        查询HQL
     * @param hqlCount   查询总条数HQL
     * @param pageNum    第几页
     * @param numPerPage 每页记录数
     * @param params     查询参数
     * @return 类的页对象
     * @throws SystemException
     */
    Page<CodeLibBean> listClass(String hql, String hqlCount, int pageNum, int numPerPage, Object[] params) throws SystemException;

    /**
     * 新增或修改类
     *
     * @param dto 类dto对象
     * @throws SystemException
     */
    void saveClass(CodeLibDto dto) throws SystemException;

    /**
     * 按ID查询类
     *
     * @param id
     * @return
     * @throws SystemException
     */
    CodeLibBean getClassBydId(String id) throws SystemException;

    /**
     * 按ID删除类
     *
     * @param ids
     * @return
     * @throws SystemException
     */
    int deleteClass(String[] ids) throws SystemException;
    
    /**
     * 按 父id 获取用户自定义函数节点下的包和类
     * @param pid
     * @return
     */
    List<Map<String,String>> getUserPackAndClass(String pid) throws SystemException ;
    
    /**
     * 获取所有用户函数
     * @return
     * @throws SystemException
     */
    List<Map<String,String>> getAllUserPackAndClass() throws SystemException ;
}
