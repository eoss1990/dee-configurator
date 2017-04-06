package com.seeyon.dee.configurator.dto;

/**
 * @author zhangfb
 */
public class CodePkgDto {
    private String id;

    private String name;

    private String parentId;

    public CodePkgDto() {
    }

    public CodePkgDto(String id, String name, String parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.intern().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof CodePkgDto && id.equals(((CodePkgDto) obj).id);
    }
}
