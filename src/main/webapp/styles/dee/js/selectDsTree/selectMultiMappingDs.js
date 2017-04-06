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
		onAsyncError: onAsyncError
	}
};
var zNodes =[{id:0, pId:-1, name:"空节点"}];
var moreDlg;

$(function(){
	init();
	moreDlg = $(">.dialog:last-child", "body").data("moreDlg");
	$("#mappingDsType").die().live("change",function (){
		chgSelDs($(this).val());
	});
	$("#btnMultiOk").die().live("click",function (){
		setSelectMultiMap();
	});
	$("#btnTarget").die().live("click",function (){
		takeTarget();
	});
	$("#btnSource").die().live("click",function (){
		takeSource();
	});
	$(".sortDrag >div",$("#multiMappingDiv")).live('mouseover',function() {
		var rv = $("a.btnRemove",$(this));
		if(rv.length == 0)
			$("<a class='btnRemove' style='position:absolute;top:1px;right:0px;'></a>").appendTo($(this));
	});
	$(".sortDrag >div",$("#multiMappingDiv")).live('mouseleave',function(){
		var rv = $("a.btnRemove",$(this));
		if(rv != undefined){
			rv.remove(); //移出
		}
	});
	$("a.btnRemove",$("#multiMappingDiv")).die().live('click', function() {
		if($(this).parent())
			$(this).parent().remove();
	});

	//实现滚动同步
	$("div",$("#multiMappingDiv")).scroll(function() {
		if($(this).parent().attr("id") == "palMultiMappingLeft"){
			$("div","#palMultiMappingRight").scrollTop($(this).scrollTop());
		}
		else if($(this).parent().attr("id") == "palMultiMappingRight"){
			$("div","#palMultiMappingLeft").scrollTop($(this).scrollTop());
		}
	});
});

function init(){
	chgSelDs("2");
}

//模糊查询回车事件
$("#selectMultiMappingDsZtree").keydown(function(event) { 
	if (event.keyCode == 13) { 
		searchZtree("mappingTree",$("#searchMultiMappingDs").val());
	} 
}); 

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

function chgSelDs(rsId){
	ajaxTodo("/dee/selectDsTree!selectOneDs.do?dsId="+rsId,mappingCallBack);
}
var driver=""
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
function setSelectMultiMap(){

	var i=0,j=0,m=0,n=0;
	var l=$("#dSource div").length,r=$("#dTarget div").length;
	if(l == 0 && r == 0){
		alertMsg.warn('请选择映射字段');
		return;
	}
	else if(l-r > 0){
		m = clearMapInput(0,moreDlg);
	}
	else if(l-r < 0){
		n = clearMapInput(1,moreDlg);
	}

	$("#dSource div").each(function (){
		setMapInput(0,
			$("input[name='sourceTableName']",$(this)).val(),
			$("input[name='sourceColumnName']",$(this)).val(),
			$("input[name='sourceTableName2']",$(this)).val(),
			$("input[name='sourceColumnName2']",$(this)).val(),
			moreDlg);
		i++;
	});
	$("#dTarget div").each(function (){
		var tTable = $("input[name='targetTableName']",$(this)).val();
		var tCol = $("input[name='targetColumnName']",$(this)).val();
		var tTable2 = $("input[name='targetTableName2']",$(this)).val();
		var tCol2 = $("input[name='targetColumnName2']",$(this)).val();
		setMapInput(1,tTable,tCol,tTable2,tCol2, moreDlg);
		j++;
	});
	var y = 0;
	if(i-j>0){
		y = m-(i-j);
		if(y > 0){
			for(var x=0;x<y;x++){
				setMapInput(0,"","","","", moreDlg);
			}
		}
		else if(y<0){
			for(var x=0;x<-y;x++){
				setMapInput(1,"","","","", moreDlg);
			}
		}
	}
	else if(i-j<0){
		y = n-(j-i);
		if(y > 0){
			for(var x=0;x<y;x++){
				setMapInput(1,"","","","", moreDlg);
			}
		}
		else if(y<0){
			for(var x=0;x<-y;x++){
				setMapInput(0,"","","","", moreDlg);
			}
		}
	}
	if ($.fn.sortDragByClass) $("div.sortDrag", document).sortDragByClass();
	$.pdialog.closeCurrent();
}

