//ztree节点的模糊查询
function searchZtree(treeId, searchText){
	 
    var hNodes = new Array();
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    if(zTree == null) return;
    if (searchText != "") {
        hNodes = zTree.getNodesByParamFuzzy("name", searchText, null);
    }
    else{
    	//还原初始状态
    	//显示所有节点
    	var nodes = zTree.getNodesByParam("isHidden", true);
    	zTree.showNodes(nodes);
    	//全部收缩
    	zTree.expandAll(false);
    	//显示对应节点下的checkbox
        nodes = zTree.getNodes();
        var len = nodes.length;
        for(var i=0;i<len;i++){
        	setSonCheckboxShw(treeId,nodes[i],false);
        }
    	return;
    }
    //全部隐藏
    setAllNodesHide(treeId);
    //显示模糊查询的节点
    //debugger;
    if (hNodes != null) {
        for (var i = 0; i < hNodes.length; i++) {
        	//过滤字段
        	if(hNodes[i].nType == "4"){
        		//表单
        		if(hNodes[i].level > 4){
        			continue;
        		}
        	}
        	else{
        		if(hNodes[i].level > 2){
        			continue;
        		}
        	}
            //显示模糊查询节点的父节点
        	setParentShw(treeId,hNodes[i]);
        	//显示查询节点的所有子节点
        	var nodes = zTree.getNodesByParam("isHidden", true,hNodes[i]);
        	zTree.showNodes(nodes);        	
        	//模糊查询节点子节点是否显示checkbox
        	setSonCheckboxShw(treeId, hNodes[i],true);
        }
    }
    //全部展开
   // zTree.expandAll(true);
}
/**
 * 隐藏所有节点
 */
function setAllNodesHide(treeId){
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    var nodes = zTree.getNodes();
    var len = nodes.length;
	zTree.hideNodes(nodes);
    for(var i=0;i<len;i++){
    	setNodesHide(treeId,nodes[i]);
    }
}
/**
 * 递归隐藏所有子节点
 */
function setNodesHide(treeId,node){
    if (node != null) {
        var zTree = $.fn.zTree.getZTreeObj(treeId);
        var nodes = node.children;
        if(nodes != null){
        	zTree.hideNodes(nodes);
            var len = nodes.length;
            for(var i=0;i<len;i++){
            	setNodesHide(treeId,nodes[i]);
            }
        }
    } 
}
/**
 * 递归显示节点的所有父节点....直到根节点
 */
function setParentShw(treeId, node){
    if (node != null) {
        var zTree = $.fn.zTree.getZTreeObj(treeId);
        zTree.showNode(node);
        isShowCheckBox(treeId, node,true);
        setParentShw(treeId, node.getParentNode());
    } 
}
/**
 * 递归显示节点的子节点的checkbox
 */
function setSonCheckboxShw(treeId, node,isExpand){
    if (node != null) {
        var zTree = $.fn.zTree.getZTreeObj(treeId);
        isShowCheckBox(treeId, node,isExpand);
        var nodes = node.children;
        if(nodes != null){
            var len = nodes.length;
            for(var i=0;i<len;i++){
                setSonCheckboxShw(treeId,nodes[i],isExpand);
            }
        } 
    }
}
/**
 * 判断是否显示节点的checkbox，并且展开与否
 */
function isShowCheckBox(treeId, chkNode,isExpand){
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    if(chkNode.nType == "4"){
    	//表单模块
        //0,1,2,3级节点去掉复选框
        if(chkNode.level <4){
        	if(!chkNode.nocheck){
        		chkNode.nocheck = true;
                zTree.updateNode(chkNode);
        	}
        	if(isExpand){
            	//只展开0,1,2,3级节点
                zTree.expandNode(chkNode, true, false, true);
        	}
       }
        else{
        	if(chkNode.nocheck){
        		chkNode.nocheck = false;
                zTree.updateNode(chkNode);
        	}
        }
    }
    else{
    	//数据库，NC，组织机构等
        //0,1级节点去掉复选框
        if(chkNode.level <2){
        	if(!chkNode.nocheck){
        		chkNode.nocheck = true;
                zTree.updateNode(chkNode);
        	}
        	if(isExpand){
            	//只展开0,1级节点
                zTree.expandNode(chkNode, true, false, true);
        	}
        }
        else{
        	if(chkNode.nocheck){
        		chkNode.nocheck = false;
                zTree.updateNode(chkNode);
        	}
        }
    }
}


function functionZTreeSearch(treeId, searchText){
//	debugger;
    var hNodes = new Array();
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    if (searchText != "") {
        hNodes = zTree.getNodesByParamFuzzy("name", searchText, null);
    }
    else{
    	//还原初始状态
    	//显示所有节点
    	var nodes = zTree.getNodesByParam("isHidden", true);
    	zTree.showNodes(nodes);
    	//全部收缩
    	zTree.expandAll(false);
    	//显示对应节点下的checkbox
        //nodes = zTree.getNodes();
       /* var len = nodes.length;
        for(var i=0;i<len;i++){
        	setSonCheckboxShw(treeId,nodes[i],false);
        }*/
    	return;
    }
    //全部隐藏
    setAllNodesHide(treeId);
    //显示模糊查询的节点
    //debugger;
    if (hNodes != null) {
        for (var i = 0; i < hNodes.length; i++) {
            //显示模糊查询节点的父节点
        	setParentShw(treeId,hNodes[i]);
        	//显示查询节点的所有子节点
        	var nodes = zTree.getNodesByParam("isHidden", true,hNodes[i]);
        	zTree.showNodes(nodes);        	
        	//模糊查询节点子节点是否显示checkbox
        	//setSonCheckboxShw(treeId, hNodes[i],true);
        }
    }
    //全部展开
    zTree.expandAll(true);
}