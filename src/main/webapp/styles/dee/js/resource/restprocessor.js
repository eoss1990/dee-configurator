var rest_setting = {
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
        onCheck: rest_oncheck
    }
};

$(function () {
    // 初始化服务和方法
    initServiceAndFunction();

    //接口类型
	$("#selInterface").change(function() {
		var selVal = $("#selInterface").val();
		if(selVal == "34" || selVal == "8"){
            var url = "";
			if(selVal == "34"){
	            url = "/dee/a8enumreader!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1001";				
			}
			else{
	            url = "/dee/a8source!view.do?flowId="+$("#flowId").val()+"&sort="+$("#sort").val()+"&resourceType=1002";				
			}
            var dlgId = "resourceDlg";
            var title = "适配器配置";
            var options = {
                width:"800px",
                height:"600px"
            };
            $.pdialog.open(url, dlgId, title, options);
		}
	});
    
    // 服务下拉框Change事件
    $("#rest_service").change(function () {
//    	debugger;
        if (!$(this).val()) {       // 没有选择服务，进行清空操作
            // 清空方法列表
            $("#rest_function").html("<option value=\"\">请选择</option>");
            // 清空参数
            $("#rest_param").html("");
        } else {                    // 已选择服务
            // 设置服务名
            //$("#restServiceName").val($("option:selected", this).text());
            changeRestService($(this).val());
        }
    });

    // 方法下拉框Change事件
    $("#rest_function").change(function () {
        if (!$(this).val()) {        // 没有选择方法，进行清空操作
            // 清空参数
            $("#rest_param").html("");
        } else {                     // 已选择方法
            $.ajax({
                async: false,
                type: "post",
                dataType: "json",
                data: 'functionId=' + $(this).val() + "&serviceId=" + $("#rest_service").val(),
                url: "/dee/restprocessor!refreshByFunction.do",
                success: function (result) {
                    var paramHtml = "";
                    if (result && result.jsonData) {
                        if (result.retFlag == 'true') {
                            $("#mergeToDocumentP").show();//合并到文档展示
                        } else {
                            $("#mergeToDocumentP").hide();//隐藏
                        }
                        var jsonData = result.jsonData;
                        var index;
                        var classType = "";
                        for (index = 0; index < jsonData.length; index++) {
                            if(jsonData[index].isRequired == "1"){
                                classType = "textInput";
                            }
                            else {
                                classType = "required textInput";
                            }
                            paramHtml += "<tr class=\"unitBox\">" +
                                "<td>" +
                                "<p>" + dropDeeCast(jsonData[index].paramName) + "</p>" +
                                "</td>" +
                                "<td>" +
                                "<input type='text' name='restParams[" + index + "].showValue' value='' class='"+classType+"' " + createDeeHref(jsonData[index].paramName) + " />" +
                                "<input type='hidden' value='" + jsonData[index].paramValue + "' name='restParams[" + index + "].paramValue' />" +
                                "<input type='hidden' value='" + jsonData[index].paramName + "' name='restParams[" + index + "].paramName' />" +
                                "<input type='hidden' value='" + jsonData[index].paramType + "' name='restParams[" + index + "].paramType' />" +
                                "<input type='hidden' value='" + jsonData[index].isRequired + "' name='restParams[" + index + "].isRequired' />" +
                                "</td>" +
                                "</tr>";
                        }
                        var responseTypeHtml = "";
                        var responseTypes = result.mapingData.split("|");
                        for (index = 0; index < responseTypes.length; index++) {
                            responseTypeHtml += "<option value=\"" + responseTypes[index] + "\">" + responseTypes[index] + "</option>";
                        }
                        $("#rest_responseType").html(responseTypeHtml);
                    }
                    $("#rest_param").html(paramHtml);
                    //debugger;
                    //判断是否显示外键映射表
                    if (result.isShowTab != undefined && Boolean(result.isShowTab)) {
                        $("#pA8flow").show();//显示
                    } else {
                        $("#pA8flow").hide();//隐藏
                    }
                }, error: DWZ.ajaxError
            });
        }
    });
});

/**
 * 初始化服务和方法列表
 */
