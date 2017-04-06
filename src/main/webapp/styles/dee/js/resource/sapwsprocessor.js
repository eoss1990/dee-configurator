function delRow(obj) {
	var tr = $(obj).closest("tr");
	tr.remove();
}

function submitSapWs(obj)
{
	var dialog = returnDlg(obj);
	var values=getGroupItems("sapWsForm","items",null,dialog.data("id"));

	values.push($("input[name='bean.dis_name']",$("#sapWsForm",dialog)).val());
	values.push($("input[name='wsBean.serviceurl']",$("#sapWsForm",dialog)).val());
	values.push($("input[name='wsBean.namespace']",$("#sapWsForm",dialog)).val());
	values.push($("input[name='wsBean.method']",$("#sapWsForm",dialog)).val());
	values.push($("input[name='wsBean.username']",$("#sapWsForm",dialog)).val());
	values.push($("input[name='wsBean.password']",$("#sapWsForm",dialog)).val());

	if(hasSpecialChar(values))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#sapWsForm",dialog).submit();
}