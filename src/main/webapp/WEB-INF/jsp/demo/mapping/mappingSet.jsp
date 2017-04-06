<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/demo/js/mapping/mappingSet.js" type="text/javascript"></script>
<form id="pagerForm" method="post" action="/demo/mapping!mappingSet.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageContent">
<div class="pageFormContent nowrap">
	<form rel="pagerForm" onsubmit="return validateCallback(this, setBringMapping);" method="post">
		<table width="100%" height="100%">
			<tr>
				<td colspan="2">
<div id="selectDB">
			<select class="combox" name="selDB">
			<c:forEach var="item" items="${drList}">
				<option value="${item.resourceId}">${item.disName}</option>
			</c:forEach>
			</select>
</div>
					</td>
				<td rowspan="3" width="45%">
		 <div style="border:1px solid #B8D0D6;overflow:auto;">
			<table width="380" height="380">
			<tr><td width="190" valign="top" align="center">源</td><td width="190" valign="top" align="center">目标</td></tr>
			<tr>
			<td valign="top">
<div id="dSource" class="sortDrag">
</div>
			</td>
			<td valign="top">
<div id="dTarget" class="sortDrag">
</div>
			</td>
			</tr>
			</table>
		</div>   
					</td>
			</tr>
			<tr>
				<td width="45%">
	<div style="float:left; display:block; overflow:auto; width:300px; height:178px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
					</div></td>
				<td width="10%"><a class='button' id="btnSource"><span>源</span></a></td>
			</tr>
			<tr>
				<td>
					<div style="float:left; display:block; overflow:auto; width:300px; height:178px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
						<select id="mySelect" multiple="multiple" style="float:left; display:block; overflow:auto; width:300px; height:178px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
							<option value="Option 1">Option 1</option>
							<option value="Option 2">Option 2</option>
							<option value="Option 3">Option 3</option>
							<option value="Option 4">Option 4</option>
							<option value="Option 5">Option 5</option>
							<option value="Option 6">Option 6</option>
						</select>
					</div>
					</td>
				<td width="10%"><a class='button' id="btnTarget"><span>目标</span></a></td>
			</tr>
		</table>
	</form>
</div>
<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button id="btnMappingSet" type="button" class="close">确定</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
			</ul>
</div>
</div>