function initServiceAndFunction() {
    if ($("#rest_service").val()) {
        changeRestService($("#rest_service").val());
        $("#rest_function").val($("#rest_function").attr("functionId"));
    }
}

/**
 * 服务列表改变事件
 * @param serviceId 服务ID
 */
function changeRestService(serviceId) {
    $.ajax({
        async: false,
        type: "post",
        dataType: "json",
        data: 'serviceId=' + serviceId,
        url: "/dee/restprocessor!listFunctionsByServiceId.do",
        success: function (result) {
            var functionHtml = "<option value=\"\">请选择</option>";
            if (result && result.jsonData) {
                var jsonData = result.jsonData;
                var resourceType = $("#selInterface").val();//$("#resourceType").val();
                for (var index = 0; index < jsonData.length; index++) {
                    if (resourceType && resourceType == "36") {
                        if (!jsonData[index].cfgType || jsonData[index].cfgType == "reader") {
                            functionHtml += "<option value=\"" + jsonData[index].functionId + "\">" + jsonData[index].functionName + "</option>";
                        }
                    }
                    if (resourceType && resourceType == "37") {
                        if (!jsonData[index].cfgType || jsonData[index].cfgType == "writer") {
                            functionHtml += "<option value=\"" + jsonData[index].functionId + "\">" + jsonData[index].functionName + "</option>";
                        }
                    }
                }
            }
            $("#rest_function").html(functionHtml);
        }, error: DWZ.ajaxError
    });
}

/**
 * “合并到文档”点击事件
 * @param obj
 */
function mergeToDocumentClick(obj) {
    if ($(obj).attr("checked")) {
        $(obj).val(true);
    } else {
        $(obj).val(false);
    }
}

/**
 * 表单提交事件
 * @param obj
 */
function submitRestProcessorForm(obj) {
    var dlg = getRestProcessorDlg(obj);
    if (hasSpecialChar($("input[name='bean.dis_name']", dlg).val()) ||
        hasSpecialChar($("input[name='restProcessorBean.address']", dlg).val()) ||
        hasSpecialChar($("input[name='restProcessorBean.adminUserName']", dlg).val()) ||
        hasSpecialChar($("input[name='restProcessorBean.adminPassword']", dlg).val()) ||
        hasSpecialChar($("input[name='restProcessorBean.responseName']", dlg).val())) {
        alertMsg.error(DWZ.msg("alertHasSpecialChar"));
        return false;
    }
    if (!validRest(dlg)) {
        return false;
    }
    $("#restProcessorForm", dlg).submit();
}

function getRestProcessorDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}

function dropDeeCast(str) {
    var start = str.indexOf("[[");
    var end = str.indexOf("]]");
    if (start >= 0 && end > start) {
        return str.substring(end + 2).trim();
    }
    return str;
}

function createDeeHref(str) {
    var start = str.indexOf("[[dee_select_");
    var end = str.indexOf("]]");
    if (start >= 0 && end > start) {
        return " readonly onclick='javascript:" + str.substring(2, end).trim() + "(this);' ";
    }
    return "";
}

