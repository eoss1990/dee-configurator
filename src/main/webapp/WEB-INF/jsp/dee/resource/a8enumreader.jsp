<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/a8enumreader.js" type="text/javascript"></script>

<div class="pageContent">
  <form method="post" action="/dee/a8enumreader!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="a8enumReaderForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
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
        <input type="text" name="a8EnumReaderBean.a8url" value="${a8EnumReaderBean.a8url}" class="required uInput"/>
      </p>
      <p>
        <label>A8WS账户：</label>
        <input type="text" name="a8EnumReaderBean.userName" value="${a8EnumReaderBean.userName}" readonly="readonly" class="required uInput" />
      </p>
      <p>
        <label>A8WS密码：</label>
        <input type="text" name="a8EnumReaderBean.password" value="${a8EnumReaderBean.password}" class="required uInput"/>
      </p>
       <p>
        <label>接口类型：</label>
        	<select id="selInterface" name="selInterface" class="uSelect"  <c:if test="${resourceId != null && resourceId != '' }">disabled</c:if>>
        		<option value="34" selected>axis2</option>
        		<option value="36" >rest</option>
        	</select>
      </p>
       <p>
        <label>服务：</label>
        	<select id="selSrv" name="selSrv" class="uSelect">
        		<option value="1" selected>枚举</option>
        	</select>
      </p>
       <p>
        <label>方法：</label>
        	<select id="selFunc" name="selFunc" class="uSelect">
        		<option value="1" selected>枚举值导出</option>
        	</select>
      </p>
      <p>
        <label>数据源：</label>
        <select name="a8EnumReaderBean.dataSource" class="uSelect">
          <c:forEach var="ds" items="${dbList}">
            <c:choose>
              <c:when test="${ds.resource_id==a8EnumReaderBean.dataSource}">
                <option value="${ds.resource_id}" selected>${ds.dis_name}</option>
              </c:when>
              <c:otherwise>
                <option value="${ds.resource_id}">${ds.dis_name}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </p>
      <p>
        <label>选择导出枚举：</label>
        <textarea id="selectReaderEnums" style="height:100px;width:543px; position: absolute; left: 130px" name="a8EnumReaderBean.enumNames" readonly="readonly" class="required">${a8EnumReaderBean.enumNames}</textarea>
      </p>
    </div>
    <div class="formBar">
      <ul>
        <li>
          <div class="buttonActive">
            <div class="buttonContent">
              <input type="hidden" id="flowId" name="flowId" value="${flowId}" />
              <input type="hidden" id="resourceId" name="resourceId" value="${resourceId}" />
              <input type="hidden" id="sort" name="sort" value="${sort}" />
              <input type="hidden" name="a8EnumReaderBean.xmlns" value="${a8EnumReaderBean.xmlns}" />
              <input type="hidden" name="a8EnumReaderBean.methodName" value="${a8EnumReaderBean.methodName}" />
              <input type="hidden" name="a8EnumReaderBean.interfaceName" value="${a8EnumReaderBean.interfaceName}" />
              <input type="hidden" name="a8EnumReaderBean.enumIds" value="${a8EnumReaderBean.enumIds}" />
              <button type="button" onclick="javascript:submitA8EnumReaderForm(this);">保存</button>
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
    <div id="a8EnumReaderTableSelect" class="pageForm" style="display:none;position: absolute; width:400px; height:200px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
      <ul id="dsTree${resourceId}" class="ztree" ></ul>
    </div>
  </form>
</div>