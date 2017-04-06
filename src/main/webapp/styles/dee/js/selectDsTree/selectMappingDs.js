var setting = {
		check: {
			enable: true,
			chkStyle: "radio",
			chkboxType: { "Y": "ps", "N": "ps" }
		},
		async: {
			enable: true,
			url: "/dee/selectDsTree!selectSonDs.do",//getUrl,
			autoParam: ["id","nId","nType","nForm"],
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
var zNodes =[{id:0, pId:-1, name:"空节点"}];
var selObj;

$(function() {
    var selectMappingDsDlg = $(">.dialog:last-child", "body");
    selObj = selectMappingDsDlg.data("selObj");
	init();
    $('#mappingDsType').change(function(){
    	chgSelDs($(this).val());
    });
    $('#btnOk').click(function(){
    	setSelectOneMap();
    });
    //
});
//模糊查询回车事件
$("#selectMappingDsZtree").keydown(function(event) { 
	if (event.keyCode == 13) { 
		searchZtree("mappingTree",$("#searchMappingDs").val());
	} 
}); 

function init(){
	chgSelDs("2");
}
function beforeExpand(treeId, treeNode) {
	if (!treeNode.isAjaxing) {
		return true;
	} else {
		alertMsg.info('正在下载数据中，请稍后展开节点。。。');
		return false;
	}
}
function ajaxDataFilter(treeId, parentNode, responseData){
//	alert(responseData.jsonData);
	return responseData.jsonData;
}
function onAsyncSuccess(event, treeId, treeNode, msg) {
	if (!msg || msg.length == 0) {
		return;
	}
	var zTree = $.fn.zTree.getZTreeObj("mappingTree");
	treeNode.icon = "";
    zTree.updateNode(treeNode); 
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	var zTree = $.fn.zTree.getZTreeObj("mappingTree");
	alertMsg.error('异步获取数据出现异常。');
	treeNode.icon = "";
	zTree.updateNode(treeNode);
}

function checkSel(){
	var zTree = $.fn.zTree.getZTreeObj("mappingTree"),
	nodes = zTree.getCheckedNodes(true);
	if(nodes.length < 1)
	  alertMsg.warn('请选择节点');
}
function chgSelDs(rsId){
	ajaxTodo("/dee/selectDsTree!selectOneDs.do?dsId="+rsId,mappingCallBack);
}
var driver;
function mappingCallBack(json){
	if (json.statusCode == DWZ.statusCode.ok){
		zNodes = json.jsonData;
		if(zNodes[0].id.indexOf(":") != -1 ){
			var tmp = zNodes[0].id.split(':');
			var start = "";
			var id = "";
			zNodes[0].id = tmp[0];
			driver = tmp[1] + zNodes[0].name;
			id = tmp[0];
			var modelName = [];
			var model1 = [];
			var model2 = [];
			if(driver.indexOf("IBM") != -1){
				for(var j = 0 ; j < zNodes.length; j++){
					if( zNodes[j].pId != null && zNodes[j].pId == id){
						modelName.push(zNodes[j].name);
						modelName.push(zNodes[j].id);
					}
				}
				if(modelName.length > 1){
					model1.push(modelName[0]);
					model1.push(modelName[1]);
					model2.push(modelName[2]);
					model2.push(modelName[3]);
				}else{
					model1.push(modelName[0]);
					model2.push(modelName[1]);
				}
				for(var b = 0 ; b < zNodes.length; b++){
					if( zNodes[b].pId !=null && model1.indexOf(zNodes[b].pId) != -1){
						zNodes[b].id += ":"+ model1[0];
					}
					if( zNodes[b].pId !=null && model2.indexOf(zNodes[b].pId) != -1){
						zNodes[b].id += ":" + model2[0];
					}
				}
			}
			$.fn.zTree.init($("#mappingTree"), setting, zNodes);
		} else{
			$.fn.zTree.init($("#mappingTree"), setting, zNodes);
		}
	} else{
		DWZ.ajaxDone(json);
	}
}
function setSelectOneMap(){
	debugger;
	var mDSTree = $.fn.zTree.getZTreeObj("mappingTree"),
	nodes = mDSTree.getCheckedNodes(true);
	if(nodes.length < 1){
		alertMsg.warn('请选择节点');
		return;
	}
	if(selObj == undefined){
		alertMsg.warn('未获取到被填充节点');
		return;
	}
	var num = 0;
	for (var i=0, l=nodes.length; i<l; i++) {
		if(nodes[i].isParent)
			continue;
		num++;
		try{
			if($("input[name='mapLeft']",selObj).length == 1){
				if(driver.indexOf("IBM") != -1){
					var tableName = nodes[i].getParentNode().id.split(":")[0];
					var modelName = nodes[i].getParentNode().id.split(":")[1];
					var colName = nodes[i].name;
					$("input[name='mapLeft']",selObj).val(modelName + "." + tableName+"/"+ colName);
					$("input[name='mapLeft']",selObj).attr("title",modelName + "." + tableName+"/"+ colName);
					$("input[name='sourceTableName']",selObj).val(modelName + "." + tableName);
					$("input[name='sourceColumnName']",selObj).val(colName);
					$("input[name='sourceTableName2']",selObj).val(modelName + "." + tableName);
					$("input[name='sourceColumnName2']",selObj).val(colName);
				}else{
					$("input[name='mapLeft']",selObj).val(nodes[i].getParentNode().name+"/"+nodes[i].name);
					$("input[name='mapLeft']",selObj).attr("title",nodes[i].getParentNode().name+"/"+nodes[i].name);
					$("input[name='sourceTableName']",selObj).val(nodes[i].getParentNode().id);
					$("input[name='sourceColumnName']",selObj).val(nodes[i].id);
					$("input[name='sourceTableName2']",selObj).val(nodes[i].getParentNode().name);
					$("input[name='sourceColumnName2']",selObj).val(nodes[i].name);
				}
			}
			else{
				if(driver.indexOf("IBM") != -1){
					var tableName = nodes[i].getParentNode().id.split(":")[0];
					var modelName = nodes[i].getParentNode().id.split(":")[1];
					var colName = nodes[i].name;
					$("input[name='mapRight']",selObj).val(modelName + "." + tableName+"/"+ colName);
					$("input[name='mapRight']",selObj).attr("title",modelName + "." + tableName+"/"+ colName);
					$("input[name='targetTableName']",selObj).val(modelName + "." + tableName);
					$("input[name='targetColumnName']",selObj).val(colName);
					$("input[name='targetTableName2']",selObj).val(modelName + "." + tableName);
					$("input[name='targetColumnName2']",selObj).val(colName);
					//字段名#表类型(表中文名)#字段中文#表名
					var cInf = colName+"#"+modelName + "." + tableName+"#"+colName+"#"+modelName + "." + tableName;
				}else{
					$("input[name='mapRight']",selObj).val(nodes[i].getParentNode().name+"/"+nodes[i].name);
					$("input[name='mapRight']",selObj).attr("title",nodes[i].getParentNode().name+"/"+nodes[i].name);
					$("input[name='targetTableName']",selObj).val(nodes[i].getParentNode().id);
					$("input[name='targetColumnName']",selObj).val(nodes[i].id);
					$("input[name='targetTableName2']",selObj).val(nodes[i].getParentNode().name);
					$("input[name='targetColumnName2']",selObj).val(nodes[i].name);
					//字段名#表类型(表中文名)#字段中文#表名
					var cInf = nodes[i].id+"#"+nodes[i].getParentNode().name+"#"+nodes[i].name+"#"+nodes[i].getParentNode().id;
				}

				$("input[name='targetColumnInfo']",selObj).val(cInf);
			}
		}
		catch(e){
			alertMsg.error('设置选择字段出现异常');
		}
	}
	if(num == 0){
		alertMsg.warn('请选择子节点');
		return;
	}
	$.pdialog.closeCurrent();
}
