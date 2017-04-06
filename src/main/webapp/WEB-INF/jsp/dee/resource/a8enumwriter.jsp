<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/a8enumwriter.js" type="text/javascript"></script>

<div class="pageContent">
  <form method="post" action="/dee/a8enumwriter!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="a8enumWriterForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
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
        <input type="text" name="a8EnumWriterBean.a8url" value="${a8EnumWriterBean.a8url}" class="required uInput"/>
      </p>
      <p>
        <label>A8WS账户：</label>
        <input type="text" name="a8EnumWriterBean.userName" value="${a8EnumWriterBean.userName}" readonly="readonly" class="required uInput" />
      </p>
      <p>
        <label>A8WS密码：</label>
        <input type="text" name="a8EnumWriterBean.password" value="${a8EnumWriterBean.password}" class="required uInput"/>
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
        		<option value="8" >表单</option>
        		<option value="28" >组织机构</option>
        		<option value="35" selected>枚举</option>
        		<option value="38" >消息服务</option>
        	</select>
      </p>
       <p>
        <label>方法：</label>
        	<select id="selFunc" name="selFunc" class="uSelect">
        		<option value="" selected>枚举值导入</option>
        	</select>
      </p>
      <p>
        <label>数据源：</label>
        <select name="a8EnumWriterBean.dataSource" class="uSelect">
          <c:forEach var="ds" items="${dbList}">
            <c:choose>
              <c:when test="${ds.resource_id==a8EnumWriterBean.dataSource}">
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
        <label>枚举数据：</label>
        <input type="text" name="a8EnumWriterBean.enumDataXml" value="${a8EnumWriterBean.enumDataXml}" class="required uInput"/>
      </p>
      <p>
        <label>选择导入枚举：</label>
        <input id="selectWriterEnums" type="text" name="a8EnumWriterBean.enumName" value="${a8EnumWriterBean.enumName}" readonly="readonly" class="required uInput"/>
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
              <input type="hidden" name="a8EnumWriterBean.xmlns" value="${a8EnumWriterBean.xmlns}" />
              <input type="hidden" name="a8EnumWriterBean.methodName" value="${a8EnumWriterBean.methodName}" />
              <input type="hidden" name="a8EnumWriterBean.interfaceName" value="${a8EnumWriterBean.interfaceName}" />
              <input type="hidden" name="a8EnumWriterBean.enumId" value="${a8EnumWriterBean.enumId}" />
              <button type="button" onclick="javascript:submitA8EnumWriterForm(this);">保存</button>
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
    <div id="a8EnumWriterTableSelect" class="pageForm" style="display:none;position: absolute; width:422px; height:200px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
      <ul id="dsTree${resourceId}" class="ztree" ></ul>
    </div>
  </form>
</div>