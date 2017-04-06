// 重命名
function beforeRename(treeId, treeNode, newName) {
	if (newName.length == 0) {
		setTimeout(function() { alertMsg.error("名称不能为空"); }, 50);
		return false;
	}
	
	var ret = false;
	
	$.ajax({
        async: false,
        type:"post",
        dataType: "json",
        data:'flowType.id=' + treeNode.id + "&flowType.name=" + newName + "&flowType.sort=1",
        url: "/dee/flowType!saveFlowType.do",
        success: function(result) {
            if (result.statusCode == 200) {
            	ret = true;
            } else if (result.statusCode == 300) {
            	setTimeout(function() { alertMsg.error(result.message); }, 50);
            } else {
                setTimeout(function() { alertMsg.error("其他错误"); }, 50);
            }
        },
        error:function() {
        	setTimeout(function() { alertMsg.error("其他错误"); }, 50);
        }
    });
	return ret;
}

// 刷新分类任务
function refreshDeeList(event, treeId, treeNode) {
	if(treeNode.id=='0')
		reloadZtree();
	var url = "/dee/flowManager!deelist.do?flowTypeId=" + treeNode.id;
	navTab.openTab("deeList", url, { title:"任务中心", fresh:true });
};

// 重新载入任务分类
function reloadZtree() {
	var flowIndexSetting = {
		view: {
	        addHoverDom: addHoverDom,
	        removeHoverDom: removeHoverDom,
	        selectedMulti: false
	    },
	    edit: {
	        enable: true,
	        editNameSelectAll: true,
            drag: {
                isCopy:false,
                isMove:false
            }
	    },
	    callback: {
	    	onClick: refreshDeeList,
	    	beforeRename: beforeRename
	    },
	    data: {
	        simpleData: {
	            enable: true
	        }
	    }
	};
	
	$.ajax({
        async: false,
        type:"post",
        dataType: "json",
        url: "/dee/flowType!getAllFlowType.do",
        success: function(result) {
        	var zNodes = [];
        	var list = result.jsonData;
        	for (var i in list) {
        		zNodes[i] = {
        			id: list[i].FLOW_TYPE_ID,
        			pId: list[i].PARENT_ID,
        			name: list[i].FLOW_TYPE_NAME,
        			nocheck: "true",
        			open: (list[i].PARENT_ID == -1)?true:false
        		};
        	}
            $.fn.zTree.init($("#flowList"), flowIndexSetting, zNodes);
        },
        error:function() {
            alert("其他错误");
        }
    });
}

// 新增事件
var newCount = 1;
function addFlowType(treeNode) {
	var flowTypename = "new node" + (newCount++);
	$.ajax({
        async: false,
        type:"post",
        dataType: "json",
        data:'flowType.parentId=' + treeNode.id + "&flowType.name=" + flowTypename + "&flowType.sort=1",
        url: "/dee/flowType!saveFlowType.do",
        success: function(result) {
            if (result.statusCode == 200) {
            	var zTree = $.fn.zTree.getZTreeObj("flowList");
            	var newNode = zTree.addNodes(treeNode, { id:result.uuid, pId:treeNode.id, name:flowTypename });
            	zTree.editName(newNode[0]);
            } else if (result.statusCode == 300) {
                alertMsg.error(result.message);
            } else {
                alertMsg.error("其他错误");
            }
        },
        error:function() {
            alert("其他错误");
        }
    });
}

// 编辑事件
function editFlowType(treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("flowList");
	zTree.editName(treeNode);
}

// 删除事件
function deleteFlowType(treeNode) {
    if (treeNode.pId == null) {
    	alertMsg.info("根节点不能删除");
    	return;
    }
    
    alertMsg.confirm("确定删除分类 " + treeNode.name + " ？", {
    	okCall: function() {
    		$.ajax({
    	        async: false,
    	        type:"post",
    	        dataType: "json",
    	        data:'flowType.id=' + treeNode.id,
    	        url: "/dee/flowType!deleteFlowType.do",
    	        success: function(result) {
    	            if (result.statusCode == 200) {
    	                var zTree = $.fn.zTree.getZTreeObj("flowList");
    	                zTree.removeNode(treeNode);
    	                alertMsg.correct(result.message);
    	            } else if (result.statusCode == 300) {
    	                alertMsg.error(result.message);
    	            } else {
    	                alertMsg.error("其他错误");
    	            }
    	        },
    	        error:function() {
    	            alert("其他错误");
    	        }
    	    });
        }
    });
  	
}

function addHoverDom(treeId, treeNode) {
    var btn = $("#" + treeNode.tId + "_add");
    btn.unbind().bind("click", function() { addFlowType(treeNode); });
    btn = $("#" + treeNode.tId + "_remove");
    btn.unbind().bind("click", function() { deleteFlowType(treeNode); });
    btn = $("#" + treeNode.tId + "_edit");
    btn.unbind().bind("click", function() { editFlowType(treeNode); });
};

function removeHoverDom(treeId, treeNode) {
	$("#" + treeNode.tId + "_add").unbind().remove();
	$("#" + treeNode.tId + "_remove").unbind().remove();
	$("#" + treeNode.tId + "_edit").unbind().remove();
};

$(document).ready(function(){
	reloadZtree();
});