package com.seeyon.dee.configurator.dto;

/**
 * 表单回填dto
 *
 * @author zhangfb
 */
public class FormWriteDto {
    private String id;

    private String name;

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
