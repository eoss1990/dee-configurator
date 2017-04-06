package com.seeyon.dee.configurator.dto;
/**
 * 元数据行项目
 * @author yangyu
 *
 */
public class MetaData {
	
	private String formName;
	private String dbFormName;
	private String formDisName;
	private String formType;
	private String id;
	private String name;
	private String display;
	private String fieldType;
	private String fieldLength;
	private String isNull;
	private String isPrimary;
	private String orderNo;//排序
	
 
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	//关联主表字段
	private String refMainField;
	
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
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}
	public String getIsNull() {
		return isNull;
	}
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}
	public String getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getDbFormName() {
		return dbFormName;
	}
	public void setDbFormName(String dbFormName) {
		this.dbFormName = dbFormName;
	}
	public String getFormDisName() {
		return formDisName;
	}
	public void setFormDisName(String formDisName) {
		this.formDisName = formDisName;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public String getRefMainField() {
		return refMainField;
	}
	public void setRefMainField(String refMainField) {
		this.refMainField = refMainField;
	}

}
