<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<SCRIPT type="text/javascript">
		<!--
	var zNodes =[
 	<c:forEach var="item" items="${ftList}" varStatus="vs">
 	<c:choose>
 		<c:when test="${item.flowTypeId=='0'}">
 			{ id:"${item.flowTypeId}", pId:"${item.parentId}", name:"${item.flowTypeName}", open:true}
 		</c:when>
 	 	<c:otherwise>
 			{ id:"${item.flowTypeId}", pId:"${item.parentId}", name:"${item.flowTypeName}"}
 		</c:otherwise>
 	</c:choose>
	<c:if test="${fn:length(ftList) != (vs.index+1)}">,</c:if>
	</c:forEach>
	];
	var zNodes1 =[
		           	<c:forEach var="dr" items="${drList}" varStatus="vs">
	     			{ id:"${dr.resourceId}",pId:0, name:"${dr.disName}",isParent:true}
		        	<c:if test="${fn:length(drList) != (vs.index+1)}">,</c:if>
		        	</c:forEach>
	        	];
	
//-->
</SCRIPT>
<script src="/styles/demo/js/ztree/async.js" type="text/javascript"></script>
<h2 class="contentTitle">树形菜单</h2>
<div class="pageContent">
<div class="pageFormContent nowrap">
	<div style="float:left; display:block; margin:10px; overflow:auto; width:300px; height:300px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
		<ul id="flowTypes" class="ztree"></ul>
	</div>
	<div style="float:left; display:block; margin:10px; overflow:auto; width:300px; height:300px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
		<ul id="asyncDemo" class="ztree"></ul>
	</div>
	<div style="float:left; display:block; margin:10px; overflow:auto; width:300px; height:300px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
					<ul class="tree treeFolder expand">
	<li><a href="#" tname="name" tvalue="A01">A01</a>
		<ul>
			<li><a tname="name" tvalue="A01B01">A01B01</a></li>
			<li><a href="#" tname="name" tvalue="A01B02">A01B02</a></li>
			<li><a href="#" tname="name" tvalue="A01B03">A01B03</a></li>
			<li><a href="#" tname="name" tvalue="A01B04">A01B04</a></li>
		</ul>
	</li>
</ul>
	</div>
</div>
<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
			</ul>
</div>
</div>

