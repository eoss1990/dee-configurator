function SubmitCustomAdapter(obj) {
	var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(dialogId == null||dialogId=="")
		dialogId = "resourceDlg";
	var dialog = $("body").data(dialogId);

	if(editor[dialogId])
		editor[dialogId].save();
	
	if($.trim($("#adapterInfo",dialog).val())==""){
		alertMsg.error("请输入内容!");
		$("#adapterInfo",dialog).focus();
		return;
	}
	if(!isWellFormed($("#adapterInfo",dialog).val())){
		alertMsg.error("内容格式不正确,请检查!");
		return;
	}
	$('#adapterForm',dialog).submit();
}