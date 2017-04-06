<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/datasource/dslist.js" type="text/javascript"></script>
<form id="pagerForm" method="post" action="/dee/datasource!dslist.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<div class="pageHeader">
	<form rel="pagerForm" onsubmit="return navTabSearch(this);" action="/dee/datasource!dslist.do" method="post">
	<div class="searchBar">
		<div class="subBar">
			<ul>
			<li>
				<label>数据源名称：</label>
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
			<li><a class="add" target="dialog" rel="addds" href="/dee/datasource!dsshow.do?navTabId=ds" title="新建-数据源" width="830" height="393"><span>新建</span></a></li>
			<li><a class="edit" selectType="selectOneToShwDiaglog" uid="ids" rel="addds" href="/dee/datasource!dsshow.do?uid={slt_uid}"  width="830" height="393" warn="请选择一条记录" title="编辑-数据源"><span>编辑</span></a></li>
			<li><a class="delete" target="selectedTodo" rel="ids" postType="string" href="/dee/datasource!deleteAll.do?navTabId=ds" callback="dslistDelDone" warn="请选择至少一条记录" title="确定要删除这些记录吗?"><span>批量删除</span></a></li>
		</ul>
	</div>
	
	<table class="table" layoutH="110" width="100%">
		<thead>
			<tr>
				<th width="40" style="text-align: center;"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
				<th width="220" orderField="DIS_NAME" class="${param.orderField eq 'DIS_NAME' ? param.orderDirection : ''}">数据源名称</th>
				<th width="220" >数据源类型</th>
				<th width="220" >A8元数据</th>
				<th orderField="CREATE_TIME" class="${param.orderField eq 'CREATE_TIME' ? param.orderDirection : ''}">创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${dsList}">
			<tr target="slt_uid" rel="${item.resource_id}" targetRel="addds" targetType="dialog" url="/dee/datasource!dsshow.do?uid=" targetTitle="编辑-数据源" wth="830" hth="393" style="cursor:pointer;">
				<td style="text-align: center;"><input name="ids" value="${item.resource_id}" type="checkbox"></td>
				<td>${item.dis_name}</td>
                <td>
                  <c:if test="${item.deeResourceTemplate.resource_template_id == 5 || (item.deeResourceTemplate.resource_template_id == 33 && item.dr.jndi == '')}">
                    JDBC
                  </c:if>
                  <c:if test="${item.deeResourceTemplate.resource_template_id == 10 || (item.deeResourceTemplate.resource_template_id == 33 && item.dr.jndi != '')}">
                    JNDI
                  </c:if>
                </td>
				<td>
                  <c:if test="${item.deeResourceTemplate.resource_template_id == 5 || item.deeResourceTemplate.resource_template_id == 10}">
                    否
                  </c:if>
                  <c:if test="${item.deeResourceTemplate.resource_template_id == 33}">
                    是
                  </c:if>
				</td>
				<td>${item.create_time}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<!-- 分页 -->
	<c:import url="../_frag/pager/panelBar.jsp"></c:import>
	
</div>


