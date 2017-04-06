<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/orgsync_writer.js" type="text/javascript"></script>
<script type="text/javascript">
function delRow(obj) {
    var tr = $(obj).closest("tr");
    tr.remove();
}
</script>

<div class="pageContent">
  <form method="post" action="/dee/orgSyncWriter!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="orgSyncWriterForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
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
        		<option value="8">表单</option>
        		<option value="28" selected>组织机构</option>
        		<option value="35" >枚举</option>
        		<option value="38" >消息服务</option>
        	</select>
      </p>
       <p>
        <label>方法：</label>
        	<select id="selFunc" name="selFunc" class="uSelect" >
        		<option value="" selected>组织机构同步</option>
        	</select>
      </p>
      <p>
        <label>同步单位：</label>
        <input type="text" name="writerBean.accountName" value="${writerBean.accountName}" class="required uInput"/>
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
              <input type="hidden" name="writerBean.methodName" value="${writerBean.methodName}" />
              <input type="hidden" name="writerBean.interfaceName" value="${writerBean.interfaceName}" />
              <input type="hidden" name="writerBean.xmlns" value="${writerBean.xmlns}" />
              <button type="button" onclick="javascript:submitOrgSyncWriterForm(this);">保存</button>
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