function rest_oncheck(event, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    var obj = zTree.dee_trigger_obj;
    var currentTr = $(obj).closest("tr");
    var paramValueName = "input[name='" + $(obj).attr("name").split(".")[0] + ".paramValue" + "']";
    var paramValueObj = $(paramValueName, currentTr);

    var paramNameName = "input[name='" + $(obj).attr("name").split(".")[0] + ".paramName" + "']";
    var paramNameObj = $(paramNameName, currentTr);

    if (paramNameObj.val().indexOf("[[dee_select_departmentIdAndOrgAccountId") >= 0) {
        var getOrgAccountNode = function () {
            if (!treeNode.checked) {
                return null;
            }
            var tmpTreeNode = treeNode;
            while (tmpTreeNode) {
                if (tmpTreeNode.getParentNode().getParentNode()) {
                    tmpTreeNode = tmpTreeNode.getParentNode();
                } else {
                    return tmpTreeNode;
                }
            }
            return null;
        };
        var tmpNames = $("input[name$='].paramName']", currentTr.parent());
        if (tmpNames.length > 0) {
            var selectOrgAccountId;
            for (var index = 0; index < tmpNames.length; index++) {
                if ($(tmpNames[index]).val().indexOf("[[dee_select_orgAccountId_readOnly]]") >= 0) {
                    selectOrgAccountId = $(tmpNames[index]).closest("td");
                    break;
                }
            }
            if (selectOrgAccountId) {
                var orgAccountNode = getOrgAccountNode();
                if (orgAccountNode) {
                    $("input[name$='].paramValue']", selectOrgAccountId).val(orgAccountNode.id);
                    $("input[name$='].showValue']", selectOrgAccountId).val(orgAccountNode.name);
                } else {
                    $("input[name$='].paramValue']", selectOrgAccountId).val("");
                    $("input[name$='].showValue']", selectOrgAccountId).val("");
                }
            }
        }
    }

    var selectNodes = zTree.getCheckedNodes();
    if (selectNodes.length < 1) {
        if (obj) {
            // 设置名称
            $(obj).val("");
            // 设置ID
            paramValueObj.val("");
        }
        return;
    }

    for (var i = 0; i < selectNodes.length; i++) {
        zTree.checkNode(selectNodes[i], false, false);
        zTree.checkNode(treeNode, true, false);
        if (obj) {
            // 设置名称
            $(obj).val(treeNode.name);
            // 设置ID
            paramValueObj.val(treeNode.id);
        }
    }
}

function onRestMDown(event) {
    var dlg = getRestProcessorDlg(event.target);
    if (!(event.target.id == "restSelect" || $(event.target).parents("#restSelect").length > 0)) {
        if (dlg && dlg.data && dlg.data("id")) {
            hideRestMenu(dlg);
        }
    }
}

function hideRestMenu(dlg) {
    if ($("#restSelect", dlg).length > 0) {
        var dialogId = dlg.data("id");
        $("#restSelect", dlg).fadeOut("fast");
        if (dialogId == "resourceDlg") {
            $("#restDsTree" + dialogId).html("");
        } else {
            $("#restDsTree" + dialogId).html("");
        }
        $("body").unbind("mousedown", onRestMDown);
    }
}

function dee_select_orgAccountId_readOnly(obj) {
}

function dee_select_orgAccountId(obj) {
    dee_select_id(obj, "/dee/restprocessor!deeSelectOrgAccountId.do");
}

function dee_select_departmentId(obj) {
    dee_select_id(obj, "/dee/restprocessor!deeSelectDepartmentId.do");
}
function dee_select_departmentIdAndOrgAccountId(obj) {
    dee_select_id(obj, "/dee/restprocessor!deeSelectDepartmentId.do");
}

function dee_select_departmentIdAndOrgAccountIdNotOrgAccount(obj) {
    dee_select_id(obj, "/dee/restprocessor!deeSelectDepartmentIdNotOrgAccount.do");
}

function dee_select_orgLevelId(obj) {
    dee_select_id(obj, "/dee/restprocessor!deeSelectOrgLevelId.do");
}

function dee_select_postId(obj) {
    dee_select_id(obj, "/dee/restprocessor!deeSelectPostId.do");
}

//function dee_select_primaryKey(obj) {
//    dee_select_id(obj, "/dee/restprocessor!deeSelectPrimaryKey.do");
//}

