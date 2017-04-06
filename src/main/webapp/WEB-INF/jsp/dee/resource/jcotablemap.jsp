<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/include.inc.jsp"%>
    <script src="/styles/dee/js/resource/sapjco_processor.js" type="text/javascript"></script>
    <div class="pageContent">
    
    <form method="post" action="/dee/sapjcowriter!saveJcoTableMap.do?callbackType=closeCurrent"
        id="sapjcoTableForm" class="pageForm required-validate" onSubmit="return validateCallback(this,dialogAjaxDone)">
	    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
		<table class="list nowrap itemDetail" addButton="新建参数" width="100%">
		        <thead>
		          <tr>
		            <th type="text" width="24%" name="jcoTableData[#index#].jcoName" size="20"  fieldClass="required" fieldAttrs="{maxlength:100}">JCoTable名称</th>
		            <th type="text" width="24%" name="jcoTableData[#index#].jcoValue" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">JCoTable字段名</th>
		            <th type="text" width="24%" name="jcoTableData[#index#].docName" size="20"  fieldClass="required" fieldAttrs="{maxlength:100}">document表名</th>
		            <th type="text" width="24%" name="jcoTableData[#index#].docValue" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">document字段名</th>
		            <th type="del" width="4%">操作</th>
		          </tr>
		        </thead>
		        <tbody>
		          <c:forEach items="${jcoTableData}" var="item" varStatus="var">
		            <tr class="unitBox">
		              <td><input type="text" name="jcoTableData[${var.index}].jcoName" submitName="jcoTableData[${var.index}].jcoName" value="${item.jcoName}" class="required textInput" /></td>
		              <td><input type="text" name="jcoTableData[${var.index}].jcoValue" submitName="jcoTableData[${var.index}].jcoValue" value="${item.jcoValue}" class="required textInput"/></td>
		              <td><input type="text" name="jcoTableData[${var.index}].docName" submitName="jcoTableData[${var.index}].docName" value="${item.docName}" class="required textInput" /></td>
		              <td><input type="text" name="jcoTableData[${var.index}].docValue" submitName="jcoTableData[${var.index}].docValue" value="${item.docValue}" class="required textInput"/></td>
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
	              <input type="hidden" name="resourceId" value="${resourceId}" />
	              <button type="button" onclick="javascript:submitJcoTableMap(this);">保存</button>
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