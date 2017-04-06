<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowManager/deelist.js" type="text/javascript"></script>
<form id="pagerForm" method="post" action="/dee/flowManager!deelist.do?flowTypeId=${flowTypeId}">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />	
 	<input type="hidden" name="keywords" value="${param.keywords}"/>
    <%-- <input type="hidden" name="flowTypeId" value="${flowTypeId}" /> --%>
</form>

<form id="deeListSearchForm" method="post" action="/dee/flowManager!deelist.do" onsubmit="return navTabSearch(this)">
	
	<div class="pageHeader">
		<div class="searchBar">
			<div class="subBar">
				<ul>
					<li>
						<label>任务名称：</label>
						<input type="text" name="keywords" value="${param.keywords}"/>
					</li>
					<div class="buttonActive"><div class="buttonContent"><button type="button" onclick="submitDeeListSearchForm()">查询</button></div></div>
				</ul>
			</div>
		</div>
	</div>
</form>

<div class="pageContent">

	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="add" target="navTab" rel="exchangeBaseNav" href="/dee/flowbase!edit.do?flowTypeId=${flowTypeId}" title="任务新建"><span>新建</span></a></li>
			<li><a class="edit" selectType="selectOneToShwNavTabTwo" uid="ids" rel="exchangeBaseNav" href="/dee/flowbase!edit.do?uid={slt_uid}" title="任务编辑"><span>编辑</span></a></li>
			<li><a title="任务批量复制" target="selectedTodo" rel="ids" postType="string" href="/dee/flowbase!copyAll.do" class="copy"><span>复制</span></a></li>
			<li><a title="任务批量删除" target="selectedTodo" rel="ids" postType="string" href="/dee/flowManager!deleteAll.do" preCallback="deleteTaskPreCallback" class="delete"><span>删除</span></a></li>
			<li class="line">line</li>
			<li><a class="icon" href="#" id="lt_exportResource" title="任务批量导出" onclick="exportResource()"><span>导出</span></a></li>
			<li><a class="icon" href="#" id="test" title="tset" onclick="testForAjax()"><span>test</span></a></li>
		</ul>
	</div>
	
	<table class="table" layoutH="110" width="100%">
		<thead width="100%">
			<tr width="100%">
				<th width="3%" style="text-align: center;"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
				<th width="23.5%" orderField="DIS_NAME" class="${param.orderField eq 'DIS_NAME' ? param.orderDirection : ''}">任务名称</th>
				<th width="24.5%" orderField="flowType.FLOW_TYPE_NAME" class="${param.orderField eq 'flowType.FLOW_TYPE_NAME' ? param.orderDirection : ''}">任务分类</th>
				<th width="24.5%">所属定时器</th>
				<th width="24.5%" orderField="CREATE_TIME" class="${param.orderField eq 'CREATE_TIME' ? param.orderDirection : ''}">创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${flowList}">
			<tr target="slt_uid" rel="${item.FLOW_ID}" targetRel="exchangeBaseNav${item.FLOW_ID}" targetType="tab" url="/dee/flowbase!edit.do?uid=" targetTitle="${item.DIS_NAME}" style="cursor:pointer;">
			<%-- <tr target="slt_uid" rel="${item.FLOW_ID}"> --%>
				<td style="text-align: center;"><input name="ids" value="${item.FLOW_ID}" type="checkbox"></td>
				<td><input name="${item.FLOW_ID}" value="${item.DIS_NAME}" type="hidden" hidden="true">${item.DIS_NAME}</td>
				<td>${item.flowType.FLOW_TYPE_NAME}</td>
				<td>
					<c:forEach var="sche" items="${item.schedules}" varStatus="stat">
						${sche.dis_name}
						<c:if test="${!stat.last}">、</c:if>
					</c:forEach>
				</td>
				<td>${item.CREATE_TIME}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<!-- 分页 -->
	<c:import url="../_frag/pager/panelBar.jsp"></c:import>
	
</div>

<script type="text/javascript">
	function testForAjax() {
		var tttt = "{'d':'dsfds', 't':'ttttsss'}";
		$.ajax({
			async: false,
			type:"post",
			dataType: "json",
			data:{"tttt":tttt},
			url: "/dee/redo!test.do",
			success: function(result) {
				debugger;
				alert("niaho");
			},
			error:function (result) {
				debugger;
				alert("error")
			}
		});
	}
</script>