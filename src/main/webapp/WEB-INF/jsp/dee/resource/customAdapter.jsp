<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>

<link rel="stylesheet" href="/codemirror-4.2/lib/codemirror.css">
<script src="/codemirror-4.2/lib/codemirror.js"></script>
<script src="/codemirror-4.2/addon/edit/matchbrackets.js"></script>
<script src="/codemirror-4.2/mode/xml/xml.js"></script>
<script src="/styles/dee/js/resource/customAdapter.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/common.js" type="text/javascript"></script>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<style>.CodeMirror {border-top: 1px solid #500; border-bottom: 1px solid #500;}</style>

<div class="pageContent">
  <form id="adapterForm" method="post" action="/dee/custom!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        class="pageForm required-validate" onSubmit="return validateCallback(this,processDone)">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="../resource/resourceType.jsp"></jsp:include>
      <div class="unit">
        <label>名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" class="required uInput" maxlength="50"/>
      </div>
      <br/>
      <br/>
      <div class="unit">
        <textarea id="adapterInfo" name="customBean.customXml" class="required">${cBean.customXml}</textarea>
      </div>
    </div>
    <div class="formBar">
      <ul>
        <li>
              <input type="hidden" name="flowId" value="${flowId}" />
              <input type="hidden" name="resourceId" value="${resourceId}" />
              <input type="hidden" name="sort" value="${sort}" />
              <div class="button">
	            <div class="buttonContent">
	              <button type="button" onclick="javascript:SubmitCustomAdapter(this);">保存</button>
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
	var	customDialog = $("body").data(dialogId);
	setTimeout(function() {
		var codeEditor=CodeMirror.fromTextArea($("#adapterInfo",customDialog)[0], {
			lineNumbers: true,
			mode: {name: "xml", alignCDATA: true}
		});
		editor[dialogId]=codeEditor;
},50);
});
</script>
    