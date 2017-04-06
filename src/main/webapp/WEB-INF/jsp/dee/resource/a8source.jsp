<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/a8source.js" type="text/javascript"></script>
<script type="text/javascript">
</script>

<div class="pageContent">
  <form method="post" action="/dee/a8source!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="a8sourceForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
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
        <input type="text" name="writerBean.a8url" value="${writerBean.a8url}" class="required uInput"/>
      </p>
      <p>
        <label>A8WS账户：</label>
        <input type="text" name="writerBean.userName" value="${writerBean.userName}" readonly="readonly" class="required uInput" />
      </p>
      <p>
        <label>A8WS密码：</label>
        <input type="text" name="writerBean.password" value="${writerBean.password}" class="required uInput"/>
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
        		<option value="8" selected>表单</option>
        		<option value="28" >组织机构</option>
        		<option value="35" >枚举</option>
        		<option value="38" >消息服务</option>
        	</select>
      </p>
       <p>
        <label>方法：</label>
        	<select id="selFunc" name="selFunc" class="uSelect" <c:if test="${resourceId != null && resourceId != '' }">disabled</c:if>>
        		<option value="8" selected>发起流程表单</option>
        		<option value="20" >无流程表单</option>
        	</select>
      </p>
      <p></p>
      <p><label>参数：</label></p>
      <table class="list nowrap" width="100%">
        <thead>
          <tr>
            <th type="text" width="20%" name="items[#index#].key" size="20" fieldAttrs="{maxlength:100}">参数名称</th>
            <th type="text" width="30%" name="items[#index#].value" size="20" fieldAttrs="{maxlength:100}">参数值</th>
            <!-- <th width="50%">描述</th> -->
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${writerBean.map}" var="item" varStatus="var">
                <c:if test="${item.key=='senderLoginName' || item.key=='templateCode' || item.key=='subject' || item.key=='attachments' || item.key=='param' || item.key=='relateDoc'}">
                <tr class="unitBox">
                  <c:choose>
                    <c:when test="${item.key=='senderLoginName'}">
                      <td><p>流程发起人登录名</p></td>
                    </c:when>
                    <c:when test="${item.key=='templateCode'}">
                      <td><p>A8表单模板号</p></td>
                    </c:when>
                    <c:when test="${item.key=='subject'}">
                      <td><p>流程标题</p></td>
                    </c:when>
                    <c:when test="${item.key=='attachments'}">
                      <td><p>附件ID</p></td>
                    </c:when>
                    <c:when test="${item.key=='param'}">
                      <td><p>是否立即发起(0:立即发起1:进入待发)</p></td>
                    </c:when>
                    <c:when test="${item.key=='relateDoc'}">
                      <td title="填写格式：col|附件ID;doc|附件ID"><p>上传附件类型（col：协同，doc：文档中心）</p></td>
                    </c:when>
                  </c:choose>
                  <c:choose>
                  	<c:when test="${item.key=='relateDoc'}">
                  		<td title="填写格式：col|附件ID;doc|附件ID">
                      		<input type="hidden" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" readonly="readonly" class="textInput" />
                      		<input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" />
                  		</td>
                  	</c:when>
                  	<c:otherwise>
                  		<td>
                      		<input type="hidden" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" readonly="readonly" class="textInput" />
                      		<input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" <c:if test="${item.key!='attachments'}">class="required"</c:if> />
                  		</td>
                  	</c:otherwise>
                  </c:choose>
              </tr>
              </c:if>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="formBar">
      <ul>
        <li>
          <div class="buttonActive">
            <div class="buttonContent">
              <input type="hidden" id="flowId" name="flowId" value="${flowId}" />
              <input type="hidden" id="resourceId" name="resourceId" value="${resourceId}" />
              <input type="hidden" id="sort" name="sort" value="${sort}" />
              <input type="hidden" name="writerBean.xmlns" value="${writerBean.xmlns}" />
              <input type="hidden" name="writerBean.methodName" value="${writerBean.methodName}" />
              <input type="hidden" name="writerBean.interfaceName" value="${writerBean.interfaceName}" />
              <button type="button" onclick="javascript:submitA8sourceForm(this);">保存</button>
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
