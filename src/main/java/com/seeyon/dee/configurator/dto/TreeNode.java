package com.seeyon.dee.configurator.dto;

public class TreeNode {
	private String nodeId;
	private String nodePId;
	private String nodeName;
	private String nodeUrl;
	private String formId;
	private String nodeType;
	private String hasDynChild;//动态节点
	private String nodeNoCheck;
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodePId() {
		return nodePId;
	}
	public void setNodePId(String nodePId) {
		this.nodePId = nodePId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeUrl() {
		return nodeUrl;
	}
	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getHasDynChild() {
		return hasDynChild;
	}
	public void setHasDynChild(String hasDynChild) {
		this.hasDynChild = hasDynChild;
	}
	public String getNodeNoCheck() {
		return nodeNoCheck;
	}
	public void setNodeNoCheck(String nodeNoCheck) {
		this.nodeNoCheck = nodeNoCheck;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
}
