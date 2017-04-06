<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/redo/syncList.js" type="text/javascript"></script>
<script type="text/javascript">

</script>
<form id="pagerForm" method="post" action="/dee/redo!syncList.do">
    <input type="hidden" name="pageNum" value="1" />
    <input type="hidden" name="numPerPage" value="${numPerPage}" />
    <input type="hidden" name="orderField" value="${param.orderField}" />
    <input type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>
<form method="post" rel="pagerForm" action="/dee/redo!syncList.do" onsubmit="return navTabSearch(this);">
    <div class="pageHeader">
        <div class="searchBar">
            <div class="subBar">
                <ul>
                    <li>
                        <label>任务名称：</label>
                        <input type="text" name="flowName" value="${flowName}"/>
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
<div class="pageContent" selector="h1">
        <div layoutH="61">
            <div class="panelBar">
                <ul>
                    <li><div style="padding-top:6px; font-weight:bold;">任务触发列表&nbsp;&nbsp;</div></li>
                </ul>
                <ul class="toolBar" style="float:right;margin-top:-2px;">
                    <li><a title="是否确定删除？" target="selectedTodo" rel="sync_id" postType="string" href="/dee/redo!delSync.do" preCallback="deleteTaskPreCallback"  class="delete"><span>删除</span></a></li>
                </ul>
            </div>
            <table class="table" width="100%" layoutH="115" >
                <thead>
                    <tr>
                        <th width="5%" style="text-align: center;"><input type="checkbox" onclick="javascript:checkSyncAll(this);" /></th>
                        <th width="40%" orderField="flow.DIS_NAME" class="${param.orderField eq 'flow.DIS_NAME' ? param.orderDirection : ''}">任务名称</th>
                        <th width="30%" orderField="sync_time" class="${param.orderField eq 'sync_time' ? param.orderDirection : ''}">时间</th>
                        <th width="10%" orderField="sync_state" class="${param.orderField eq 'sync_state' ? param.orderDirection : ''}">状态</th>
                    </tr>
                </thead>
                <tbody class="list1">
                    <c:forEach var="item" items="${syncPage.items}" varStatus="status">
                        <tr 
                        <c:if test="${item.sync_state != 1}">
                        target="slt_sync_id" rel="${item.sync_id}" targetRel="exchangeBaseNav" 
                        targetType="tab"  
                        url="/dee/redo!redoList.do?sync_id="
                        targetTitle="异常信息列表" 
                        </c:if>
                         <c:if test="${item.sync_state == 1}">
                     	ondblclick="alertMsg.info('当前任务不存在异常信息!');"
                          </c:if>
                          style="cursor:pointer;"> 
                            <td align="center"><input type="checkbox" name="sync_id" value="${item.sync_id}" /></td>
                            <td>${item.flow.DIS_NAME}</td>
                            <td>${item.sync_time}</td>
                            <td class="state">
                            <c:if test="${item.sync_state == 0}">失败</c:if>
                            <c:if test="${item.sync_state == 1}">成功</c:if>
                            <c:if test="${item.sync_state == 2}">部分失败</c:if>	
                            </td>
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
    
      <!--   <div>
            <div class="panelBar" style="margin-top:5px;">
                <ul>
                    <li><div style="padding-top:6px; font-weight:bold;">异常信息列表&nbsp;&nbsp;</div></li>
                </ul>
                <ul class="toolBar" style="float:right;">
                    <li><a title="是否确定删除？" target="selectedTodo" rel="redo_id" postType="string" href="/dee/redo!delRedo.do" class="delete"><span>删除</span></a></li>
                    <li><a title="是否确定重发？" target="selectedTodo" callback="refresh_redo" rel="redo_id" postType="string" href="/dee/redo!executeRedo.do" class="icon"><span>重发</span></a></li>
                </ul>
            </div>
            <table class="list" width="100%" style="word-break:break-all;word-wrap:break-word">
                <thead>
                    <tr>
                        <th width="5%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;"><input type="checkbox"  onclick="javascript:checkRedoAll(this);" /></th>
                        <th width="30%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">内容</th>
                        <th width="50%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">错误信息</th>
                        <th width="5%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">重发次数</th>
                        <th width="10%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">数据状态</th>
                    </tr>
                </thead>
                <tbody id="redoList">
                </tbody>
            </table>
        </div> -->
</div>

