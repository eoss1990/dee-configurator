<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>

<link rel="stylesheet" href="/codemirror-4.2/lib/codemirror.css">
<script src="/codemirror-4.2/lib/codemirror.js"></script>
<script src="/codemirror-4.2/lib/formatting.js"></script>
<script src="/codemirror-4.2/mode/xml/xml.js"></script>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/xsltprocessor.js" type="text/javascript"></script>
<style>.CodeMirror {border-top: 1px solid #500; border-bottom: 1px solid #500;}</style>
<div class="pageContent">
  <form method="post" action="/dee/xslt!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="xsltProcessorForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <div class="unit">
        <label>名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" maxlength="50" class="required uInput" />
      </div>
      <div class="unit">
        <label>XSLT模板：</label>
        <select id="xslChgType" name="xslChgType" class="uSelect" onchange="javascript:xslChgTypeChangeFunc(this);">
          <option value="0" <c:if test="${xslChgType==0}">selected</c:if>>A8流程表单XSLT</option>
          <option value="1" <c:if test="${xslChgType==1}">selected</c:if>>A8无流程表单XSLT</option>
          <%-- <option value="2" <c:if test="${xslChgType==2}">selected</c:if>>A8组织机构XSLT</option> --%>
          <option value="3" <c:if test="${xslChgType==3}">selected</c:if>>自定义XSLT</option>
        </select>
      </div>
      <div class="unit" id="xsltTmpContent"  style="height: 350px;">
        <textarea id="xsl" name="xsl" style="display:none;">${downBean.CONTENT}</textarea>
        <textarea id="a8FormFlowContent" name="a8FormFlowContent" style="display:none;">${a8FormFlowContent}</textarea>
        <textarea id="a8FormNoflowContent" name="a8FormNoflowContent" style="display:none;">${a8FormNoflowContent}</textarea>
        <textarea id="a8OrgInputContent" name="a8OrgInputContent" style="display:none;">${a8OrgInputContent}</textarea>
      </div>
    </div>
    <div class="formBar">
      <ul>
        <li id="formatXsltLi">
          <div class="button">
            <div class="buttonContent">
              <button type="button" onclick="javascript:xsltFormat(this);">格式化代码</button>
            </div>
          </div>
        </li>
        <li>
          <div class="button">
            <div class="buttonContent">
              <input type="hidden" name="flowId" value="${flowId}" />
              <input type="hidden" name="sort" value="${sort}" />
              <input type="hidden" name="file_name" value="${downBean.FILENEME}" />
              <input type="hidden" name="download_id" value="${downBean.DOWNLOAD_ID}" />
              <input type="hidden" id="resourceId" name="resourceId" value="${resourceId}" />
              <button type="button" onclick="javascript:submitXsltProcessorForm(this);">保存</button>
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
<script>
jQuery(function(){	
	var dialogId = "${resourceId}";
	if(dialogId==null||dialogId=="")
		dialogId = "resourceDlg";
	var xsltDialog = $("body").data(dialogId);

	setTimeout(function() {
	var	xsEditor = CodeMirror.fromTextArea($("#xsl",xsltDialog)[0], {
			lineNumbers: true,
			mode: {name: "xml", alignCDATA: true},
		});
      xsEditor.setSize("100%","100%");
	editor[dialogId]=xsEditor;
	xslChgTypeChange("${xslChgType}",xsltDialog,xsEditor);
},50);

});
</script>