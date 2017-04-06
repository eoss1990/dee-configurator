Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
};

function zTreeOnCheck(event, treeId, treeNode) {
	if (!treeNode.isPrent) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		treeObj.checkNode(treeNode.getParentNode(), true, false);
	}
}

function beforeCheck(treeId, treeNode) {
    var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
    if (treeObj.getCheckedNodes().contains(treeNode) || 
        treeObj.getCheckedNodes().contains(treeNode.getParentNode())) {
        return;
    }
    var nodes = treeObj.getCheckedNodes(true);
    for (var i=0, l=nodes.length; i<l; i++) {
        treeObj.checkNode(nodes[i], false, true);
    }
}

function saveTreeState() {
    var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
    var nodes = treeObj.getNodes();
    
    var ret = "";
    for (var i=0; i<nodes.length; i++) {
        ret += appendJson(nodes[i]);
    }
    ret = "[" + ret + "]";
    $.ajax({
        async: false,
        type:"post",
        dataType: "json",
        data:'treeValue='+ret,
        url: "/demo/depts!saveDepts.do",
        success: function(result) {
        	//window.location.reload();
        }
    });
}

function moveToLeaf() {
    var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
    var nodes = treeObj.getCheckedNodes(true);
    var selectedLeafContent = "";
    for (var i=0; i<nodes.length; i++) {
        if (!nodes[i].isParent) {
            selectedLeafContent += nodes[i].name;
            selectedLeafContent += "<br/>";
        }
    }
    $("#selectedLeaf").html(selectedLeafContent);
}

function appendJson(node) {
    var ret = "";
    if (node) {
        ret = "{";
        ret += "'deptId':'" + node.id + "',";
        ret += "'deptPid':'" + (node.pId?node.pId:"0") + "',";
        ret += "'deptName':'" + node.name + "',";
        ret += "'open':'" + (node.open?1:-1) + "',";
        ret += "'nocheck':'" + (node.nocheck?1:-1) + "',";
        ret += "'checked':'" + (node.checked?1:-1) + "'},\n";
    }
    if (node.children) {
        for (var i=0; i<node.children.length; i++) {
            ret += appendJson(node.children[i]);
        }
    }
    return ret ? ret : "";
}