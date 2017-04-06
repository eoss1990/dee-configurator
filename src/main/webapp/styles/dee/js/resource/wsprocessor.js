function delRow(obj) {
    var tr = $(obj).closest("tr");
    tr.remove();
}

/**
 * 模态窗口关闭时，当前窗口设置设置为父窗口，防止多窗口出现模态窗口失效的问题。
 *
 * @returns {boolean}
 */
function closeMask(param) {
	var parentId = param.parent_id ? param.parent_id : "resourceDlg";
	$.pdialog._current = $("body").data(parentId);
	return true;
}

function submitWsProcessorForm(obj) {
	var dialog = returnDlg(obj);
	if (hasSpecialChar($("input[name='bean.dis_name']",dialog).val()) ||
		hasSpecialChar($("input[name='wsBean.serviceurl']",dialog).val()) ||
		hasSpecialChar($("input[name='wsBean.namespace']",dialog).val()) ||
		hasSpecialChar($("input[name='wsBean.method']",dialog).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#wsProcessorForm",dialog).submit();
}