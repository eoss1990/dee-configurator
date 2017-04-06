$(function() {
	//接口类型
	$("#selInterface").change(function() {
		if($("#selInterface").val() == "37"){
            var url = "/dee/restprocessor!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
            var dlgId = "resourceDlg";
            var title = "适配器配置";
            var options = {
                width:"800px",
                height:"600px"
            };
            $.pdialog.open(url, dlgId, title, options);
		}
	});
	//服务
	$("#selSrv").change(function() {
		var selSrv = $("#selSrv").val();
		var url = "";
		if(selSrv == "8"){
			//表单-发起流程表单
			url = "/dee/a8source!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
		}
		else if(selSrv == "28"){
			//组织机构同步
			url = "/dee/orgSyncWriter!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
		}
		else{
			//枚举
			url = "/dee/a8enumwriter!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
		}
        var dlgId = "resourceDlg";
        var title = "适配器配置";
        var options = {
            width:"800px",
            height:"600px"
        };
        $.pdialog.open(url, dlgId, title, options);
	});
});

function delRow(obj) {
	var tr = $(obj).closest("tr");
	tr.remove();
}

function checkInput(obj){
	var inputObj = $("input.required",obj);
	var len = inputObj.length;
	for(var i=0;i<len;i++){
		if(hasSpecialChar(inputObj[i].value)){
			alertMsg.error(DWZ.msg("alertHasSpecialChar"));
			return false;
		}
	}
	return validateCallback(obj,processDone);
}
