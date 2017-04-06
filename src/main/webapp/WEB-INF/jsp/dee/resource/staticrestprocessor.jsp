<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/srprocessor.js" type="text/javascript"></script>

<div class="pageContent">
  <form method="post" action="/dee/staticrestprocesser!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="srProcessorForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <p></p>
      <p>
        <label>名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" class="required uInput"/>
      </p>
      <p>
        <label>是否为A8内部接口：</label>
      	<input id="isA8" name="isA8" value="${srBean.isA8=='1'?'1':'0'}" type="checkbox" ${srBean.isA8=='1'?'checked':''} 
      		onclick="changeValue()">
      </p>
      <p>
        <label>调用方法类型：</label>
        <select name="srBean.urlType" id="urlType" class="uSelect">
          <option value="GET" <c:if test="${srBean.urlType=='GET'}">selected</c:if>>GET</option>
          <option value="POST" <c:if test="${srBean.urlType=='POST'}">selected</c:if>>POST</option>
          <option value="PUT" <c:if test="${srBean.urlType=='PUT'}">selected</c:if>>PUT</option>
        </select>
      </p>
      <p>
        <label>Content-type：</label>
		<select name="srBean.contentType" id="contentType" class="uSelect">
          <option value="application/json" <c:if test="${srBean.contentType=='application/json'}">selected</c:if>>application/json</option>
          <option value="application/xml" <c:if test="${srBean.contentType=='application/xml'}">selected</c:if>>application/xml</option>
          <option value="text/plain" <c:if test="${srBean.contentType=='text/plain'}">selected</c:if>>text/plain</option>
        </select>
      </p>
      <p style="width: 100%">
        <label>访问地址：</label>
        <input type="text" name="srBean.url" id="url" value="${srBean.url}" class="required uInput" style="width: 66%"/>
      </p>
      <div style="width: 100%; height: 150px; margin-top: 120px; overflow:auto; overflow-x:hidden;">
      	<table class="list nowrap itemDetail" addButton="新建Header" id="addHeader" width="100%">
        <thead>
          <tr>
            <th type="text" width="200" name="headers[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">header名称</th>
            <th type="text" width="300" name="headers[#index#].value" size="20" fieldAttrs="{maxlength:100}">header值</th>
            <th type="del" width="60">操作</th>
          </tr>
        </thead>
        <tbody id="theader">
          <c:forEach items="${srBean.headers}" var="item" varStatus="var">
            <tr class="unitBox">
              <td><input type="text" id="headers" name="headers[${var.index}].key" submitName="headers[${var.index}].key"  value="${item.key}" class="required textInput" /></td>
              <td><input type="text" name="headers[${var.index}].value" submitName="headers[${var.index}].value" value="${item.value}" /></td>
              <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a> </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      </div>
      <div style="width: 100%; height: 150px; overflow:auto; overflow-x:hidden;">
      	<table class="list nowrap itemDetail" addButton="新建Body" id="addBody" width="100%">
        <thead>
          <tr>
            <th type="text" width="200" name="bodys[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">body名称</th>
            <th type="text" width="300" name="bodys[#index#].value" size="20" fieldAttrs="{maxlength:100}">body值</th>
            <th type="del" width="60">操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${srBean.bodys}" var="item" varStatus="var">
            <tr class="unitBox">
              <td><input type="text" name="bodys[${var.index}].key" submitName="bodys[${var.index}].key"  value="${item.key}" class="required textInput" /></td>
              <td><input type="text" name="bodys[${var.index}].value" submitName="bodys[${var.index}].value" value="${item.value}" /></td>
              <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a> </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      </div>
    </div>
    <div class="formBar">
      <ul>
        <li>
          <div class="buttonActive">
            <div class="buttonContent">
              <input type="hidden" name="flowId" value="${flowId}" />
              <input type="hidden" name="resourceId" value="${resourceId}" />
              <input type="hidden" name="sort" value="${sort}" />
              <button type="button" onclick="javascript:submitSrProcessorForm(this);">保存</button>
            </div>
          </div>
        </li>
        <li>
          <div class="button">
            <div class="buttonContent">
              <button type="button" class="close">取消</button>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </form>
</div>
