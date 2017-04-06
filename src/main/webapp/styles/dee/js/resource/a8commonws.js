var a8commonWsSetting = {
	check: {
		enable: true,
		chkboxType: { "Y": "ps", "N": "ps" },
		chkStyle: "radio",
		radioType: "all"
	},
	async: {
		enable: true,
		url: "/dee/selectDsTree!selectSonDs.do",
		autoParam: ["id","nId","nType","nForm"],
		dataFilter: ajaxDataFilter
	},
	data: {
		simpleData: {
			enable: true,
			idKey: "id",
			pIdKey: "pId",
			rootPId: 0
		}
	},
	callback: {
		beforeExpand: beforeExpand,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError,
		onCheck: onCheck
	}
};

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
		if($("#selFunc").val() == "8"){
            var url = "/dee/a8source!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
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

var gDescTable;     // 目标表
var gForeignKey;    // 关联外键
var cy;              //鼠标点击Y坐标
$("#fcmousey").click(function(){
    cy=window.event.clientY;
    alert(cy);
   
});

$("[id^=tableSelect]").keydown(function(event) { 
	if (event.keyCode == 13) { 
		var searchIdName = $(event.target).attr("id");
		if(searchIdName == null || searchIdName.indexOf("searchText")<0) return;
		var dialogId = searchIdName.substr(10,searchIdName.length);
		searchZtree("dsTree"+dialogId,$("#searchText"+dialogId).val());
	} 
}); 


function showTree(currentRow) {
    
    var obj=document.getElementById("biggest");
	var event = window.event || arguments.callee.caller.arguments[0];
    var parObj=obj; 
    var top=obj.offsetTop; 
    while(parObj = parObj.offsetParent){ 
    top+=parObj.offsetTop; 
   
    } 
    var cy=event.clientY-top+document.documentElement.scrollTop -113;
    var dialogId = getA8CommonWsResourceId(currentRow);

	//清空查询条件
	$("#searchText"+dialogId).val("");
	
	gDescTable = $("input[type='text']", currentRow);
	gForeignKey = $("textarea", currentRow);
	var offset = gForeignKey.offset();
	
	$("#tableSelect" + dialogId).css({left:"237px", top:cy+"px"}).slideDown("fast");

	ajaxTodo("/dee/a8commonws!selectAllA8Dbs.do", function(data) {
		var jsonData = data.jsonData;
		var options = [];
		options.push("<option value=''>请选择...</option>");
		for (var i in jsonData) {
			options.push("<option value='"+jsonData[i].resource_id+"'>"+jsonData[i].dis_name+"</option>");
		}
        $("#dbList" + dialogId).html(options.join(""));
	});
    $("#a8commonwsDiv" + dialogId).bind("mousedown", onA8CommonWsBodyDown);
}

function onA8CommonWsBodyDown(event) {
    var dialogId = getA8CommonWsResourceId(event.target);
    if (!(event.target.id == "tableSelect"+dialogId || $(event.target).parents("#tableSelect"+dialogId).length > 0)) {
        hideA8CommonWsMenu(dialogId);
    }
}

function hideA8CommonWsMenu(dialogId) {
	$("#tableSelect" + dialogId).fadeOut("fast");
    $("#dsTree" + dialogId).html("");
	$("#a8commonwsDiv" + dialogId).unbind("mousedown", onA8CommonWsBodyDown);
}

function ajaxDataFilter(treeId, parentNode, responseData){
	return responseData.jsonData;
}

function beforeExpand(treeId, treeNode) {
	if (!treeNode.isAjaxing) {
		return true;
	} else {
		alertMsg.info('正在下载数据中，请稍后展开节点。。。');
		return false;
	}
}

function onAsyncSuccess(event, treeId, treeNode, msg) {
	if (!msg || msg.length == 0) return;
	
	var zTree = $.fn.zTree.getZTreeObj(treeId);
    zTree.updateNode(treeNode); 
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	var zTree = $.fn.zTree.getZTreeObj(treeId);
	alertMsg.error('异步获取数据出现异常。');
	treeNode.icon = "";
	zTree.updateNode(treeNode);
}

function onCheck(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj(treeId);
	var nodes = zTree.getCheckedNodes(true);
	var tableName = "", colName="";
	for (var i=0, length=nodes.length; i<length; i++) {
		if (!nodes[i].isParent) {
			colName = nodes[i].name;
			if (nodes[i].getParentNode()) {
				tableName = nodes[i].getParentNode().id;
			}
			break;
		}
	}

	gDescTable.val(tableName);
	gForeignKey.val(colName);
}

function submitA8commonwsForm(obj) {
    var dlg = getA8CommonWsDlg(obj);
	if (hasSpecialChar($("input[name='bean.dis_name']", dlg).val()) ||
		hasSpecialChar($("input[name='writerBean.a8url']", dlg).val()) ||
		hasSpecialChar($("input[name='writerBean.userName']", dlg).val()) ||
		hasSpecialChar($("input[name='writerBean.password']", dlg).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#a8commonwsForm", dlg).submit();
}

function getA8CommonWsResourceId(obj) {
    return $(obj).parents("form").find("input[name='resourceId']").val();
}

function getA8CommonWsDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}