$("#bringId").live('click', function() {
	var tr = $(this).parent().parent().parent();
	var flowId = tr.attr("rel");
	var disName = tr.attr("disName");
	$("#flow_id",$("#sourceform")).val(flowId);
	$("#flow_name",$("#sourceform")).val(disName);
	$.pdialog.closeCurrent();
});


function submitDeeListSearchDlg()
{
	if(hasSpecialChar($("input[name='keywords']",$("#deeListSearchDlg")).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#deeListSearchDlg").submit();
}