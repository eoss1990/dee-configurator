<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/redo/syncList.js" type="text/javascript"></script>
<script src="/dee/WdatePicker.js" type="text/JavaScript"></script>
<script type="text/javascript">

</script>
<form id="pagerForm" method="post" action="/dee/redo!syncListLog.do">
    <input type="hidden" name="pageNum" value="1" />
    <input type="hidden" name="numPerPage" value="${numPerPage}" />
    <input type="hidden" name="orderField" value="${param.orderField}" />
    <input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<form id="postForm" method="post" rel="pagerForm" action="/dee/redo!syncListLog.do" onsubmit="return checkDate(this);" >
    <div class="pageHeader">
        <div class="searchBar">
            <div class="subBar">
                <ul>
                    <li>
                        <label style="width: 60px;">任务名称：</label>
                        <input type="text" name="flowName" value="${flowName}"/>
                    </li>
                    <li>
                        <label style="width: 40px;">状态：</label>
                        <select name="state" value="${state}">
                        	<option value=""></option>
                        	<c:if test="${state == '1'}">
                        		<option value="1" selected="selected">成功</option>
                        		<option value="0">失败</option>
                        	</c:if>
                        	<c:if test="${state == '0'}">
                        		<option value="1">成功</option>
                        		<option value="0" selected="selected">失败</option>
                        	</c:if>
                        	<c:if test="${state == ''}">
                        		<option value="1">成功</option>
                        		<option value="0">失败</option>
                        	</c:if>
						</select>
                    </li>
                    <li>
                        <label style="width: 40px;">时间：</label>
                        <input name="startDate" id="startDate" value="${startDate}" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"/>
                        <input name="endDate" id="endDate" value="${endDate}" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"/>
                    </li>
                    <li>
                        <div class="buttonActive"><div class="buttonContent">
                            <input type="hidden" name="pageNum" value="${pageNum}" />
                            <input type="hidden" name="numPerPage" value="${numPerPage}" />
                            <button type="submit">查询</button>
                         </div></div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</form>
<div class="pageContent" id="syncLog" selector="h1">
        <div layoutH="61" id="taskFrom">
            <div class="panelBar">
                <ul>
                    <li><div style="padding-top:6px; font-weight:bold;">任务触发列表&nbsp;&nbsp;</div></li>
                </ul>
                <ul class="toolBar" style="float:right;margin-top:-2px;">
                    <li><a title="是否确定删除？" 
                    	target="selectedTodo" rel="sync_id" postType="string" 
                    	href="/dee/redo!delSync.do" preCallback="deleteTaskPreCallback"  
                    	class="delete"><span>删除</span></a></li>
                	<li><a title="是否确定重发？（修改适配器顺序后可能会导致任务重发失败）" 
                		rel="redo_id" postType="string" 
                		href="/dee/redo!executeRedo.do" onclick="return checkState();"
                		class="icon"><span>重发</span></a></li>
                </ul>
            </div>
            <table class="table" width="100%" layoutH="115" >
                <thead>
                    <tr>
                        <th width="5%" style="text-align: center;"><input type="checkbox" onclick="javascript:checkSyncAllAndRedo(this);" /></th>
                        <th width="5%" style="text-align: center; display: none;" hidden="true"><input type="checkbox" hidden="true" /></th>
                        <th width="5%" style="text-align: center; display: none;" hidden="true"><input type="checkbox" hidden="true" /></th>
                        <th width="30%" style="text-align: left;" orderField="flow.DIS_NAME" class="${param.orderField eq 'flow_dis_name' ? param.orderDirection : ''}">任务名称</th>
                        <th width="25%" style="text-align: left;" orderField="sync_time" class="${param.orderField eq 'sync_time' ? param.orderDirection : ''}">时间</th>
                        <th width="10%" style="text-align: left;" orderField="sync_state" class="${param.orderField eq 'sync_state' ? param.orderDirection : ''}">状态</th>
                        <th width="10%" style="text-align: left;" orderField="counter" class="${param.orderField eq 'counter' ? param.orderDirection : ''}">重发次数</th>
                        <th width="10%" style="text-align: left;" orderField="exec_time" class="${param.orderField eq 'exec_time' ? param.orderDirection : ''}">执行时间</th>
                    </tr>
                </thead>
                <tbody class="list1">
                    <c:forEach var="item" items="${syncPage.items}" varStatus="status">
                        <tr ondblclick="adapterDetail('${item.flow_id}'+','+'${item.sync_id}')" style="cursor:pointer;"> 
                            <td align="center"><input type="checkbox" name="sync_id" value="${item.sync_id}" onclick="checkSyncRedo(this);" /></td>
                            <c:if test="${item.redo_id != null}">
                            <td align="center" hidden="true" style="display: none;"><input type="checkbox" hidden="true" id="${item.sync_id}" name="redo_id" value="${item.redo_id}" /></td>
                        	</c:if>
                        	<c:if test="${item.redo_id == null}">
                            <td align="center" hidden="true" style="display: none;"><input type="checkbox" hidden="true" id="${item.sync_id}" name="redo_id" value="--1" /></td>
                        	</c:if>
                            <td align="center" hidden="true" style="display: none;"><input type="checkbox" hidden="true" id="${item.sync_id}" name="state" value="${item.sync_state}" /></td>
                            <td>${item.flow_dis_name}</td>
                            <td>${item.sync_time}</td>
                            <td class="state">
                            <c:if test="${item.sync_state == 0}">失败</c:if>
                            <c:if test="${item.sync_state == 1}">成功</c:if>
                            <c:if test="${item.sync_state == 2}">部分失败</c:if>	
                            </td>
                            <td id="${item.redo_id}">${item.counter}</td>
                            <td>${item.exec_time}毫秒</td>
                        </tr>
                       
                    </c:forEach>
                </tbody>
            </table>
        </div>
    <div class="panelBar">
        <div class="pages">
            <span>每页</span>
            <select name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">
                <c:forEach begin="10" end="40" step="10" varStatus="s">
                    <option value="${s.index}" ${numPerPage eq s.index ? 'selected="selected"' : ''}>${s.index}</option>
                </c:forEach>
            </select>
            <span>总条数: ${syncPage.context.totalCount}</span>
        </div>
        <div class="pagination" targetType="navTab" totalCount="${syncPage.context.totalCount}" numPerPage="${syncPage.context.pageSize}" pageNumShown="3" currentPage="${syncPage.index}"></div>
    </div>
</div>
<div id="adapterDetail" style="display: none; height:0px; width: 100%;">
	<table id="detailTable" style="width: 100%; height:0px; border-color: silver; border-collapse: 0;" border="1">
		<tr>
			<td style="vertical-align:top; width: 20%;">
        		<ul id="taskTree" class="ztree"></ul>
			</td>
			<td  style="vertical-align:top; word-wrap:break-word; word-break:break-all;">
				<textarea id="dataText"  readonly="readonly" style="width: 100%; border: 0px; background-color: white;"></textarea>
			</td>
		</tr>
	</table>
</div>

