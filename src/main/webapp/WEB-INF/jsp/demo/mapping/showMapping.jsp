<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/demo/js/mapping/mapping.js" type="text/javascript"></script>
<form id="pagerForm" method="post" action="/demo/mapping!showMapping.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageContent">
<div class="pageFormContent nowrap">
	<form rel="pagerForm" onsubmit="return navTabSearch(this);" action="/demo/mapping!showMapping.do" method="post">
			<p>
				<label>类 别：</label>
			<select id="selType" class="combox">
				<option value="1">字段映射</option>
				<option value="2">XSLT转换</option>
				<option value="3">XML校验</option>
				<option value="4">脚本</option>
				<option value="5">自定义配置</option>
			</select>
			</p>
			<p>
			</p>
			<p>
				<label>名 称：</label>
				<input type="text" name="inputName" class="required" alt="请输入名称"/>
			</p>
	</form>
</div>
<div id="selectDict" style="display:none;">
			<select name="selType">
				<option value="1">系统函数</option>
				<option value="101">A</option>
				<option value="102">B</option>
				<option value="2">字典</option>
				<option value="201">A</option>
			</select>
</div>
	<div class="tabs" currentIndex="0" eventType="click">
		<div class="tabsContent">
			<div layoutH="180">
	<div class="panelBar">
		<ul class="toolBar">
			<li><a id="addBtn" class="add"><span>新增</span></a></li>
			<li><a id="moreBtn" class="add"><span>批量配置</span></a></li>
			<li><a id="loadBtn" class="add"><span>载入来源字段</span></a></li>
			<li><label><input type="checkbox" name="c1" value="1" />是否使用到A8表单</label></li>
		</ul>
	</div>
		 <div style="border:1px solid #B8D0D6;overflow:auto;">
			<table width="100%">
			<tr><td align="center">源</td><td align="center">目标</td></tr>
			<tr>
			<td valign="top">
<div id="dLeft" class="sortDrag">
</div>
			</td>
			<td valign="top">
<div id="dRight" class="sortDrag">
</div>
			</td>
			</tr>
			</table>
		</div>   
           </div>
<div layoutH="180">
	<fieldset>
		<legend>映射脚本</legend>
		<dl class="nowrap">
			<dd><textarea name="textarea1" cols="80" rows="10"></textarea></dd>
		</dl>
	</fieldset>
			</div>
		</div>
		<div class="tabsHeader">
			<div class="tabsHeaderContent">
				<ul>
					<li><a href="javascript:;"><span>字段映射</span></a></li>
					<li><a href="javascript:;"><span>映射脚本</span></a></li>
				</ul>
			</div>
		</div>
	</div>
<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
			</ul>
</div>
</div>
