<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/jdbcwriter.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>

<div id="jdbcWriterDiv${resourceId}" class="pageContent">
  <form id="jdbcWriterForm" method="post" action="/dee/jdbcwriter!saveJdbcWriter.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
            class="pageForm required-validate" onSubmit="return validateCallback(this,processDone)">
    <div  id="biggest" class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <div class="unit">
				<label>名称：</label>
				<input type="text" id="dis_name" name="bean.dis_name" class="required uInput" value="${bean.dis_name}" maxlength="50"/>
      </div>
      <div class="unit">
				<label>数据源：</label>
				<select id="ref_id${resourceId}" name="ref_id" class="uSelect">
            	<c:forEach var="ds" items="${dsList}">
        		<c:if test="${ds.resource_id==jdbcBean.dataSource}">
        		<option value="${ds.resource_id}" selected>${ds.dis_name}</option>
        		</c:if>
        		<c:if test="${ds.resource_id!=jdbcBean.dataSource}">
        		<option value="${ds.resource_id}">${ds.dis_name}</option>
        		</c:if>
				</c:forEach>
				</select>			
      </div>
      <br/>
      <br/>
      <div>
        <table id="jdbcWriterTb${resourceId}" class="list nowrap itemDetail" addButton="新建" width="100%">
          <thead>
            <tr>
              <th type="text" width="200" name="items[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">表名</th>
              <th type="text" width="300" name="items[#index#].value" size="50" fieldClass="required">主键</th>
              <th type="btn" width="60" name="selectBtn" value="..."></th>
              <th type="del" width="60">操作</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${jdbcBean.map}" var="item" varStatus="var">
              <tr class="unitBox">
                <td><input type="text" name="items[${var.index}].key" value="${item.key}" class="required textInput" /></td>
                <td><input type="text" style="width:277px;" name="items[${var.index}].value" value="${item.value}" class="required textInput" /></td>
               <!--  <td><input type="button" name="selectBtn" value="..."/></td> -->
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
              <button type="button" onclick="submitJDBCWriter(this)">保存</button>
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
<div id="jdbcWriterSelect${resourceId}" class="pageForm" style="display:none;position: absolute; width:296px; height:150px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
	<ul id="dsTree${resourceId}" class="ztree" ></ul>
	<div style="position:absolute; right:5px; top:5px;">
				<input type="text" id="searchText${resourceId}"   size="15" alt="回车查询"/>
				</div>
</div>
<script type="text/javascript">
$(function(){
	var dialogId = "${resourceId}";
	if(dialogId==null||dialogId=="")
		dialogId = "";
	$("input[name='selectBtn']",$("#jdbcWriterTb"+dialogId)).die().live('click', function() {
		setSelectTree($(this).parents('tr:first'),dialogId);
	});
});
</script>
