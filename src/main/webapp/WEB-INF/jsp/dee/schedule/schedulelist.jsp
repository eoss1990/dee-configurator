<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/dee/js/schedule/schedulelist.js"></script>
<form id="pagerForm" method="post" action="/dee/schedule!scheduleList.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />	
 	<input type="hidden" name="keywords" value="${param.keywords}"/>
</form>

<form id="scheduleSearchForm" method="post" action="/dee/schedule!scheduleList.do" onsubmit="return navTabSearch(this)">
	
	<div class="pageHeader">
		<div class="searchBar">
			<div class="subBar">
				<ul>
					<li>
						<label> 定时器名称：</label>
						<input type="text" name="keywords" value="${param.keywords}"/>
					</li>						
					<div class="buttonActive"><div class="buttonContent"><button type="button" onclick="submitScheduleSearchForm()">查询</button></div></div></li>
				</ul>
			</div>
		</div>
	</div>
</form>

<div class="pageContent">

	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="add" target="dialog" rel="scheduleDetail" href="/dee/schedule!detail.do" title="定时器新建" width="600" height="300"><span>新建</span></a></li>
			<li><a class="edit" selectType="selectOneToShwDiaglog" uid="ids" rel="scheduleDetail" title="定时器编辑" href="/dee/schedule!detail.do?uid={slt_uid}" width="600" height="300"><span>编辑</span></a></li>
			<li><a title="定时器批量删除" target="selectedTodo" rel="ids" postType="string" href="/dee/schedule!deleteAll.do" class="delete"><span>删除</span></a></li>
		</ul>
	</div>
	
	<table class="table" layoutH="110" width="100%">
		<thead>
			<tr>
				<th width="40" style="text-align: center;"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
				<th orderField="dis_name" class="${param.orderField eq 'dis_name' ? param.orderDirection : ''}">定时器名称</th>
				<th orderField="flow.DIS_NAME" class="${param.orderField eq 'flow.DIS_NAME' ? param.orderDirection : ''}">任务名称</th>
				<th>是否启用</th>
				<th orderField="create_time" class="${param.orderField eq 'create_time' ? param.orderDirection : ''}">创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${scheduleList}">
			<tr target="slt_uid" rel="${item.schedule_id}" targetRel="scheduleDetail" targetType="dialog" url="/dee/schedule!detail.do?uid=" targetTitle="定时器配置" wth="600" hth="300" style="cursor:pointer;">
			<%-- <tr target="slt_uid" rel="${item.schedule_id}"> --%>
				<td style="text-align: center;"><input name="ids" value="${item.schedule_id}" type="checkbox"></td>
				<td>${item.dis_name}</td>
				<td>${item.flow.DIS_NAME}</td>
				<td>${item.enable==true?'启用':'停用'}</td>
				<td>${item.create_time}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<!-- 分页 -->
	<c:import url="../_frag/pager/panelBar.jsp"></c:import>
	
</div>
