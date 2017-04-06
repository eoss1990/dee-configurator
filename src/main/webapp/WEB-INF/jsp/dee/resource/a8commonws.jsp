<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/a8commonws.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>
<script type="text/javascript">
function delRow(obj) {
    var tr = $(obj).closest("tr");
    tr.remove();
}
</script>

<div class="pageContent" id="a8commonwsDiv${resourceId}">
  <form method="post" action="/dee/a8commonws!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="a8commonwsForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
    <div id="biggest" class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
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
        		<option value="8">发起流程表单</option>
        		<option value="20" selected>无流程表单</option>
        	</select>
      </p>
      <p></p>
      <p><label>参数：</label></p>
      <table class="list nowrap" width="100%">
        <thead>
          <tr>
            <th type="text" width="20%" name="items[#index#].key" size="20" fieldAttrs="{maxlength:100}">参数名称</th>
            <th type="text" width="30%" name="items[#index#].value" size="20" fieldAttrs="{maxlength:100}">参数值</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${items}" var="item" varStatus="var">
              <c:if test="${item.key=='loginName' || item.key=='formCode' || item.key=='valiFieldAry'}">
                <tr class="unitBox">
                  <c:choose>
                    <c:when test="${item.key=='loginName'}">
                      <td><p>A8表单登录名</p></td>
                    </c:when>
                    <c:when test="${item.key=='formCode'}">
                      <td><p>A8表单模板号</p></td>
                    </c:when>
                    <c:when test="${item.key=='valiFieldAry'}">
                      <td><p>表单主键（ 格式如下：[id1,id2,id3] ）</p></td>
                    </c:when>
                  </c:choose>
                  <td>
                      <input type="hidden" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" readonly="readonly" class="textInput" />
                      <input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" class="required" />
                  </td>
              </tr>
              </c:if>
          </c:forEach>
        </tbody>
      </table>
      <p><label>外键关联：</label></p>
      <br/>
      <br/>
      <br/>
      <table id="a8commonws_table${resourceId}" class="list nowrap itemDetail" addButton="新建外键关联" width="100%">
        <thead>
          <tr>
            <th type="text" width="200" name="sqlItems[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">目标表</th>
            <th type="textarea" width="300" name="sqlItems[#index#].value" cols="60" rows="2" fieldClass="required">关联外键</th>
            <th type="del" width="60">操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${sqlItems}" var="item" varStatus="var">
            <tr class="unitBox">
              <td><input type="text" name="sqlItems[${var.index}].key" submitName="sqlItems[${var.index}].key" value="${item.key}" class="required textInput" /></td>
              <td>
                <textarea class="required textInput valid" rows="2" cols="60" name="sqlItems[${var.index}].value"  submitName="sqlItems[${var.index}].value">${item.value}</textarea>
                <a href="javascript:void(0)"  name="deeTreeSelect">选择</a>
              </td>
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
              <input type="hidden" id="flowId" name="flowId" value="${flowId}" />
              <input type="hidden" id="resourceId" name="resourceId" value="${resourceId}" />
              <input type="hidden" id="sort" name="sort" value="${sort}" />
              <input type="hidden" name="writerBean.xmlns" value="${writerBean.xmlns}" />
              <input type="hidden" name="writerBean.methodName" value="${writerBean.methodName}" />
              <input type="hidden" name="writerBean.interfaceName" value="${writerBean.interfaceName}" />
              <button type="button" onclick="javascript:submitA8commonwsForm(this);">保存</button>
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
    <div id="tableSelect${resourceId}" class="pageForm" style="display:none;position: absolute; width:452px; height:100px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
      <label>请选择数据源：</label><select id="dbList${resourceId}"></select>
      <input type="text" id="searchText${resourceId}"   size="15" alt="回车查询"/><br/>
      <ul id="dsTree${resourceId}" class="ztree" ></ul>
    </div>
  </form>
</div>
<script type="text/javascript">
  $(function () {
    // 窗口ID
    var dialogId = "${resourceId}";
    if (!dialogId) {
      dialogId = "";
    }

    $("a[name='deeTreeSelect']", $("#a8commonws_table"+dialogId)).die().live('click', function () {
      debugger;
      
   		showTree($(this).parents('tr:first'));
    });

    $("#dbList"+dialogId).live('change', function () {
      var url = "/dee/a8commonws!selectDs.do?dsId=" + $(this).val();
      ajaxTodo(url, function (json) {
    		//清空查询条件
    		$("#searchText"+dialogId).val("");
    		
       		$.fn.zTree.init($("#dsTree" + dialogId), a8commonWsSetting, json.jsonData);
      });
    });
  });
</script>
