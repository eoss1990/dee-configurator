/**
 * 枚举导入JS代码
 * @auther zhangfb
 */
var enumwriter_setting = {
    check: {
        enable: true,
        chkboxType: { "Y": "ps", "N": "ps" },
        chkStyle: "radio"
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
        onCheck: enumwriter_oncheck
    }
};

$(function() {
    $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumName]").die().live('click', function () {
        var dlg = getEnumWriterDlg(this);
        showEnumWriterTree($(this), dlg);
    });
    $("#a8enumWriterForm select[name=a8EnumWriterBean\\.dataSource]").change(function() {
        var dlg = getEnumWriterDlg(this);
        $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumName]", dlg).val("");
        $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumId]", dlg).val("");
    });
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
		else if(selSrv == "8"){
			//表单-发起流程表单
			url = "/dee/a8source!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";
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
});

function showEnumWriterTree(currentComponent, dlg) {
    var offset = currentComponent.offset();
    //  $("#a8EnumWriterTableSelect", dlg).css({left:"15px", top:offset.top - 10 + "px"}).slideDown("fast");
    $("#a8EnumWriterTableSelect", dlg).css({left: "134px", top: "175px"}).slideDown("fast");
    var dsId = $("#a8enumWriterForm select[name=a8EnumWriterBean\\.dataSource]", dlg).val();
    var url = "/dee/a8enumwriter!selectDs.do?dsId=" + dsId;

    ajaxTodo(url, function(data) {
        if (data.statusCode == 300) {
            alertMsg.error(data.message);
        } else if (data.statusCode == 200) {
            var resourceId = "";
            if (dlg.data("id") != "resourceDlg") {
                resourceId = dlg.data("id");
            }
            var zTreeObj = $.fn.zTree.init($("#dsTree"+resourceId, dlg), enumwriter_setting, data.jsonData);
            var enumId = $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumId]", dlg).val();
            // 根据枚举ID，将树的相应节点展开并选中
            if (enumId) {
                var treeNode = zTreeObj.getNodeByParam("id", enumId);
                if (treeNode) {
                    zTreeObj.checkNode(treeNode, true);
                    var parentTreeNode = treeNode.getParentNode();
                    while (parentTreeNode) {
                        zTreeObj.expandNode(parentTreeNode, true)
                        parentTreeNode = parentTreeNode.getParentNode();
                    }
                }
            }
        }
    });
    $("body").bind("mousedown", onEnumWriterMDown);
}

function hideEnumWriterMenu(dlg) {
    if ($("#a8EnumWriterTableSelect", dlg).length > 0) {
        var dialogId = dlg.data("id")
        $("#a8EnumWriterTableSelect", dlg).fadeOut("fast");
        if (dialogId == "resourceDlg") {
            $("#dsTree").html("");
            debugger;
        } else {
            $("#dsTree" + dialogId).html("");
            debugger;
        }
        $("body").unbind("mousedown", onEnumWriterMDown);
    }
}

function onEnumWriterMDown(event) {
    var dlg = getEnumWriterDlg(event.target);
    if (!(event.target.id == "a8EnumWriterTableSelect" || $(event.target).parents("#a8EnumWriterTableSelect").length > 0)) {
        if (dlg && dlg.data && dlg.data("id")) {
            hideEnumWriterMenu(dlg);
        }
    }
}

function enumwriter_oncheck(event, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj(treeId);

    var selectNodes = zTree.getCheckedNodes();
    for (var i=0; i<selectNodes.length; i++) {
        zTree.checkNode(selectNodes[i], false, false);
        zTree.checkNode(treeNode, true, false);
    }

    var selectNodes = zTree.getCheckedNodes();
    if (selectNodes.length > 0) {
        var enumName = (function () {
            var result = selectNodes[0].name;
            var node = selectNodes[0].getParentNode();
            while (node) {
                result = node.name + "＞" + result;
                node = node.getParentNode();
            }
            return result;
        })();
        var dlg = getEnumWriterDlg("#"+treeId);
        $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumName]", dlg).val(enumName);
        $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumId]", dlg).val(selectNodes[0].id);
    } else {
        $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumName]", dlg).val("");
        $("#a8enumWriterForm input[name=a8EnumWriterBean\\.enumId]", dlg).val("");
    }
}

function submitA8EnumWriterForm(obj) {
    var dlg = getEnumWriterDlg(obj);
    if (hasSpecialChar($("input[name='bean.dis_name']", dlg).val()) ||
        hasSpecialChar($("input[name='a8EnumWriterBean.a8url']", dlg).val()) ||
        hasSpecialChar($("input[name='a8EnumWriterBean.userName']", dlg).val()) ||
        hasSpecialChar($("input[name='a8EnumWriterBean.password']", dlg).val()))
    {
        alertMsg.error(DWZ.msg("alertHasSpecialChar"));
        return false;
    }
    $("#a8enumWriterForm", dlg).submit();
}

function getEnumWriterDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}