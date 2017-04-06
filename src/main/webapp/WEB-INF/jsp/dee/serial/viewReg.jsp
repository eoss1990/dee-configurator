<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<script src="/styles/management/js/index/editSerial.js" type="text/javascript"></script>
<input type="hidden" id="inputRegFlag" value="${regFlag}"/>
<div class="pageContent">
    <form id="serialform" method="post" action="/dee/serial!getKeycode.do" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone)">
        <div class="pageFormContent" layoutH="58">
            <div class="unit">
                <label>授权日期：</label>
                <c:if test="${regFlag=='true'}">
                    <label>${AuthorizeDate}</label>
                </c:if>
                <c:if test="${regFlag!='true'}">
                    <input type="text" name="AuthorizeDate" id="AuthorizeDate" value="${AuthorizeDate}" readonly="true" class="date" minDate="%y-%M-{%d}"/>
                </c:if>
            </div>
            <div class="unit">
                <label>用户类型：</label>
                <c:if test="${regFlag=='true'}">
                <label>
                    <c:if test="${Deadline==3}">试用用户</c:if>
                    <c:if test="${Deadline==120}">正式用户</c:if>
                </label>
                </c:if>
                <c:if test="${regFlag!='true'}">
                    <select  name="Deadline" id="Deadline">
                        <option value="3" <c:if test="${Deadline==3}">selected</c:if>>试用用户</option>
                        <option value="120" <c:if test="${Deadline==120}">selected</c:if>>正式用户</option>
                    </select>&nbsp;<input type="button" value="获取识别码" id="getKeycode"/>
                </c:if>
            </div>
            <div class="unit">
                <label>授权码：</label>
                <c:if test="${regFlag=='true'}">
                    <label>${keyCode}</label>
                </c:if>
                <c:if test="${regFlag!='true'}">
                    <input type="text" name="SerialNumber" id="SerialNumber" size="40" value="${SerialNumber}"/>
                </c:if>
            </div>
            <c:if test="${regFlag!='true'}">
            <div class="unit">
                <label>错误信息：</label>
                <p style="color:#f00;" id="errorMsgShow">${errorMsg}</p>
            </div>
            </c:if>
        </div>
        <div class="formBar">
            <ul>
                <c:if test="${regFlag=='true'}">
                    <li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
                </c:if>
                <c:if test="${regFlag!='true'}">
                    <li><div class="buttonActive"><div class="buttonContent"><button type="button" id="confirmSerial">确认</button></div></div></li>
                </c:if>
            </ul>
        </div>
    </form>
</div>