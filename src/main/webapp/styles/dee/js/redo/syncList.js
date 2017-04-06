$(document).ready(function() {
	
});
function checkDate(form, navTabId){
//	if(!(($("#startDate").val() == "" && $("#endDate").val() == "") || ($("#startDate").val() != "" && $("#endDate").val() != ""))){
//        alertMsg.info("请输入完成日期进行查询！");
//	}
	var $form = $(form);
	if (form[DWZ.pageInfo.pageNum]) form[DWZ.pageInfo.pageNum].value = 1;
	navTab.reload($form.attr('action'), {data: $form.serializeArray(), navTabId:navTabId});
	return false;
}
function refreshpage() {
 
    navTab.reloadFlag("deeRedoSyncList");
    navTab.reloadFlag("exchangeBaseNav");
    alertMsg.info("删除成功");
    
}

function deleteTaskPreCallback() {

        var returnVals = [];
        $("[name='sync_id']").each(function() {
            if ($(this).attr("checked")) {
                returnVals.push($(this).val());
            }
        });
    
        var uids = $("#open_sync_id");
        
      if (!Array.prototype.indexOf){  
            Array.prototype.indexOf = function(elt){  
            var len = this.length >>> 0;  
            var from = Number(arguments[1]) || 0;  
            from = (from < 0)  
                 ? Math.ceil(from)  
                 : Math.floor(from);  
            if (from < 0)  
              from += len;  
            for (; from < len; from++)  
            {  
              if (from in this &&  
                  this[from] === elt){
                  return from;  
              }
            }
            return -1;  
          };  
        }  
       
        if (uids) {
            for (var i = 0; i < uids.size(); i++) {
                var uid = $(uids[i]).val();
            
                if (returnVals.indexOf(uid) != -1) {
                    alertMsg.error("删除任务已打开，请关闭后删除！");
                    return false;
                }
            }
        }
}


// 全选同步历史
function checkSyncAll(obj) {
    if ($(obj).attr("checked")) {
        $("[name='sync_id']").attr("checked", 'true'); // 全选
    } else {
        $("[name='sync_id']").removeAttr("checked"); // 取消全选
    }
}
//全选同步历史
function checkSyncAllAndRedo(obj) {
    if ($(obj).attr("checked")) {
        $("[name='sync_id']").attr("checked", 'true'); // 全选
        $("[name='redo_id']").attr("checked", 'true'); // 
        $("[name='state']").attr("checked", 'true');
    } else {
        $("[name='sync_id']").removeAttr("checked"); // 取消全选
        $("[name='redo_id']").removeAttr("checked"); // 
        $("[name='state']").removeAttr("checked");
    }
}
//勾选隐藏属性
function checkSyncRedo(obj) {
    if ($(obj).attr("checked")) {
        $("[id='"+obj.value+"']").attr("checked", 'true'); // 全选
    } else {
        $("[id='"+obj.value+"']").removeAttr("checked"); // 取消全选
    }
}

// 全选重发列表
function checkRedoAll(obj) {
    if ($(obj).attr("checked")) {
        $("[name='redo_id']").attr("checked", 'true'); // 全选
    } else {
        $("[name='redo_id']").removeAttr("checked"); // 取消全选
    }
}
// 重发后刷新redo

function refresh_redo(obj) {
    var redo_id = $("#redo_id").val();
    var sync_id = $(".redopage_syncid").val();
    $.ajax({
        async : false,
        type : "post",
        dataType : "json",
        data : 'redo_id=' + redo_id,
        url : "/dee/redo!findNewRedo.do",
        success : function(result) {
            var jsonData = result.jsonData;
            var counter = jsonData.counter;

            $(".redocounter").html(counter);
            if (obj.statusCode == 200) {
                alertMsg.correct(obj.message);
                $(".stateflag").html("成功");
                $(".redocounter").html((counter + 1));
            } else if (obj.statusCode == 300) {
                alertMsg.error(obj.message);

            }
        },

    });

    $.ajax({
        async : false,
        type : "post",
        dataType : "text",
        data : 'sync_id=' + sync_id,
        url : "/dee/redo!refreshSync.do",
        success : function(result) {
            if (result == 1) {
                $(".list1").find("tr:first").find("td:last").text("成功");
            }
        }
    });

}

