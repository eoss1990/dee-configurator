var setting = {
	check: {
		enable: true,
//			chkStyle: "radio",
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
		onAsyncError: onAsyncError,
		onCheck: onCheck
	}
};
var zNodes =[{id:0, pId:-1, name:"空节点"}];
var selObj;
$("[id^=jdbcWriterSelect]").keydown(function(event) { 
	if (event.keyCode == 13) { 
		var searchIdName = $(event.target).attr("id");
		if(searchIdName == null || searchIdName.indexOf("searchText")<0) return;
		var dialogId = searchIdName.substr(10,searchIdName.length);
		searchZtree("dsTree"+dialogId,$("#searchText"+dialogId).val());
	} 
}); 

function setSelectTree(thisRow,dialogId){
	//清空查询条件
	$("#searchText"+dialogId).val("");
	
	var obj=document.getElementById("biggest");

	var parObj=obj;
	var top=obj.offsetTop;
	while(parObj = parObj.offsetParent){
		top+=parObj.offsetTop;

	}
	var event = window.event || arguments.callee.caller.arguments[0];
	var cy=event.clientY-top+document.documentElement.scrollTop +48;


	tabObj = $("input:first",thisRow);
	selObj=$("input:eq(1)",thisRow);
//	selObj = $("input:last",thisRow);
	var jdbcOffset = selObj.offset();
//	$("#jdbcWriterSelect"+dialogId).css({left:jdbcOffset.left-280 + "px", top:jdbcOffset.top + selObj.outerHeight()-60 + "px"}).slideDown("fast");
	$("#jdbcWriterSelect"+dialogId).css({left:"279px", top:cy+"px"}).slideDown("fast");
	var rsId = $("#ref_id"+dialogId).val();
	ajaxTodo("/dee/selectDsTree!selectJdbcReaderDs.do?dsId="+rsId,function(json) {
		jdbcReaderCallBack(json,dialogId);
	});

	$("#jdbcWriterDiv"+dialogId).bind("mousedown",onJdbcReaderMDown);

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

function checkSel(treeId){
	var zTree = $.fn.zTree.getZTreeObj(treeId),
		nodes = zTree.getCheckedNodes(true);
	if(nodes.length < 1)
		alertMsg.warn('请选择节点');

}

function hideMenu(dialogId) {
	$.fn.zTree.destroy("dsTree"+dialogId);
	$("#jdbcWriterSelect"+dialogId).fadeOut("fast");
	$("#jdbcWriterDiv"+dialogId).unbind("mousedown");
}
function onJdbcReaderMDown(event) {
	var dialogId = $(event.target).parents("form").find("input[name='resourceId']").val();
	if (!(event.target.id == "jdbcWriterSelect"+dialogId || $(event.target).parents("#jdbcWriterSelect"+dialogId).length>0)) {
		hideMenu(dialogId);
	}
}
var driver;
function jdbcReaderCallBack(json,dialogId){
	if (json.statusCode == DWZ.statusCode.ok){
		zNodes = json.jsonData;
		var start = "";
		var id = "";
		for (var i = 0 ;i<zNodes.length;i++) {
			if (zNodes[i].pId == "-1") {
				var tmp = zNodes[i].id.split(':');
				if (tmp.length > 1) {
					zNodes[i].id = tmp[0];
					driver = tmp[1] + zNodes[i].name;
					start = i-1;
					id = tmp[0];
				}
			}
		}
		var modelName = [];
		var model1 = [];
		var model2 = [];
		if(driver.indexOf("IBM") != -1){
			for(var j = start ; j < zNodes.length; j++){
				if( zNodes[j].pId !=null && zNodes[j].pId == id){
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

		}
		for(var b = start ; b < zNodes.length; b++){
			if( zNodes[b].pId !=null && model1.indexOf(zNodes[b].pId) != -1){
				zNodes[b].id += ":"+ model1[0];
			}
			if( zNodes[b].pId !=null && model2.indexOf(zNodes[b].pId) != -1){
				zNodes[b].id += ":" + model2[0];
			}
		}
		$.fn.zTree.init($("#dsTree"+dialogId), setting, zNodes);
	}
	else{
		DWZ.ajaxDone(json);
	}
}

function onCheck(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj(treeId);
	nodes = zTree.getCheckedNodes(true);
	var tableName = "",colName="";
	var check = "";
	debugger;
	if(nodes[0] != null && driver.indexOf("dm.jdbc.driver.DmDriver") != -1)
		check = nodes[0].getParentNode().getParentNode().name;
	if(check !="" && driver.indexOf(check) != -1){
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
			{
				if(tableName==""){
					tableName = nodes[i].id;
					var typeName = nodes[i].getParentNode().name;
				}


				else
				{
					if(tableName!=nodes[i].id)
					{
						selObj.val("");
						tabObj.val("");
						alertMsg.error("每条数据只能选择一个表！");
						return false;
					}
				}
			}
			else
				colName += nodes[i].id + ",";
		}
		if (colName.length > 0 ) colName = colName.substring(0, colName.length-1);
		tableName = typeName+"."+tableName;
		selObj.val(colName);
		tabObj.val(tableName);

	}else if(driver.indexOf("IBM") != -1){
		debugger;
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
			{
				if(tableName==""){
					var nameSplit =  nodes[i].id.split(":");
					tableName += nameSplit[1] + "." + nameSplit[0];

				}
				else
				{
					if(tableName!=nodes[i].id)
					{
						selObj.val("");
						tabObj.val("");
						alertMsg.error("每条数据只能选择一个表！");
						return false;
					}
				}
			}
			else
				colName += nodes[i].id + ",";
		}
		if (colName.length > 0 ) colName = colName.substring(0, colName.length-1);
		selObj.val(colName);
		tabObj.val(tableName);

	}
	else{
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
			{
				if(tableName=="")
					tableName = nodes[i].id;
				else
				{
					if(tableName!=nodes[i].id)
					{
						selObj.val("");
						tabObj.val("");
						alertMsg.error("每条数据只能选择一个表！");
						return false;
					}
				}
			}
			else
				colName += nodes[i].id + ",";
		}
		if (colName.length > 0 ) colName = colName.substring(0, colName.length-1);
		selObj.val(colName);
		tabObj.val(tableName);

	}




}

function submitJDBCWriter(obj)
{
	var dialog = returnDlg(obj);
	var values=getGroupItems("jdbcWriterForm","items",null,dialog.data("id"));
	values.push($("#dis_name",$("#jdbcWriterForm",dialog)).val());
	if(hasSpecialChar(values))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#jdbcWriterForm",dialog).submit();
}

function delRow(obj) {
	var tr = $(obj).closest("tr");
	tr.remove();
}