function takeTarget(){
	if(driver == "dm.jdbc.driver.DmDriver"){
		var mDSTree = $.fn.zTree.getZTreeObj("mappingTree"),
			nodes = mDSTree.getCheckedNodes(true);
		if(nodes.length < 1){
			alertMsg.warn('请选择目标字段');
			return;
		}
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
				continue;
			var selvalue = nodes[i].getParentNode().getParentNode().name+"."+ nodes[i].getParentNode().name+"/"+nodes[i].name;
			var selShortVal = selvalue;
			if(selvalue.length > 11){
				selShortVal = selvalue.substring(0,10)+"..";
			}
			var targetStr = "<div style='border:1px solid #B8D0D6;padding:5px;margin:1px;width:138px;position:relative;' title='";
			targetStr += selvalue +"'>";
			targetStr += selShortVal;
			targetStr += "<input type='hidden' name='targetTableName' value='"+nodes[i].getParentNode().getParentNode().name+"."+nodes[i].getParentNode().id+"'/>";
			targetStr += "<input type='hidden' name='targetColumnName' value='"+nodes[i].id+"'/>";
			targetStr += "<input type='hidden' name='targetTableName2' value='"+nodes[i].getParentNode().getParentNode().name+"."+nodes[i].getParentNode().name+"'/>";
			targetStr += "<input type='hidden' name='targetColumnName2' value='"+nodes[i].name+"'/>";
			targetStr += "</div>";
			$("#dTarget").append(targetStr);
		}
		if ($.fn.sortDragAct) $("#dTarget").sortDragAct();
	}else if(driver.indexOf("IBM") != -1){
		var mDSTree = $.fn.zTree.getZTreeObj("mappingTree"),
			nodes = mDSTree.getCheckedNodes(true);
		if(nodes.length < 1){
			alertMsg.warn('请选择目标字段');
			return;
		}
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
				continue;
			var selvalue = nodes[i].getParentNode().getParentNode().name+"."+ nodes[i].getParentNode().name+"/"+nodes[i].name;
			var selShortVal = selvalue;
			var tableName = nodes[i].getParentNode().id.split(":")[0];
			var modelName = nodes[i].getParentNode().id.split(":")[1];
			var colName = nodes[i].name;
			if(selvalue.length > 11){
				selShortVal = selvalue.substring(0,10)+"..";
			}
			var targetStr = "<div style='border:1px solid #B8D0D6;padding:5px;margin:1px;width:138px;position:relative;' title='";
			targetStr += selvalue +"'>";
			targetStr += selShortVal;
			targetStr += "<input type='hidden' name='targetTableName' value='"+ modelName +"."+ tableName +"'/>";
			targetStr += "<input type='hidden' name='targetColumnName' value='"+ colName +"'/>";
			targetStr += "<input type='hidden' name='targetTableName2' value='"+ modelName +"."+ tableName +"'/>";
			targetStr += "<input type='hidden' name='targetColumnName2' value='"+nodes[i].name+"'/>";
			targetStr += "</div>";
			$("#dTarget").append(targetStr);
		}
		if ($.fn.sortDragAct) $("#dTarget").sortDragAct();
	}
	else{
		var mDSTree = $.fn.zTree.getZTreeObj("mappingTree"),
			nodes = mDSTree.getCheckedNodes(true);
		if(nodes.length < 1){
			alertMsg.warn('请选择目标字段');
			return;
		}
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
				continue;
			var selvalue = nodes[i].getParentNode().name+"/"+nodes[i].name;
			var selShortVal = selvalue;
			if(selvalue.length > 11){
				selShortVal = selvalue.substring(0,10)+"..";
			}
			var targetStr = "<div style='border:1px solid #B8D0D6;padding:5px;margin:1px;width:138px;position:relative;' title='";
			targetStr += selvalue +"'>";
			targetStr += selShortVal;
			targetStr += "<input type='hidden' name='targetTableName' value='"+nodes[i].getParentNode().id+"'/>";
			targetStr += "<input type='hidden' name='targetColumnName'  value='"+ nodes[i].id +"'/>";
			targetStr += "<input type='hidden' name='targetTableName2' value='"+ nodes[i].getParentNode().name +"'/>";
			targetStr += "<input type='hidden' name='targetColumnName2' value='"+nodes[i].name+"'/>";
			targetStr += "</div>";
			$("#dTarget").append(targetStr);
		}
		if ($.fn.sortDragAct) $("#dTarget").sortDragAct();
	}
}

