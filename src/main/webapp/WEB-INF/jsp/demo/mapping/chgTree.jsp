<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<SCRIPT type="text/javascript">
		<!--
	var zNodes = ${jData};
//-->
</SCRIPT>
<script src="/styles/demo/js/mapping/chgTree.js" type="text/javascript"></script>
<h2 class="contentTitle">树形菜单</h2>
<div class="pageContent">
<div class="pageFormContent nowrap">
	<div style="float:left; display:block; margin:10px; overflow:auto; width:300px; height:300px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
		<ul id="asyncDemo" class="ztree"></ul>
	</div>
</div>
<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button id="sglBtn" type="button" class="close">确定</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
			</ul>
</div>
</div>