function dee_select_id(obj, url) {
    var paramValueName = $(obj).attr("name").split(".")[0] + ".paramValue";
    var tr = $(obj).closest("tr");
    var paramValueObj = $("input[name='" + paramValueName + "']", tr);

    var $this = $(obj);
    var offset = $this.offset();

    $.ajax({
        async: false,
        type: "post",
        dataType: "json",
        data: {
            id:paramValueObj.val(),
            address:$("#rest_address").val(),
            adminUserName:$("#rest_adminUserName").val(),
            adminPassword:$("#rest_adminPassword").val()
        },
        url: url,
        success: function (result) {
            if (result.statusCode == 300) {
                alertMsg.error(result.message);
                return;
            }
            $("#restSelect").remove();
            $this.parent().append($("#restSelectCopy").clone(true));
            $("#restSelectCopy", $this.parent()).attr("id", "restSelect");
            $("#restDsTreeCopy", $this.parent()).attr("id", "restDsTree");
            var restSelect = $("#restSelect");
            restSelect.show();
            restSelect.offset({top: restSelect.offset().top + 25});

            var zTreeObj = $.fn.zTree.init($("#restDsTree"), rest_setting, result.jsonData);
            zTreeObj.dee_trigger_obj = obj;
            var currentId = paramValueObj.val();
            if (currentId) {
                var treeNode = zTreeObj.getNodeByParam("id", currentId);
                if (treeNode) {
                    zTreeObj.checkNode(treeNode, true);
                    var parentTreeNode = treeNode.getParentNode();
                    while (parentTreeNode) {
                        zTreeObj.expandNode(parentTreeNode, true);
                        parentTreeNode = parentTreeNode.getParentNode();
                    }
                }
            }
        }, error: DWZ.ajaxError
    });

    $("body").bind("mousedown", onRestMDown);
}

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
    var dialogId = getA8CommonWsResourceId(currentRow);
    var obj=document.getElementById("restBig"+dialogId);
    var event = window.event || arguments.callee.caller.arguments[0];
    var parObj=obj;
    var top=obj.offsetTop;
    while(parObj = parObj.offsetParent){
        top+=parObj.offsetTop;

    }
    var cy=event.clientY-top+document.documentElement.scrollTop -113;

    //清空查询条件
    $("#searchText"+dialogId).val("");

    gDescTable = $("input[type='text']", currentRow);
    gForeignKey = $("textarea", currentRow);
    var offset = gForeignKey.offset();

    $("#tableSelect"+dialogId).css({left:"237px", top:cy+"px"}).slideDown("fast");

    ajaxTodo("/dee/a8commonws!selectAllA8Dbs.do", function(data) {
        var jsonData = data.jsonData;
        var options = [];
        options.push("<option value=''>请选择...</option>");
        for (var i in jsonData) {
            options.push("<option value='"+jsonData[i].resource_id+"'>"+jsonData[i].dis_name+"</option>");
        }
        $("#dbList"+dialogId).html(options.join(""));
    });
    $("#a8restProDiv"+dialogId).bind("mousedown", onA8CommonWsBodyDown);
}

function onA8CommonWsBodyDown(event) {
    var dialogId = getA8CommonWsResourceId(event.target);
    if (!(event.target.id == "tableSelect"+dialogId || $(event.target).parents("#tableSelect"+dialogId).length > 0)) {
        hideA8CommonWsMenu(dialogId);
    }
}

function hideA8CommonWsMenu(dialogId) {
    $("#tableSelect"+dialogId).fadeOut("fast");
    $("#dsTree"+dialogId).html("");
    $("#a8restProDiv"+dialogId).unbind("mousedown", onA8CommonWsBodyDown);
}

function getA8CommonWsResourceId(obj) {
    return $(obj).parents("form").find("input[name='resourceId']").val();
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
//单选
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

/*
function dee_select_id(obj, url) {
    var $this = $(obj);
    var offset = $this.offset();

    $.ajax({
        async: false,
        type: "post",
        dataType: "json",
        data: {
            id:$this.val(),
            address:$("#rest_address").val(),
            adminUserName:$("#rest_adminUserName").val(),
            adminPassword:$("#rest_adminPassword").val()
        },
        url: url,
        success: function (result) {
            if (result.statusCode == 300) {
                alertMsg.error(result.message);
                return;
            }
            $("#restSelect").css({left:"15px", top:offset.top - 10 + "px"}).slideDown("fast");
            var zTreeObj = $.fn.zTree.init($("#dsTree"), rest_setting, result.jsonData);
            zTreeObj.dee_trigger_obj = obj;
            var currentId = $(obj).val();
            if (currentId) {
                var treeNode = zTreeObj.getNodeByParam("id", currentId);
                if (treeNode) {
                    zTreeObj.checkNode(treeNode, true);
                    var parentTreeNode = treeNode.getParentNode();
                    while (parentTreeNode) {
                        zTreeObj.expandNode(parentTreeNode, true);
                        parentTreeNode = parentTreeNode.getParentNode();
                    }
                }
            }
        }, error: DWZ.ajaxError
    });

    $("body").bind("mousedown", onRestMDown);
}*/
