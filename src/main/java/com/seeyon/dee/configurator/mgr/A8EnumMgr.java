package com.seeyon.dee.configurator.mgr;

import com.seeyon.dee.configurator.dto.A8EnumTreeNode;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import dwz.framework.exception.SystemException;

import java.util.List;

/**
 * A8枚举管理器
 *
 * @author zhangfb
 */
public interface A8EnumMgr {

    /**
     * 获取A8枚举树
     *
     * @param dr 数据源
     * @return java.util.List&lt;A8EnumTreeNode&gt;
     */
    public List<A8EnumTreeNode> getA8EnumTreeNodes(DeeResourceBean dr) throws SystemException;

    /**
     * 树列表转换为json数据
     *
     * @param tnList 树列表
     * @return json字符串
     */
    public String treeList2JsonForExport(List<A8EnumTreeNode> tnList);

    /**
     * 树列表转换为json数据
     *
     * @param tnList 树列表
     * @return json字符串
     */
    public String treeList2JsonForImport(List<A8EnumTreeNode> tnList);

    /**
     * 选择所有A8数据源
     *
     * @return A8数据源列表
     * @throws SystemException
     */
    public List<DeeResourceBean> selectAllA8Dbs() throws SystemException;
}