function checkState(){
	obj1 = $("[name='state']");
    state = "";
    for(k in obj1){
        if(obj1[k].checked)
            state = state + "," + obj1[k].value;
    }
    obj2 = $("[name='redo_id']");
    redo_id = "";
    for(k in obj2){
        if(obj2[k].checked){
        	if(redo_id == ""){
        		redo_id = obj2[k].value;
        	}else{
            	redo_id = redo_id + "," + obj2[k].value;
        	}
        }
    };
    if(state.indexOf("1") != -1){
    	alertMsg.error("请勿勾选成功任务重发！");
    }else if(redo_id == ""){
        alertMsg.error("请选择需要重发的任务！");
    }else{
    	$.ajax({
            async : false,
            type : "post",
            dataType : "text",
            data : 'redo_id=' + redo_id,
            url : "/dee/redo!executeRedo.do",
            success : function(result) {
            	map = result.trim().split(",");
            	if(map[0].split(":")[1].replaceAll("\"","") == 200){
            		alertMsg.correct(map[1].split(":")[1].split(";")[0].replace("\"",""));
            	} else if (map[0].split(":")[1].replaceAll("\"","") == 300) {
                    alertMsg.error(map[1].split(":")[1].split(";")[0].replaceAll("\"",""));
                }
            }
        });
    	$.ajax({
            async : true,
            type : "post",
            dataType : "text",
            url : "/dee/redo!syncListLog.do",
            success : function(result) {
            	$("#postForm").submit();
            }
        });
    }
    return false;
}

//重发后刷新异常列表
function refresh_sync(obj) {
	var strs = obj.message.split(";");
	if (obj.statusCode == 200) {
        alertMsg.correct(strs[0]);
    } else if (obj.statusCode == 300) {
        alertMsg.error(strs[0]);
    }
	$.ajax({
        async : true,
        type : "post",
        dataType : "text",
        url : "/dee/redo!syncListLog.do",
        success : function(result) {
        	$("#postForm").submit();
        }
    });
}

// 删除redo
function delRedo() {
    var returnVal = [];
    $("[name='redo_id']").each(function() {
        if ($(this).attr("checked")) {
            returnVal.push($(this).val());
        }
    });
    if (returnVal.length > 0) {
        alertMsg.confirm("请您确认是否删除异常信息？", {
            okCall : function() {
                $.ajax({
                    async : false,
                    type : "post",
                    dataType : "json",
                    data : 'redoIds=' + returnVal.join(','),
                    url : "/dee/redo!delRedo.do",
                    success : function(result) {
                        if (result.statusCode == 200) {

                            alertMsg.correct(result.message); // 操作成功

                        } else {
                            alertMsg.info(result.message); // 操作失败
                        }
                    },
                    error : function(result) {
                        alertMsg.info("error:" + result);
                    }
                });
            }
        });
    } else {
        alertMsg.info('没有待删除的数据。');
    }
}

