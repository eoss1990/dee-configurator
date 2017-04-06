package com.seeyon.dee.configurator.dto;

/**
 * A8枚举树节点
 *
 * @author zhangfb
 */
public class A8EnumTreeNode {
    private String id;

    private String parentId;

    private String name;

    private String enumType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnumType() {
        return enumType;
    }

    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }
}
