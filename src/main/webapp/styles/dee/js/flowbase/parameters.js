function submitParams(obj)
{
    var uuid = $(obj).closest(".pageFormContent").attr("name");
	if(uuid!=null && uuid!="")
	{
		if(hasSpecialChar(getGroupItems("paramsForm","params")))
		{
			alertMsg.error(DWZ.msg("alertHasSpecialChar"));
			return false;
		}
		var action="/dee/flowbase!saveParams.do?uuid="+uuid;
        $(obj).closest("#paramsForm").attr("action", action);
        $(obj).closest("#paramsForm").submit();
	}
	else
		alertMsg.error("请先保存基础信息！");
}

function delRow(obj) {
	var tr = $(obj).closest("tr");
	tr.remove();
}