// 重发
function redoSelect() {
    var returnVal = [];
    $("[name='redo_id']").each(function() {
        if ($(this).attr("checked")) {
            returnVal.push($(this).val());
        }
    });
    if (returnVal.length > 0) {
        window.location = ctx + "/servlet/RedoServlet?method=redoSele&sync_id=" + syncId + "&ids=" + returVal.join(',');
    } else {
        alertMsg.info('没有待重发的数据。');
    }
}
var curExpandNode = null;
function beforeExpand(treeId, treeNode) {
	var pNode = curExpandNode ? curExpandNode.getParentNode() : null;
	var treeNodeP = treeNode.parentTId ? treeNode.getParentNode() : null;
	var zTree = $.fn.zTree.getZTreeObj("taskTree");
	for (var i = 0, l = !treeNodeP ? 0 : treeNodeP.children.length; i < l; i++) {
		if (treeNode !== treeNodeP.children[i]) {
			zTree.expandNode(treeNodeP.children[i], false);
		}
	}
	while (pNode) {
		if (pNode === treeNode) {
			break;
		}
		pNode = pNode.getParentNode();
	}
	if (!pNode) {
		singlePath(treeNode);
	}

}
function singlePath(newNode) {
	if (newNode === curExpandNode)
		return;
	if (curExpandNode && curExpandNode.open == true) {
		var zTree = $.fn.zTree.getZTreeObj("taskTree");
		if (newNode.parentTId === curExpandNode.parentTId) {
			zTree.expandNode(curExpandNode, false);
		} else {
			var newParents = [];
			while (newNode) {
				newNode = newNode.getParentNode();
				if (newNode === curExpandNode) {
					newParents = null;
					break;
				} else if (newNode) {
					newParents.push(newNode);
				}
			}
			if (newParents != null) {
				var oldNode = curExpandNode;
				var oldParents = [];
				while (oldNode) {
					oldNode = oldNode.getParentNode();
					if (oldNode) {
						oldParents.push(oldNode);
					}
				}
				if (newParents.length > 0) {
					zTree.expandNode(oldParents[Math.abs(oldParents.length
							- newParents.length) - 1], false);
				} else {
					zTree.expandNode(oldParents[oldParents.length - 1], false);
				}
			}
		}
	}
	curExpandNode = newNode;
}
function onExpand(event, treeId, treeNode) {
	curExpandNode = treeNode;
}
function formatXml(xml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    var pad = 0;
    jQuery.each(xml.split('\r\n'), function(index, node) {
        var indent = 0;
        if (node.match( /.+<\/\w[^>]*>$/ )) {
            indent = 0;
        } else if (node.match( /^<\/\w/ )) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    });

    return formatted;
}
function unescapeHTML(target) {
    //还原为可被文档解析的HTML标签            
    return target.replace(/&quot;/g, '"').replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&")
        //处理转义的中文和实体字符
        .replace(/&#([\d]+);/g, function($0, $1) {
            return String.fromCharCode(parseInt($1, 10));
        });
}
var setting = {
	view : {
		dblClickExpand : false
	},
	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {
		beforeExpand : beforeExpand,
		onExpand : onExpand,
		onClick : function zTreeOnClick(event, treeId, treeNode) {
			$.ajax({
		        async : false,
		        type : "post",
		        dataType : "json",
		        data : 'sync_id=' + treeNode.strIds + ',' + treeNode.id,
		        url : "/dee/redo!getAdapterDetail.do",
		        success : function(result) {
		        	var jsonData = result.jsonData;
		        	if(jsonData != null){
			        	var adapter = jsonData[0][0];
			            if(!("startData" == adapter.name || "endData" == adapter.name)){
			            	if("" != adapter.data){
			            		$("#dataText").val("异常信息:"+formatXml(unescapeHTML(adapter.data)));
			            	}else{
			            		$("#dataText").val(adapter.parms);
			            	}
			            } else {
			                $("#dataText").val("数据:"+formatXml(unescapeHTML(adapter.data)) + 
			                		"\r\n参数:" + adapter.parms.replace(new RegExp('],','gm'),'],\r\n'));
			            }
		        	}else{
		        		$("#dataText").val("")
		        	}
		        }
		    });
		}
	}
};
function adapterDetail(str) {
	if ($("#adapterDetail").is(":hidden")) {
		var a = $("#syncLog").height() / 2;
		$("#syncLog").height(a);
		$("#taskFrom").height(a-27);
		$("#adapterDetail").show();
		$("#adapterDetail").height(a);
		$("#detailTable").height(a-8);
		$("#dataText").height(a-8);
	}
	$.ajax({
        async : false,
        type : "post",
        dataType : "json",
        data : 'sync_id=' + str,
        url : "/dee/redo!getTaskTree.do",
        success : function(result) {
        	var jsonData = result.jsonData;
        	if(jsonData != null){
        		$("#dataText").val("")
        		var rootNode = new Array();
                rootNode = jsonData[0];
                var zNodes = [];
    			if (rootNode.length > 0) {
    				for (var i = 0; i < rootNode.length; i++) {
    					if(rootNode[i].state == "1"){
    	    				path = "/styles/dee/themes/images/adapterSuccess.png";
    					}else if(rootNode[i].state == "0"){
    	    				path = "/styles/dee/themes/images/adapterFalse.png";
    					}
    					if(rootNode[i].state != "-1"){
    						var obj = {
    	    					id : rootNode[i].nodeId,
    	    					pId : rootNode[i].nodePId,
    	    					name : rootNode[i].nodeName,
    	    					state : rootNode[i].state,
    	    					strIds : rootNode[i].strIds,
    	    					open : true,
    	    					icon : path
    	    				};
    					}else{
    						var obj = {
        	    				id : rootNode[i].nodeId,
        	    				pId : rootNode[i].nodePId,
        	    				name : rootNode[i].nodeName,
        	    				state : rootNode[i].state,
        	    				strIds : rootNode[i].strIds,
        	    				open : true
        	    			};
    					}
    					zNodes.push(obj);
    				}
    				$.fn.zTree.init($("#taskTree"), setting, zNodes);
    			}
        	}else{
        		$("#taskTree").text("");
        		$("#dataText").val("");
        	}
        }
    });
}