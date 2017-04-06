<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>

<form id="pagerForm" method="post" action="/dee/codelib!listClass.do">
    <input type="hidden" name="pageNum" value="1" />
    <input type="hidden" name="numPerPage" value="${numPerPage}" />
    <input type="hidden" name="orderField" value="${param.orderField}" />
    <input type="hidden" name="orderDirection" value="${param.orderDirection}" />
    <input type="hidden" name="pkgName" id="codelib_packagename" value="${pkgName}" />
</form>

<form method="post" rel="pagerForm" action="/dee/codelib!listClass.do" onsubmit="return divSearch(this, 'codelib_content');">
    <div class="pageHeader">
        <div class="searchBar">
            <div class="subBar">
                <ul>
                    <li>
                        <label>类名称：</label>
                        <input type="text" name="className" value="${className}"/>
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

<div class="pageContent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="add" target="dialog" width="800" height="500" rel="addds1" maxable="false" href="/dee/codelib!edit.do" title="新建类" resizable="false"><span>新建</span></a></li>
            <li><a class="edit" selectType="selectOneToShwDiaglog" width="800" height="500" rel="addds1" maxable="false" uid="ids" href="/dee/codelib!viewClass.do?uid={slt_uid}"  warn="请选择一条记录" title="编辑类" resizable="false"><span>编辑</span></a></li>
            <li><a class="delete" target="selectedTodo" rel="ids" postType="string" href="/dee/codelib!deleteClass.do" title="批量删除类" callback="deleteCodeLibAjaxDone"><span>删除</span></a></li>
        </ul>
    </div>

    <table class="table" style="height: 600px;" layoutH="110" width="100%">
        <thead width="100%">
        <tr width="100%">
            <th width="3%" style="text-align: center;"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
            <th width="20%">类名称</th>
            <th width="30%">所属包</th>
            <th width="30%">类简介</th>
            <th width="15%">创建时间</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${codeLibPage.items}">
            <tr target="slt_uid" rel="${item.id}" targetRel="addds1" targetType="dialog" url="/dee/codelib!viewClass.do?uid=" targetTitle="${item.className}" wth="800" hth="500" style="cursor:pointer;" resizable="false" maxable="false">
                <td style="text-align: center;">
                    <input name="ids" value="${item.id}" type="checkbox">
                </td>
                <td>${item.className}</td>
                <td>${item.pkgName}</td>
                <td>${item.simpleDesc}</td>
                <td>${item.createTime}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<!-- 分页 -->
<div class="panelBar">
    <div class="pages">
        <span>每页</span>
        <select name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value}, 'codelib_content')">
            <c:forEach begin="10" end="40" step="10" varStatus="s">
                <option value="${s.index}" ${numPerPage eq s.index ? 'selected="selected"' : ''}>${s.index}</option>
            </c:forEach>
        </select>
        <span>总条数: ${codeLibPage.context.totalCount}</span>
    </div>
    <div class="pagination" targetType="navTab" totalCount="${codeLibPage.context.totalCount}" numPerPage="${codeLibPage.context.pageSize}" pageNumShown="3" currentPage="${codeLibPage.index}" rel="codelib_content"></div>
</div>

<script>
    function deleteCodeLibAjaxDone(json) {
        json.rel = "codelib_content";
        navTabAjaxDone(json);
    }
</script>