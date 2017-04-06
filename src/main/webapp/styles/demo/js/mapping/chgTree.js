var setting = {
		check: {
			enable: true,
			chkStyle: "radio",
			chkboxType: { "Y": "p", "N": "s" }
		},
		async: {
			enable: true,
			url: "/demo/flowType!getTreeAllTableForJson.do",//getUrl,
			autoParam: ["id","mId"],
			dataFilter:ajaxDataFilter
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
			onAsyncError: onAsyncError
		}
};
var closeFlag=1;

$(document).ready(function(){
	closeFlag=1;
	$.fn.zTree.init($("#asyncDemo"), setting, zNodes);
	$("#sglBtn").click(function (){
		closeFlag=0;
	});
});

function beforeExpand(treeId, treeNode) {
	if (!treeNode.isAjaxing) {
		return true;
	} else {
		alertMsg.info('zTree 正在下载数据中，请稍后展开节点。。。');
		return false;
	}
}
function ajaxDataFilter(treeId, parentNode, responseData){
	alert(responseData.jsonData);
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
	alertMsg.error('异步获取数据出现异常。');
	treeNode.icon = "";
	zTree.updateNode(treeNode);
}

function checkSel(){
	var zTree = $.fn.zTree.getZTreeObj("asyncDemo"),
	nodes = zTree.getCheckedNodes(true);
	if(nodes.length < 1)
	  alertMsg.warn('请选择节点');
	
}


