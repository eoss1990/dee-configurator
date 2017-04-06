$("#bringId").live('click', function() {
	var tr = $(this).parent().parent().parent();
	var flowId = tr.attr("rel");
	var disName = tr.attr("disName");
	$("#flow_id",$("#restFlow")).val(flowId);
	$("#flow_name",$("#restFlow")).val(disName);
	ajaxTodo("/dee/restSrv!selectOneFlow.do?flowId="+flowId,restSrvCallBack);
	ajaxTodo("/dee/restSrv!getRestSrvPath.do?flowId="+flowId,restPathSrvCallBack);
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

function restSrvCallBack(json){
	if (json.statusCode == DWZ.statusCode.ok){
		var params = json.jsonData;
		var tb = $("#restTb",$("#restFlow"));
		$("#restTb tbody",$("#restFlow")).empty(); 
		for(var i=0;i<params.length;i++){
			var tr = "<tr><td>"+params[i].paramName+"</td><td>String</td></tr>";
			tb.append(tr);
		}
		if(params.length > 0){
			$('#restLab',$("#restFlow")).html("注意：调用请使用【POST】方法，传入参数为JSON格式").css("color","red");
		}
		else{
			$('#restLab',$("#restFlow")).html("注意：调用请使用【GET】方法").css("color","red");
		}
	}
	else{
		DWZ.ajaxDone(json);
	}
}
function restPathSrvCallBack(json){
	if (json.statusCode == DWZ.statusCode.ok){
		var params = json.jsonData;
		$("#rest_url",$("#restFlow")).val(params.path);
	}
	else{
		DWZ.ajaxDone(json);
	}
}
