<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/sapwsprocessor.js" type="text/javascript"></script>

<div class="pageContent">
  <form id="sapWsForm" method="post" action="/dee/sapws!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
            class="pageForm required-validate" onSubmit="return validateCallback(this,processDone)">
            
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <p></p>
      <p>
        <label>名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" maxlength="50" class="required uInput" />
      </p>
      <p>
        <label>服务地址：</label>
        <input type="text" name="wsBean.serviceurl" value="${wsBean.serviceurl}" maxlength="200" class="required uInput" />
      </p>
      <p>
        <label>命名空间：</label>
        <input type="text" name="wsBean.namespace" value="${wsBean.namespace}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>方法名称：</label>
        <input type="text" name="wsBean.method" value="${wsBean.method}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>用户名：</label>
        <input type="text" name="wsBean.username" value="${wsBean.username}" maxlength="200" class="uInput"/>
      </p>
      <p>
        <label>密码：</label>
        <input type="password" name="wsBean.password" value="${wsBean.password}" maxlength="200" class="uInput"/>
      </p>
      <p>
        <label>返回值类型：</label>
        <select name="wsBean.returnType" id="returnType" class="uSelect">
          <option value="string" <c:if test="${wsBean.returnType=='string'}">selected</c:if>>字符串类型--string</option>
          <option value="number" <c:if test="${wsBean.returnType=='number'}">selected</c:if>>数字类型--number</option>
          <option value="void" <c:if test="${wsBean.returnType=='void'}">selected</c:if>>空类型--void</option>
        </select>
      </p>
      <p></p>
      <br/>
      <br/>
      <br/>
      <div>
        <table class="list nowrap itemDetail" addButton="新建参数" width="100%">
          <thead>
            <tr>
              <th type="text" width="200" name="items[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">参数名称</th>
              <th type="text" width="300" name="items[#index#].value" size="20" fieldAttrs="{maxlength:100}">参数值</th>
              <th type="del" width="60">操作</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${wsBean.parameter}" var="item" varStatus="var">
              <tr class="unitBox">
                <td><input type="text" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" class="required textInput" /></td>
                <td><input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" /></td>
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
              <button type="button" onclick="submitSapWs(this)">保存</button>
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
