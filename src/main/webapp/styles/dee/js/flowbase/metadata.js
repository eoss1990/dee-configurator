
$(document).ready(function(){
  
});





function submitMeta(obj) {
 
    
    var trcounter = $("#trcounter tr").length;
    var resultList = "";
    var partofResult = [];
    var colum = [];
    var check = [];
    var relation = "";
    var flaglist = [];
    var flag = false;
    if (!Array.prototype.indexOf) {
        Array.prototype.indexOf = function(elt, from) {
            var len = this.length >>> 0;
            var from = Number(arguments[1]) || 0;
            from = (from < 0) ? Math.ceil(from) : Math.floor(from);
            if (from < 0)
                from += len;
            for (; from < len; from++) {
                if (from in this && this[from] === elt)
                    return from;
            }
            return -1;
        };
    }

        for (var i = 0; i < trcounter; i++) {
            if ($("#trcounter tr").eq(i).find("td").eq(6).find("input").val() != ""
                    && $("#trcounter tr").eq(i).find("td").eq(6).is(':visible')) {
                resultList = $("#trcounter tr").eq(i).find("input").val();
                relation = $("#trcounter tr").eq(i).find("td").eq(6).find("input").val();
                for (var k = 0; k < trcounter; k++) {
                    if (resultList == $("#trcounter tr").eq(k).find("input").val()) {
                        partofResult.push($("#trcounter tr").eq(k).find("select").eq(0).val());
                        check.push($("#trcounter tr").eq(k).find("td").eq(4).find("input").val());
                    }
                }
                for (var j = 0; j < partofResult.length; j++) {
                    if (partofResult[j] == "master") {
                        colum.push(check[j]);
                    }
                }

                flaglist.push(colum.indexOf(relation));
                colum.length = 0;
                partofResult.length = 0;
                check.length = 0;
            }
        }
        if (flaglist.indexOf(-1) == -1) {
            
            flag = true;
            
        }

        if (!flag) {
            
            alertMsg.error("关联主表字段只能关联同一结果集下的主表字段！");
            
        } else {
            var uuid = $(obj).closest(".pageFormContent").attr("name");
            if (uuid != null && uuid != "") {
                if (hasSpecialChar(getGroupItems("metaForm", "meta"))) {
                    alertMsg.error(DWZ.msg("alertHasSpecialChar"));
                    return false;
                }
                var flagtips = true;
               
                for (var i = 0; i < trcounter; i++) {
                    if(isNaN($("#trcounter tr").eq(i).find("td").eq(8).find("input").val())){
                        flagtips = false;
                        break;
                    }
                   
                }
                if(!flagtips){
                    alertMsg.error("字段长度必须为数字！");
                    return;
                }
                //排序号添加约束判断
                var msg = "";
                var orderNoFlag = true;
                var orders = [];
                for (var i = 0; i < trcounter; i++){
                	var val = $("#trcounter tr").eq(i).find("td").eq(11).find("input").val();
                	orders.push(val);
                	 if(!checkRate(val)){
                     	orderNoFlag = false;
                     	msg = "排序号请输入大于等于1的正整数！";
                        break;
                     }else{
                    	if(val > 1000){
                    		orderNoFlag = false;
                    		msg = "排序号不能大于1000！";
                    		break;
                    	}
                     }
                }
                if(!orderNoFlag){
                    alertMsg.error(msg);
                    return;
                }
            //    debugger;
                //重复排序号验证
                for(var i=0 ;i<orders.length;i++) {
                    for(var j=i+1 ;j<orders.length; j++) {
                        if(orders[i]===orders[j]) {
                        	orderNoFlag = false;
                        	msg = "排序号有重复,请修改！";
                        	break;
                        }
                    }
                }
                if(!orderNoFlag){
                    alertMsg.error(msg);
                    return;
                }
                
                
                var action = "/dee/flowbase!saveMetadata.do?uuid=" + uuid;
                $(obj).closest("#metaForm").attr("action", action);
                $(obj).closest("#metaForm").submit();
            } else {
                alertMsg.error("请先保存基础信息！");
            }
        }
 
}

/**
 * 排序号计算
 */
function CalcOrder(orderObj){
	debugger;
	var t = $("#metaDataOrderMax");
	if(t){
		var val = t.attr("value");
		var newVal = orderObj.value;
		var selectName = orderObj.name;
		var re = /^[1-9]+[0-9]*]*$/;
		if (!re.test(newVal)){
//			var msg = "排序号请输入大于等于1的正整数！";
//			alertMsg.error(msg);
//			$(orderObj).addClass("error");
			return;
		}
		newVal = parseInt(newVal);
		if(newVal > 1000){
//			var msg = "排序号不能大于1000！";
//			alertMsg.error(msg);
//			$(orderObj).addClass("error");
    		return;
    	}
		
		//重复排序号验证
		var trcounter = $("#trcounter tr").length;
		for (var i = 0; i < trcounter; i++){
			var inputObj = $("#trcounter tr").eq(i).find("td").eq(11).find("input");
			var name = inputObj[0].name;
			if(name == selectName){
				continue;
			}
			var val = inputObj.val();
			var testVal = newVal+"";
			if(testVal == val){
//				msg = "排序号有重复,请修改！";
//				alertMsg.error(msg);
//				$(orderObj).addClass("error");
				return;
			}
		}
  
        
		
		val = parseInt(val); 
		if( newVal > val ){
			t.attr("value",newVal);
		//	var obj = $(orderObj).next();
		}
	}
}
function checkRate(val)  {  
	var re = /^[1-9]+[0-9]*]*$/;
	if (!re.test(val)){
        return false;  
    }else{
    	return true;  
    }
}  


