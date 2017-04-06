<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/dee/js/schedule/flowbringback.js"></script>
<form id="pagerForm" method="post" action="/dee/schedule!deeList.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />	
 	<input type="hidden" name="keywords" value="${param.keywords}"/>
</form>

<div class="pageHeader">
	<form id="deeListSearchDlg" rel="pagerForm" method="post" action="/dee/schedule!deeList.do" onsubmit="return dwzSearch(this, 'dialog');">
		<div class="searchBar">
			<div class="subBar">
				<ul>
					<li>
						<label> 任务名称：</label>
						<input type="text" name="keywords" value="${param.keywords}"/>
					</li>						
					<div class="buttonActive"><div class="buttonContent"><button type="button" onclick="submitDeeListSearchDlg()">查询</button></div></div></li>
				</ul>
			</div>
		</div>
	</form>
</div>


<div class="pageContent">
	<table class="table" layoutH="90" width="100%">
		<thead>
			<tr>
				<th width="200" orderField="DIS_NAME" class="${param.orderField eq 'DIS_NAME' ? param.orderDirection : ''}">任务名称</th>
				<th width="200" orderField="flowType.FLOW_TYPE_NAME" class="${param.orderField eq 'flowType.FLOW_TYPE_NAME' ? param.orderDirection : ''}">任务分类</th>
				<th width="200">所属定时器</th>
				<th width="100" orderField="CREATE_TIME" class="${param.orderField eq 'CREATE_TIME' ? param.orderDirection : ''}">创建时间</th>
				<th width="80">查找带回</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${flowList}">
			<tr target="slt_uid" rel="${item.FLOW_ID}" disName="${item.DIS_NAME}">
				<td>${item.DIS_NAME}</td>
				<td>${item.flowType.FLOW_TYPE_NAME}</td>
				<td>
					<c:forEach var="sche" items="${item.schedules}" varStatus="stat">
						${sche.dis_name}
						<c:if test="${!stat.last}">、</c:if>
					</c:forEach>
				</td>
				<td>${item.CREATE_TIME}</td>
				<td>
					<a id="bringId" class="btnSelect" title="查找带回">选择</a>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<div class="panelBar">
		<div class="pages">
			<span>每页</span>
			<select name="numPerPage" onchange="dialogPageBreak({numPerPage:this.value})">
				<c:forEach begin="10" end="40" step="10" varStatus="s">
					<option value="${s.index}" ${numPerPage eq s.index ? 'selected="selected"' : ''}>${s.index}</option>
				</c:forEach>
			</select>
			<span>总条数: ${totalCount}</span>
		</div>
		<div class="pagination" targetType="dialog" totalCount="${totalCount}" numPerPage="${numPerPage}" pageNumShown="10" currentPage="${param.pageNum}"></div>
	</div>
	
</div>