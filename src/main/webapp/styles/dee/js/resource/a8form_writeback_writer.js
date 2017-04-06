var writeBackSetting = {
    check: {
        enable: true,
        chkboxType: { "Y": "ps", "N": "ps" }
    },
    async: {
        enable: true,
        url: "/dee/a8form_writeback_writer!selectOneDs.do",     // url
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

var zNodes = [{id:0, pId:-1, name:"空节点"}];

$("#writeBackSelect").keydown(function(event) {
	if (event.keyCode == 13) { 
		var searchIdName = $(event.target).attr("id");
		if(searchIdName == null || searchIdName.indexOf("searchText")<0) return;
		var dialogId = searchIdName.substr(10,searchIdName.length);
		searchZtree("writeBackDsTree",$("#searchText"+dialogId).val());
	} 
}); 

function setWriteBackSelectTree(obj) {
    var dlg = getWriteBackWriterDlg(obj);
	//清空查询条件
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    $("#searchText"+resourceId).val("");
	
    var dsId = $("#ref_id", dlg).val();
    if (!dsId) {
        alertMsg.info('请选择数据源！');
        return;
    }
    $("#writeBackSelect", dlg).css({left: "130px", top: "28px"}).slideDown("fast");
    ajaxTodo("/dee/a8form_writeback_writer!selectDs.do?dsId=" + dsId, function (json) {
        writeBackWriterCallBack(json);
    });
    $("body").bind("mousedown", onWriteBackWriterMDown);
}

function onWriteBackWriterMDown(event) {
    if (!(event.target.id == "writeBackSelect" || $(event.target).parents("#writeBackSelect").length>0)) {
        hideWriteBackMenu();
    }
}

function hideWriteBackMenu() {
    $.fn.zTree.destroy("dsTree");
    $("#writeBackSelect").fadeOut("fast");
    $("body").unbind("mousedown", onWriteBackWriterMDown);
}

function writeBackWriterCallBack(json) {
    if (json.statusCode == DWZ.statusCode.ok) {
        zNodes = json.jsonData;
        var newNodes = new Array();
        for (var i=0; i<zNodes.length;i++) {
            // 在这儿，从表是不可选的
           // if (zNodes[i].id.indexOf("formson_") >= 0) {
          //      zNodes[i].nocheck = true;
           // }
        	//不显示从表
        	if (zNodes[i].id.indexOf("formson_") < 0){
        		newNodes.push(zNodes[i]);
        	}
        }

        var zTreeObj = $.fn.zTree.init($("#writeBackDsTree"), writeBackSetting, newNodes);

        var currentId = $("#writerbean_formid").val();
        if (currentId) {
            var treeNode = zTreeObj.getNodeByParam("nForm", currentId);
            if (treeNode) {
                zTreeObj.checkNode(treeNode, true);
                var parentTreeNode = treeNode.getParentNode();
                while (parentTreeNode) {
                    zTreeObj.expandNode(parentTreeNode, true)
                    parentTreeNode = parentTreeNode.getParentNode();
                }
            }
        }
    } else {
        DWZ.ajaxDone(json);
    }
}

function ajaxDataFilter(treeId, parentNode, responseData){
    if (parentNode) {
        var parentNodeId = parentNode.id;
        var jsonData = responseData.jsonData;
        var i;
        if (parentNodeId.indexOf("formson_") >= 0) {
            for (i = 0; i < jsonData.length; i++) {
                jsonData[i].nocheck = true;
            }
        } else {
            var ids = $("input[name$='].id']", $("#writeback_param"));
            for (i = 0; i < jsonData.length; i++) {
                for (var j = 0; j < ids.length; j++) {
                    var id = $(ids[j]).val();
                    if (jsonData[i].id == id) {
                        jsonData[i].checked = true;
                    }
                }
            }
        }
    }
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
    if (!msg || msg.length == 0) {
        return;
    }
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    treeNode.icon = "";
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
    var tableName = "", tableShowName = "";
    var col = "";
    var index = 0;
    out: for (var i = 0, l = nodes.length; i < l; i++) {
        if (nodes[i].isParent) {
            tableName += nodes[i].getParentNode().id.substring(0, nodes[i].getParentNode().id.length -1);
            tableShowName += nodes[i].getParentNode().name;
        } else {
            var ids = $("input[name$='].id']", $("#writeback_param"));
            for (var j = 0; j < ids.length; j++) {
                var id = $(ids[j]).val();
                if (nodes[i].id == id) {
                    var sourceTr = $(ids[j]).closest("tr");
                    var sourceId = $("input[name$='].id']", sourceTr).val();
                    var sourceName = $("input[name$='].name']", sourceTr).val();
                    var sourceValue = $("input[name$='].value']", sourceTr).val();
                    col += "<tr class=\"unitBox\">" +
                        "<td>" +
                        "<label>" + sourceName + "</label>" +
                        "<input type=\"hidden\" name=\"formWriteDtos[" + index + "].id\" value='" + sourceId + "'  />" +
                        "<input type=\"hidden\" name=\"formWriteDtos[" + index + "].name\" value='" + sourceName + "'  />" +
                        "</td>" +
                        "<td><input type='text' name=\"formWriteDtos[" + index + "].value\" value='" + sourceValue + "' maxlength='100' class='required textInput' /></td>" +
                        "<td><a href=\"javascript:void(0);\" onClick=\"javascript:delRow(this);\" class=\"btnDel\"></a></td>"
                    "</tr>";
                    index++;
                    continue out;
                }
            }

            col += "<tr class=\"unitBox\">" +
                "<td>" +
                "<label>" + nodes[i].name + "</label>" +
                "<input type=\"hidden\" name=\"formWriteDtos[" + index + "].id\" value='" + nodes[i].id + "'  />" +
                "<input type=\"hidden\" name=\"formWriteDtos[" + index + "].name\" value='" + nodes[i].name + "'  />" +
                "</td>" +
                "<td><input type='text' name=\"formWriteDtos[" + index + "].value\" value='' maxlength='100' class='required textInput' /></td>" +
                "<td><a href=\"javascript:void(0);\" onClick=\"javascript:delRow(this);\" class=\"btnDel\"></a></td>"
                "</tr>";
            index++;
        }
    }
    $("#writerbean_formname").val(tableShowName);
    $("#writerbean_formid").val(tableName);
    $("#writeback_param").html(col);
}

/**
 * 数据源切换
 * @param obj #ref_id
 */
function selectWriteBackRefId(obj) {
    var dlg = getWriteBackWriterDlg(obj);
    $("#writerbean_formname", dlg).val("");
    $("#writerbean_formid", dlg).val("");
    $("#writeback_param", dlg).html("");
}

/**
 * 表单提交事件
 * @param obj
 */
function submitWriteBackWriterForm(obj) {
    var dlg = getWriteBackWriterDlg(obj);
    if (hasSpecialChar($("input[name='bean.dis_name']", dlg).val()) ||
        hasSpecialChar($("input[name='writerBean.formName']", dlg).val())) {
        alertMsg.error(DWZ.msg("alertHasSpecialChar"));
        return false;
    }

    var writeBackParam = $("#writeback_param", dlg);
    var valueParams = $("input[type='text']", writeBackParam);
    for (var index = 0; index < valueParams.length; index++) {
        if (hasSpecialChar($(valueParams[index]).val())) {
            alertMsg.error(DWZ.msg("alertHasSpecialChar"));
            return false;
        }
    }

    if ($("#writeback_param", dlg).children("tr").size()  < 1) {
        alertMsg.error("请选择表单字段！");
        return false;
    }

    $("#writeBackWriterForm", dlg).submit();
}

function getWriteBackWriterDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}