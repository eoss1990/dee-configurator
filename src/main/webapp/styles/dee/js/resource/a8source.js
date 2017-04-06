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
		if(selSrv == "28"){
			//组织机构同步
			url = "/dee/orgSyncWriter!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
		}
		else if(selSrv == "35"){
			//枚举
			url = "/dee/a8enumwriter!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
		}
		else{
			//消息发送
			url = "/dee/a8msgwriter!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
		}
        var dlgId = "resourceDlg";
        var title = "适配器配置";
        var options = {
            width:"800px",
            height:"600px"
        };
        $.pdialog.open(url, dlgId, title, options);
	});
	//方法
	$("#selFunc").change(function() {
		if($("#selFunc").val() == "20"){
            var url = "/dee/a8commonws!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
            var dlgId = "resourceDlg";
            var title = "适配器配置";
            var options = {
                width:"800px",
                height:"600px"
            };
            $.pdialog.open(url, dlgId, title, options);
		}
	});
    
});

function submitA8sourceForm(obj) {
    var dlg = getA8sourceDlg(obj);
	if (hasSpecialChar($("input[name='bean.dis_name']", dlg).val()) ||
		hasSpecialChar($("input[name='writerBean.a8url']", dlg).val()) ||
		hasSpecialChar($("input[name='writerBean.userName']", dlg).val()) ||
		hasSpecialChar($("input[name='writerBean.password']", dlg).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#a8sourceForm", dlg).submit();
}

function getA8sourceDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}