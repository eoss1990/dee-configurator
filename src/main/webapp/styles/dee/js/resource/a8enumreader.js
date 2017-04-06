/**
 * 枚举导出JS代码
 * @auther zhangfb
 */
var enumreader_setting = {
    check: {
        enable: true,
        chkboxType: { "Y": "ps", "N": "ps" }
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
        onCheck: enumreader_oncheck
    }
};

$(function() {
    $("#selectReaderEnums").die().live('click', function() {
        var dlg = getEnumReaderDlg(this);
        showEnumReaderTree($(this), dlg);
    });
    $("#a8enumReaderForm select[name=a8EnumReaderBean\\.dataSource]").change(function() {
        var dlg = getEnumReaderDlg(this);
        $("#a8enumReaderForm textarea[name=a8EnumReaderBean\\.enumNames]", dlg).val("");
        $("#a8enumReaderForm input[name=a8EnumReaderBean\\.enumIds]", dlg).val("");
    });
	$("#selInterface").change(function() {
		if($("#selInterface").val() == "36"){
            var url = "/dee/restprocessor!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1001";
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

function showEnumReaderTree(currentComponent, dlg) {
    var offset = currentComponent.offset();
    var height = currentComponent.height();
   // $("#a8EnumReaderTableSelect", dlg).css({left:"15px", top:(offset.top + height*0.5) + "px"}).slideDown("fast");
    $("#a8EnumReaderTableSelect", dlg).css({left: "135px", top: "258px"}).slideDown("fast");
    var dsId = $("#a8enumReaderForm select[name=a8EnumReaderBean\\.dataSource]", dlg).val();
    var url = "/dee/a8enumreader!selectDs.do?dsId=" + dsId;
    ajaxTodo(url, function(data) {
        if (data.statusCode == 300) {
            alertMsg.error(data.message);
        } else if (data.statusCode == 200) {
            var resourceId = "";
            if (dlg.data("id") != "resourceDlg") {
                resourceId = dlg.data("id");
            }
            var zTreeObj = $.fn.zTree.init($("#dsTree"+resourceId, dlg), enumreader_setting, data.jsonData);
            var enumId = $("#a8enumReaderForm input[name=a8EnumReaderBean\\.enumIds]", dlg).val();
            var enumIds = enumId.split(";");
            if (enumIds.length > 5) {
                var isAllPublic = enumIds[0].substring(enumIds[0].indexOf("{")+1, enumIds[0].indexOf("}"));
                var publicEnumTypeIds = enumIds[1].substring(enumIds[1].indexOf("{")+1, enumIds[1].indexOf("}")).split(",");
                var publicEnumIds = enumIds[2].substring(enumIds[2].indexOf("{")+1, enumIds[2].indexOf("}")).split(",");
                var unitIds = enumIds[3].substring(enumIds[3].indexOf("{")+1, enumIds[3].indexOf("}")).split(",");
                var unitEnumTypeIds = enumIds[4].substring(enumIds[4].indexOf("{")+1, enumIds[4].indexOf("}")).split(",");
                var unitEnumIds = enumIds[5].substring(enumIds[5].indexOf("{")+1, enumIds[5].indexOf("}")).split(",");
                checkAndExpandTreeNodes(zTreeObj, isAllPublic, publicEnumTypeIds, publicEnumIds, unitIds, unitEnumTypeIds, unitEnumIds);
            }
        }
    });
    $("body").bind("mousedown", onEnumReaderMDown);
}

function hideEnumReaderMenu(dlg) {
    if ($("#a8EnumReaderTableSelect", dlg).length > 0) {
        var dialogId = dlg.data("id");
        $("#a8EnumReaderTableSelect", dlg).fadeOut("fast");
        if (dialogId == "resourceDlg") {
            $("#dsTree").html("");
        } else {
            $("#dsTree" + dialogId).html("");
        }
        $("body").unbind("mousedown", onEnumReaderMDown);
    }
}

function onEnumReaderMDown(event) {
    var dlg = getEnumReaderDlg(event.target);
    if (!(event.target.id == "a8EnumReaderTableSelect" || $(event.target).parents("#a8EnumReaderTableSelect").length > 0)) {
        if (dlg && dlg.data && dlg.data("id")) {
            hideEnumReaderMenu(dlg);
        }
    }
}

function checkAndExpandTreeNodes(zTreeObj,
                                isAllPublic, publicEnumTypeIds, publicEnumIds,
                                unitIds, unitEnumTypeIds, unitEnumIds) {
    var treeNodes = [];
    var treeNode;
    if ("true" == isAllPublic) {
        treeNode = zTreeObj.getNodeByParam("id", "0");
        if (treeNode) {
            checkAllTreeNode(zTreeObj, treeNode)
        }
    } else {
        var publicEnums = publicEnumTypeIds.concat(publicEnumIds);
        for (var i = 0; i < publicEnums.length; i++) {
            treeNode = zTreeObj.getNodeByParam("id", publicEnums[i]);
            if (treeNode) {
                treeNodes.push(treeNode);
            }
        }
    }
    var unitEnums = unitIds.concat(unitEnumTypeIds).concat(unitEnumIds);
    for (var i = 0; i < unitEnums.length; i++) {
        treeNode = zTreeObj.getNodeByParam("id", unitEnums[i]);
        if (treeNode) {
            treeNodes.push(treeNode);
        }
    }

    for (var i = 0; i < treeNodes.length; i++) {
        checkAndExpandTreeNode(zTreeObj, treeNodes[i]);
    }
}

function checkAndExpandTreeNode(zTreeObj, treeNode) {
    checkAllTreeNode(zTreeObj, treeNode);
    var parentTreeNode = treeNode.getParentNode();
    while (parentTreeNode) {
        zTreeObj.expandNode(parentTreeNode, true)
        parentTreeNode = parentTreeNode.getParentNode();
    }
}

function checkAllTreeNode(zTreeObj, treeNode) {
    if (treeNode) {
        zTreeObj.checkNode(treeNode, true);
        for (var i in treeNode.children) {
            zTreeObj.checkNode(treeNode.children[i], true);
            checkAllTreeNode(zTreeObj, treeNode.children[i]);
        }
    }
}

function enumreader_oncheck(event, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj(treeId);

    var pNode = treeNode.getParentNode();
    while (pNode) {
        zTree.checkNode(pNode, false, false);
        pNode = pNode.getParentNode();
    }

    var isAllPublic = "false";
    var publicEnumTypeIds = [];
    var publicEnumTypeNames = [];
    var publicEnumIds = [];
    var publicEnumNames = [];
    var unitIds = [];
    var unitNames = [];
    var unitEnumTypeIds = [];
    var unitEnumTypeNames = [];
    var unitEnumIds = [];
    var unitEnumNames = [];

    function merge(ids, names, node) {
        ids.push(node.id);
        names.push(exchangeTreeNodeToEnumName(node));
    }

    if (zTree.getNodeByParam("id", "0").checked) {
        isAllPublic = "true";
    }

    var unitNodes = zTree.getNodesByParam("pId", "1");
    for (var i=0; i<unitNodes.length; i++) {
        if (unitNodes[i].checked) {
            merge(unitIds, unitNames, unitNodes[i]);
        }
    }

    var selectNodes = zTree.getCheckedNodes();
    for (var i=0; i<selectNodes.length; i++) {
        var ppNode = selectNodes[i].getParentNode();
        if (ppNode && ppNode.checked) {
            continue;
        }
        var flag = false;
        if (isAllPublic == "false") {
            if (parentNodeContainId(selectNodes[i], "0")) {
                if (selectNodes[i].enumtype == "0") {
                    merge(publicEnumIds, publicEnumNames, selectNodes[i]);
                } else if (selectNodes[i].enumtype == "3") {
                    merge(publicEnumTypeIds, publicEnumTypeNames, selectNodes[i]);
                }
                flag = true;
            }
        }

        if (!arrayContain(unitIds, selectNodes[i]) && !flag) {
            if (selectNodes[i].enumtype == "0") {
                merge(unitEnumIds, unitEnumNames, selectNodes[i]);
            } else if (selectNodes[i].enumtype == "3") {
                merge(unitEnumTypeIds, unitEnumTypeNames, selectNodes[i]);
            }
        }
    }

    var enumNames = "";
    if (isAllPublic == "true") {
        enumNames += "导出所有公用枚举:{是};\r\n\r\n";
    } else {
        if (publicEnumTypeNames.length > 0) {
            enumNames += "公用枚举类别:{" + publicEnumTypeNames.join("\r\n") + "};\r\n\r\n";
        }
        if (publicEnumNames.length > 0) {
            enumNames += "公用枚举:{" + publicEnumNames.join("\r\n") + "};\r\n\r\n";
        }
    }
    if (unitNames.length > 0) {
        enumNames += "单位:{" + unitNames.join("\r\n") + "};\r\n\r\n";
    }
    if (unitEnumTypeNames.length > 0) {
        enumNames += "单位枚举类别:{" + unitEnumTypeNames.join("\r\n") + "};\r\n\r\n";
    }
    if (unitEnumNames.length > 0) {
        enumNames += "单位枚举:{" + unitEnumNames.join("\r\n") + "};\r\n\r\n";
    }

    var dlg = getEnumReaderDlg("#"+treeId);
    $("#selectReaderEnums", dlg).val(enumNames);

    $("#a8enumReaderForm input[name=a8EnumReaderBean\\.enumIds]", dlg).val("导出所有公用枚举:{" + isAllPublic + "};" +
        "公用枚举类别ID:{" + publicEnumTypeIds + "};" +
        "公用枚举ID:{" + publicEnumIds + "};" +
        "单位ID:{" + unitIds + "};" +
        "单位枚举类别ID:{" + unitEnumTypeIds + "};" +
        "单位枚举ID:{" + unitEnumIds + "};");
}

function exchangeTreeNodeToEnumName(treeNode) {
    var result = treeNode.name;
    var tmpNode = treeNode.getParentNode();
    while (tmpNode) {
        result = tmpNode.name + "＞" + result;
        tmpNode = tmpNode.getParentNode();
    }
    return result;
}

function parentNodeContainId(node, id) {
    if (!node) {
        return false;
    }
    if (node.id == id) {
        return true;
    }
    var tmpNode = node.getParentNode();
    while (tmpNode) {
        if (tmpNode.id == id) {
            return true;
        }
        tmpNode = tmpNode.getParentNode();
    }
    return false;
}

function arrayContain(array, node) {
    for (var i=0; i<array.length; i++) {
        if (parentNodeContainId(node, array[i])) {
            return true;
        }
    }
    return false;
}

function submitA8EnumReaderForm(obj) {
    var dlg = getEnumReaderDlg(obj);
    if (hasSpecialChar($("input[name='bean.dis_name']", dlg).val()) ||
        hasSpecialChar($("input[name='a8EnumReaderBean.a8url']", dlg).val()) ||
        hasSpecialChar($("input[name='a8EnumReaderBean.userName']", dlg).val()) ||
        hasSpecialChar($("input[name='a8EnumReaderBean.password']", dlg).val()))
    {
        alertMsg.error(DWZ.msg("alertHasSpecialChar"));
        return false;
    }
    $("#a8enumReaderForm", dlg).submit();
}

function getEnumReaderDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}