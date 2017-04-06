<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/wsprocessor.js" type="text/javascript"></script>

<div class="pageContent">
  <form method="post" action="/dee/wsprocessor!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="wsProcessorForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <p></p>
      <p>
        <label style="width:25%">名称：</label>
        <label style="width:15px;"><a href="/dee/wsprocessor!autoAnalysis.do" mask="true" resizable="false" maxable="false" target="dialog"  rel="auto" close="closeMask" param="{parent_id:'${resourceId}'}" title="WSDL信息填写" width="500" height="300"><img style="width:17px;height:17px;cursor: pointer;" src="/styles/dee/themes/images/analysis.png"  title="！此按钮作用为自动解析wsdl"/></a></label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" class="required uInput" maxlength="50"/>
      </p>
      <p>
        <label>WebService地址：</label>
        <input type="text" name="wsBean.serviceurl" id="address" value="${wsBean.serviceurl}" class="required uInput" />
      </p>
      <p>
        <label>命名空间：</label>
        <input type="text" name="wsBean.namespace" id="namespace" value="${wsBean.namespace}" class="required uInput"/>
      </p>
      <p>
        <label>方法名称：</label>
        <input type="text" name="wsBean.method" id="mtname" value="${wsBean.method}" class="required uInput"/>
      </p>
      <p>
        <label style="width:25%">用户名：</label>
        <label style="width:15px;"><img id="img_userName" src="/styles/dee/themes/images/wsAuth.png"  title="!注释：提供WSS4J以及SOAPHeader两种身份验证方式。使用SOAPHeader身份验证时，用户名与密码应为括号内格式（headerName:fieldName:fieldKey）"/></label>
        <input type="text" name="wsBean.username" value="${wsBean.username}" class="uInput"/>
      </p>
      <p>
        <label style="width:25%">密码：</label>
        <label style="width:15px;"><img id="img_passWord" src="/styles/dee/themes/images/wsAuth.png"  title="!注释：提供WSS4J以及SOAPHeader两种身份验证方式。使用SOAPHeader身份验证时，用户名与密码应为括号内格式（headerName:fieldName:fieldKey）"/></label>
        <input type="text" name="wsBean.password" value="${wsBean.password}" class="uInput"/>
      </p>
      <p>
        <label>返回值类型：</label>
        <select name="wsBean.returnType" id="returnType" class="uSelect">
          <option value="string" <c:if test="${wsBean.returnType=='string'}">selected</c:if>>字符串类型--string</option>
          <option value="number" <c:if test="${wsBean.returnType=='number'}">selected</c:if>>数字类型--number</option>
          <option value="void" <c:if test="${wsBean.returnType=='void'}">selected</c:if>>空类型--void</option>
        </select>
      </p>
      <p>
        <label>超时时间（秒）：</label>
        <input type="text" name="wsBean.timeout" value="<c:if test="${wsBean.timeout==null}">30</c:if><c:if test="${wsBean.timeout!=null}">${wsBean.timeout}</c:if>" class="uInput positiveinteger"/>
      </p>
      <br/>
      <br/>
      <br/>
      <table class="list nowrap itemDetail" addButton="新建参数" id="addPara" width="100%">
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
              <td><input type="text" name="items[${var.index}].key" submitName="items[${var.index}].key"  value="${item.key}" class="required textInput" /></td>
              <td><input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" /></td>
              <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a> </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="formBar">
      <ul>
        <li>
          <div class="buttonActive">
            <div class="buttonContent">
              <input type="hidden" name="flowId" value="${flowId}" />
              <input type="hidden" name="resourceId" value="${resourceId}" />
              <input type="hidden" name="sort" value="${sort}" />
              <button type="button" onclick="javascript:submitWsProcessorForm(this);">保存</button>
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
