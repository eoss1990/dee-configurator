var setting = {
			view: {
				selectedMulti: false
			},
			edit: {
				enable: true,
				editNameSelectAll: true
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeDrag: beforeDrag,
				beforeEditName: beforeEditName,
				beforeRemove: beforeRemove,
				beforeRename: beforeRename,
				beforeAddNod: bfAddNod
			}
};

var setting1 = {
		check: {
			enable: true,
			chkboxType: { "Y": "p", "N": "s" }
		},
		async: {
			enable: true,
			url: "/demo/flowType!getTreeAllTableForJson.do",//getUrl,
			autoParam: ["id","url"],
			dataFilter:ajaxDataFilter
		},
		data: {
			simpleData: {
				enable: false,
				idKey: "id",
				pIdKey: "pId",
				rootPId: 0
			}
		},
		callback: {
			beforeExpand: beforeExpand,
			onAsyncSuccess: onAsyncSuccess,
			onAsyncError: onAsyncError
		}
};


$(document).ready(function(){
	$.fn.zTree.init($("#flowTypes"), setting, zNodes);
	$.fn.zTree.init($("#asyncDemo"), setting1, zNodes1);
});

function beforeDrag(treeId, treeNode) {
	return false;
}
var newCount = 1;
function bfAddNod(treeId, treeNode){
	var zTree = $.fn.zTree.getZTreeObj("flowTypes");
	zTree.addNodes(treeNode, {id:(100 + newCount), pId:treeNode.id, name:"新节点" + (newCount++)});
	var ret = SaveFlowTypeName("新节点"+ (newCount - 1),treeNode.id);
	if(ret != "0"){
		alertMsg.error('新增节点失败！');
		return true;
	}
	alertMsg.correct('新增节点成功！');
	return false;
}
function beforeEditName(treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("flowTypes");
	zTree.selectNode(treeNode);
	return confirm("进入节点 -- " + treeNode.name + " 的编辑状态吗？");
}
function beforeRemove(treeId, treeNode) {
	showLog("[ "+getTime()+" 删除前触发事件 ]" + treeNode.name);
	var zTree = $.fn.zTree.getZTreeObj("flowTypes");
	zTree.selectNode(treeNode);
	return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
}
function beforeRename(treeId, treeNode, newName) {
	if (newName.length == 0) {
		alert("节点名称不能为空.");
		var zTree = $.fn.zTree.getZTreeObj("flowTypes");
		setTimeout(function(){zTree.editName(treeNode)}, 10);
		return false;
	}
	var ret = reFlowTypeName(treeNode.id,newName);
	if(ret != "0"){
		alertMsg.error('修改任务类别名称失败！');
		return false;
	}
	alertMsg.correct('修改任务类别名称成功！');
	return true;
}

function reFlowTypeName(treeId,nodeName) {
	var ret = "1";
	$.ajax({
		async: false,
		type:"post",
		dataType: "json",
		data:eval("({'flowTypeId':'"+treeId+"','actName':'"+nodeName+"'})"),
		url: "/demo/flowType!reFlowTypeName.do",
		success: function(result) {
			ret = result.retFlag;
		}
	});
	return ret;
}		
function SaveFlowTypeName(nodeName,nodePId) {
	var ret = "1";
	alert(nodePId);
	$.ajax({
		async: false,
		type:"post",
		dataType: "json",
		data:eval("({'pId':'"+nodePId+"','actName':'"+nodeName+"'})"),
		url: "/demo/flowType!saveNewFlowType.do",
		success: function(result) {
			ret = result.retFlag;
		}
	});
	return ret;
}		
function beforeExpand(treeId, treeNode) {
	if (!treeNode.isAjaxing) {
		return true;
	} else {
		alert("zTree 正在下载数据中，请稍后展开节点。。。");
		return false;
	}
}
function ajaxDataFilter(treeId, parentNode, responseData){
	return responseData.jsonData;
}
function onAsyncSuccess(event, treeId, treeNode, msg) {
	if (!msg || msg.length == 0) {
		return;
	}
	var zTree = $.fn.zTree.getZTreeObj("asyncDemo");
	treeNode.icon = "";
    zTree.updateNode(treeNode); 
//    zTree.expandNode(treeNode.children[0].children[0],true);
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	var zTree = $.fn.zTree.getZTreeObj("asyncDemo");
	alert("异步获取数据出现异常。");
	treeNode.icon = "";
	zTree.updateNode(treeNode);
}



