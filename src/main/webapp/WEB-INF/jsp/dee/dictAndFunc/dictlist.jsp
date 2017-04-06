<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/dictAndFunc/dictlist.js" type="text/javascript"></script>
<form id="pagerForm" method="post" action="/dee/dictAndFunc!dictlist.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageHeader">
	<form rel="pagerForm" onsubmit="return navTabSearch(this);" action="/dee/dictAndFunc!dictlist.do" method="post">
	<div class="searchBar">
		<div class="subBar">
			<ul>
			<li>
				<label>字典名称：</label>
				<input type="text" name="keywords" value=""/>
			</li>
				<li><div class="button"><div class="buttonContent"><button type="submit">查询</button></div></div></li>
			</ul>
		</div>
	</div>
	</form>
</div>

<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="add" target="dialog" rel="adddict" href="/dee/dictAndFunc!dictshow.do?navTabId=dict" title="新建-数据字典" width="920" height="280"><span>新建</span></a></li>
			<li><a class="edit" selectType="selectOneToShwDiaglog" uid="ids" rel="adddict" href="/dee/dictAndFunc!dictshow.do?uid={slt_uid}"  width="920" height="280" warn="请选择一条记录" title="编辑-数据字典"><span>编辑</span></a></li>
			<li><a class="delete" target="selectedTodo" rel="ids" postType="string" href="/dee/dictAndFunc!deleteAll.do?navTabId=dict" callback="dictlistDelDone" warn="请选择至少一条记录" title="确定要删除这些记录吗?"><span>批量删除</span></a></li>
		</ul>
	</div>
	
	<table class="table" layoutH="110" width="100%">
		<thead>
			<tr>
				<th width="40" style="text-align: center;"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
				<th width="220" orderField="DIS_NAME" class="${param.orderField eq 'DIS_NAME' ? param.orderDirection : ''}">数据字典名称</th>
				<th width="220">参数</th>
				<th width="220">数据类型</th>
				<th orderField="CREATE_TIME" class="${param.orderField eq 'CREATE_TIME' ? param.orderDirection : ''}">创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${dictList}">
			<tr target="slt_uid" rel="${item.resource_id}" targetRel="adddict" targetType="dialog" url="/dee/dictAndFunc!dictshow.do?uid=" targetTitle="编辑-数据字典" wth="920" hth="280" style="cursor:pointer;">
				<td style="text-align: center;"><input name="ids" value="${item.resource_id}" type="checkbox"></td>
				<td>${item.dis_name}</td>
				<c:choose>
				<c:when test="${item.deeResourceTemplate.resource_template_name=='SystemFunction'}">
					<td>${item.resource_code}</td>
 				</c:when>
 	 			<c:otherwise>
 					<td></td>
 	 			</c:otherwise>
				</c:choose>
				<c:choose>
				<c:when test="${item.deeResourceTemplate.resource_template_name=='JDBCDictionary'}">
 					<td>JDBC字典</td>
 				</c:when>
				<c:when test="${item.deeResourceTemplate.resource_template_name=='StaticDictionary'}">
 					<td>枚举字典</td>
 				</c:when>
 	 			<c:otherwise>
 					<td>系统函数</td>
 	 			</c:otherwise>
				</c:choose>
				<td>${item.create_time}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<!-- 分页 -->
	<c:import url="../_frag/pager/panelBar.jsp"></c:import>
	
</div>


