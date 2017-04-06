// xslt模板选择事件
function xslChgTypeChangeFunc(obj) {
	var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(dialogId ==""||dialogId==null)
		dialogId = "resourceDlg";
	var dialog = $("body").data(dialogId);
	xslChgTypeChange($(obj).val(),dialog);
}

function xsltFormat(obj)
{
    var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (dialogId == "" || dialogId == null) {
        dialogId = "resourceDlg";
    }
    if (editor[dialogId]) {
        editor[dialogId].autoFormatAll();
    }
}

function xslChgTypeChange(xslChgType,dialog,xsEditor) {
	var xsltEditor = null;
	if(xsEditor)
		xsltEditor = xsEditor;
	else
		{
			if(editor[dialog.data("id")])
				xsltEditor=editor[dialog.data("id")];
		}
	var elementId;
	if (xslChgType == 0) {
		elementId = "a8FormFlowContent";
	} else if (xslChgType == 1) {
		elementId = "a8FormNoflowContent";
	} else if (xslChgType == 2) {
		elementId = "a8OrgInputContent";
	} else {
		elementId = "xsl";
	}
	
    xsltEditor.setValue($("#" + elementId,dialog).text());
	if (xslChgType != 3) {
	   	xsltEditor.setOption("readOnly", true);
	   	$("#formatXsltLi",dialog).hide();
	} else {
		xsltEditor.setOption("readOnly", false);
		$("#formatXsltLi",dialog).show();
	}
}

function submitXsltProcessorForm(obj) {
	var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(dialogId ==""||dialogId==null)
		dialogId = "resourceDlg";
	var dialog = $("body").data(dialogId);
	
	if(editor[dialogId])
		editor[dialogId].save();
	
	if (hasSpecialChar($("input[name='bean.dis_name']").val())) {
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#xsltProcessorForm",dialog).submit();
}