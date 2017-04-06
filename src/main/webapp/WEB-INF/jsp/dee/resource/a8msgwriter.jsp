<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/a8msgwriter.js" type="text/javascript"></script>

<div class="pageContent">
  <form id="msgWriterForm" method="post" action="/dee/a8msgwriter!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
            class="pageForm required-validate" onSubmit="return checkInput(this);">
            
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <p>
      </p>
      <p>
        <label>名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" class="required uInput" maxlength="50"/>
      </p>
      <p>
        <label>A8地址：</label>
        <input type="text" name="msgb.a8url" value="${msgb.a8url}" class="required uInput"/>
      </p>
      <p>
        <label>A8WS账户：</label>
        <input type="text" name="msgb.userName" value="${msgb.userName}" readonly="readonly" class="required uInput" />
      </p>
      <p>
        <label>A8WS密码：</label>
        <input type="text" name="msgb.password" value="${msgb.password}" class="required uInput"/>
      </p>
       <p>
        <label>接口类型：</label>
        	<select id="selInterface" name="selInterface" class="uSelect"  <c:if test="${resourceId != null && resourceId != '' }">disabled</c:if>>
        		<option value="8" selected>axis2</option>
        		<option value="37" >rest</option>
        	</select>
      </p>
       <p>
        <label>服务：</label>
        	<select id="selSrv" name="selSrv" class="uSelect" <c:if test="${resourceId != null && resourceId != '' }">disabled</c:if>>
        		<option value="8">表单</option>
        		<option value="28">组织机构</option>
        		<option value="35" >枚举</option>
        		<option value="38" selected>消息服务</option>
        	</select>
      </p>
       <p>
        <label>方法：</label>
        	<select id="selFunc" name="selFunc" class="uSelect" >
        		<option value="" selected>A8消息推送</option>
        	</select>
      </p>
      <p>
        <label>消息内容：</label>
        <input type="text" name="msgb.msgContent" value="${msgb.msgContent}" class="required uInput"/>
      </p>
      <p></p>
      <br/>
      <br/>
      <br/>
      <div>
        <table class="list nowrap itemDetail" addButton="新建" width="100%">
          <thead>
            <tr>
              <th type="text" width="200" name="items[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">消息接收人登录名</th>
              <th type="text" width="300" name="items[#index#].value" size="20" fieldAttrs="{maxlength:100}">对应链接</th>
              <th type="del" width="60">操作</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${msgb.map}" var="item" varStatus="var">
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
              <input type="hidden" id="flowId" name="flowId" value="${flowId}" />
              <input type="hidden" id="resourceId" name="resourceId" value="${resourceId}" />
              <input type="hidden" id="sort" name="sort" value="${sort}" />
              <input type="hidden" name="msgb.methodName" value="${msgb.methodName}" />
              <input type="hidden" name="msgb.interfaceName" value="${msgb.interfaceName}" />
              <input type="hidden" name="msgb.xmlns" value="${msgb.xmlns}" />
              <button type="submit">保存</button>
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
