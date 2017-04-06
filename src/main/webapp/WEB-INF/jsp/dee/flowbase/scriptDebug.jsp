<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ include file="/include.inc.jsp"%>
<div class="pageContent">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
        <table class="table"  width="100%" nowrapTD="false">
            <thead width="100%">
            <tr width="100%">
                <th width="15%"><h2>参数名称</h2></th>
                <th width="15%"><h2>脚本名称</h2></th>
                <th width="15%"><h2>参数行数</h2></th>
                <th width="55%"><h2>参数值</h2></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${scriptList}">
                <tr height="50px">
                    <td>${item.name}</td>
                    <td>${item.adapterName}</td>
                    <td>${item.line}行</td>
                    <td><textarea readonly="readonly" style="width: 100%;height: 100%;background:transparent;border-style:none;">${item.val}</textarea></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="formBar">
        <ul>
            <li>
                <div class="button">
                    <div class="buttonContent">
                        <button  type="button" class="close">取消</button>
                    </div>
                </div>
            </li>
        </ul>
    </div>

</div>
    