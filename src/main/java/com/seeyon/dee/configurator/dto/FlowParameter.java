package com.seeyon.dee.configurator.dto;

/**
 * 使用KeyValue代替
 * 
 * @author Zhang.Fubing
 * @date 2013-5-21上午10:09:43
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */

public class FlowParameter {

	private String paraName;
	private String disName;
	private String defaultValue;
	
	public String getParaName() {
		return paraName;
	}
	public void setParaName(String paraName) {
		this.paraName = paraName;
	}
	public String getDisName() {
		return disName;
	}
	public void setDisName(String disName) {
		this.disName = disName;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
}