function loadmetadata(){
    
    var currentPage = getCurrentPage();
    var consoleInfo = $("#debugConsole_info", currentPage).val();
    var consoleMeta = $("#debugConsole_mata", currentPage).val();

    if (!consoleInfo) {
        alertMsg.info("请先调试任务！");
    } else {
        var check = consoleInfo.substr(0, 4);

        if (check == "运行出错") {
            alertMsg.error("任务调试失败，无法生成元数据！");
        } else if (consoleMeta == "[]") {
            alertMsg.info("没有数据，无法生成元数据！");
        } else {
            if ($("#debugConsole_out", currentPage).val() != "") {
                var title = "自动载入元数据会覆盖原有数据，是否要覆盖？";
                alertMsg.confirm(title, {
                    okCall: function () {
                    //	debugger;
                        var tmp = consoleMeta.substr(1, consoleMeta.length - 2);
                        var mata = tmp.split(".");

                        $("#mata tbody", currentPage).empty();
                        var index = 0;
                        var orderNo = 1;
                        for (var lsize = 0; lsize < mata.length; lsize++) {

                            var minfo = mata[lsize].split(",");

                            for (var lg = 2; lg < minfo.length; lg++) {
                            	
                                 $("#mata tbody", currentPage).append("<tr class=\"unitBox\">" +
                                        "<td><input size=\"9\" class=\"required textInput\" type=\"text\"  maxlength=\"20\" name=\"meta[" + index + "].formName\" value="+ minfo[0] +" ></td>" +
                                        "<td><input size=\"9\" class=\"required textInput\" type=\"text\"  maxlength=\"20\" name=\"meta[" + index + "].dbFormName\" value="+ minfo[1] +" ></td>" +
                                        "<td><input size=\"9\" class=\"required textInput\" type=\"text\"  maxlength=\"20\" name=\"meta[" + index + "].formDisName\" value="+ minfo[1] +" ></td>"+
                                        "<td><select name=\"meta[" + index + "].formType\" value=\"${item.formType}\" onchange=\"setRefFieldNone(this);\"><option value=\"master\">主表</option><option value=\"slave\">从表</option></select></td>"+
                                        "<td><input size=\"9\" class=\"required textInput\" type=\"text\" name=\"meta[" + index + "].name\"  maxlength=\"20\" value="+ minfo[lg] +" ></td>"+
                                        "<td><input size=\"9\" class=\"required textInput\" type=\"text\" name=\"meta[" + index + "].display\" maxlength=\"20\" value="+ minfo[lg] +" ></td>"+
                                        "<td><input size=\"9\" class=\"textInput\" id=\"connect\" type=\"text\" maxlength=\"20\" style=\"display:none\"  name=\"meta[" + index + "].refMainField\"></td>"+
                                        "<td><select name=\"meta[" + index + "].fieldType\" value=\"${item.fieldType}\">"+
                                                  " <option value=\"varchar\">varchar</option>" +
                                                  " <option value=\"decimal\">decimal</option>" +
                                                  " <option value=\"timestamp\">timestamp</option>" +
                                                  " <option value=\"longtext\">longtext</option>" +
                                                  " <option value=\"datetime\">datetime</option>" +
                                             "</select></td>"+
                                        "<td><input size=\"9\" class=\"required textInput\" name=\"meta[" + index + "].fieldLength\" type=\"text\"  maxlength=\"20\" ></td>"+
                                        "<td><select name=\"meta[" + index + "].isNull\">"+
                                                  "  <option value=\"true\" >是</option>"+
                                                  "  <option value=\"false\" selected=\"selected\">否</option>"+
                                                  "  </select></td>"+
                                        "<td><select name=\"meta[" + index + "].isPrimary\">"+
                                                  "  <option value=\"true\" >是</option>"+
                                                  "  <option value=\"false\" selected=\"selected\">否</option>"+
                                                  "  </select></td>"+
                                        "<td><input size=\"9\" class=\"required textInput\" type=\"text\" value="+orderNo+"  onblur=\"CalcOrder(this);\"   name=\"meta[" + index + "].orderNo\"></td>"+
                                        "<td><a class=\"btnDel\" href=\"javascript:void(0)\" onClick=\"javascript:delRow(this);\">删除</a></td>"+
                                             "</tr>");
                                 index++;
                                 orderNo++;
                            }
                        }
                        $("#metaDataOrderMax").attr("value",orderNo-1); 
                    }
                });
            }
        }
    }
}

function delRow(obj) {
    var tr = $(obj).closest("tr");
    tr.remove();
}

function setRefFieldNone(obj){
    var selRow = $(obj).parents('tr:first');
    var selRefMasterField = $("input[name$='.refMainField']",selRow);
    if($(obj).val() == 'master'){
        selRefMasterField.attr("style","display:none");
    }
    else if($(obj).val() == 'slave'){
        selRefMasterField.attr("style","");
    }
}

function getCurrentPage() {
    return $(".unitBox.page:visible");
}