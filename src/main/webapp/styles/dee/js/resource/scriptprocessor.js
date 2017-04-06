function submitScriptProcessorForm(obj) {
	var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(dialogId ==""||dialogId==null)
		dialogId = "resourceDlg";
	var dialog = $("body").data(dialogId);
	
	if(editor[dialogId])
		editor[dialogId].save();
	
	if (hasSpecialChar($("input[name='bean.dis_name']",dialog).val())) {
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	if (!$.trim($("#scriptInfo",dialog).val())) {
		alertMsg.error("请输入内容!");
		return false;
	}
	document.scriptProcessorForm.action = '/dee/script!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav'
	$("#scriptProcessorForm",dialog).submit();
}
function compileScriptForm(obj) {
	var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(dialogId ==""||dialogId==null)
		dialogId = "resourceDlg";
	var dialog = $("body").data(dialogId);
	
	if(editor[dialogId])
		editor[dialogId].save();
	
	if (hasSpecialChar($("input[name='bean.dis_name']",dialog).val())) {
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	if (!$.trim($("#scriptInfo",dialog).val())) {
		alertMsg.error("请输入内容!");
		return false;
	}
	document.scriptProcessorForm.action = '/dee/script!compile.do'
	$("#scriptProcessorForm",dialog).submit();
} 