function takeSource(){
	if(driver.indexOf("IBM") != -1){
		var mDSTree = $.fn.zTree.getZTreeObj("mappingTree"),
			nodes = mDSTree.getCheckedNodes(true);
		if(nodes.length < 1){
			alertMsg.warn('请选择来源字段');
			return;
		}
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
				continue;
			var selvalue = nodes[i].getParentNode().name+"/"+nodes[i].name;
			var selShortVal = selvalue;
			var tableName = nodes[i].getParentNode().id.split(":")[0];
			var modelName = nodes[i].getParentNode().id.split(":")[1];
			if(selvalue.length > 11){
				selShortVal = selvalue.substring(0,10)+"..";
			}
			var sourceStr = "<div style='border:1px solid #B8D0D6;padding:5px;margin:1px;width:138px;position:relative;' title='";
			sourceStr += selvalue +"'>";
			sourceStr += selShortVal;
			sourceStr += "<input type='hidden' name='sourceTableName'  value='"+ modelName +"."+ tableName +"'/>";
			sourceStr += "<input type='hidden' name='sourceColumnName' value='"+nodes[i].id+"'/>";
			sourceStr += "<input type='hidden' name='sourceTableName2' value='"+nodes[i].getParentNode().name+"'/>";
			sourceStr += "<input type='hidden' name='sourceColumnName2' value='"+nodes[i].name+"'/>";
			sourceStr += "</div>";
			$("#dSource").append(sourceStr);
		}
		if ($.fn.sortDragAct) $("#dSource").sortDragAct();
	}

	else{
		var mDSTree = $.fn.zTree.getZTreeObj("mappingTree"),
			nodes = mDSTree.getCheckedNodes(true);
		if(nodes.length < 1){
			alertMsg.warn('请选择来源字段');
			return;
		}
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].isParent)
				continue;
			var selvalue = nodes[i].getParentNode().name+"/"+nodes[i].name;
			var selShortVal = selvalue;
			if(selvalue.length > 11){
				selShortVal = selvalue.substring(0,10)+"..";
			}
			var sourceStr = "<div style='border:1px solid #B8D0D6;padding:5px;margin:1px;width:138px;position:relative;' title='";
			sourceStr += selvalue +"'>";
			sourceStr += selShortVal;
			sourceStr += "<input type='hidden' name='sourceTableName' value='"+nodes[i].getParentNode().id+"'/>";
			sourceStr += "<input type='hidden' name='sourceColumnName' value='"+nodes[i].id+"'/>";
			sourceStr += "<input type='hidden' name='sourceTableName2' value='"+nodes[i].getParentNode().name+"'/>";
			sourceStr += "<input type='hidden' name='sourceColumnName2' value='"+nodes[i].name+"'/>";
			sourceStr += "</div>";
			$("#dSource").append(sourceStr);
		}
		if ($.fn.sortDragAct) $("#dSource").sortDragAct();
	}
}
