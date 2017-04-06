/**
 * Editor数组，用于保证多个映射配置窗口同时打开时，Editor控件正确获取
 */
var g_mappingEditors;

$(function() {
    // 字典选择事件
    /*$('#selType', mappingDialog).change(function () {
     chgPanel($('#selType', mappingDialog).val());
     });*/

    // 字段选择
    $("a#shwMap").die().live('click', function () {
        var selObj = $(this).parents('tr:first');
        var options = {width: 400, height: 480, mask: true, selObj: selObj};
        $.pdialog.open("/dee/selectDsTree!selectMappingDs.do", "selectOne", "字段选择", options);
    });

    // 批量设置
    $("#moreBtn").die().live('click', function() {
        $.pdialog.open("/dee/selectDsTree!selectMultiMappingDs.do", "selectMulti", "批量配置",
            {width:800,height:500,mask:true, moreDlg:getMappingDlg(this)});
    });

    // 载入来源字段
    $("#loadBtn").die().live('click', function () {
        var dlg = getMappingDlg(this);
        getJdbcReaderCallBack(dlg);
    });

    // 显示删除图标
    $(".sortDrag >div", $("#mappingProcessorDiv")).live('mouseover', function () {
        var rv = $("a[class='btnRemove']", $(this));
        var lastTd = $("td:last", $(this));
        if (rv.length == 0)
            $("<td><a class='btnRemove' title='删除'></a></td>").appendTo(lastTd.parent());
    });

    // 隐藏删除图标
    $(".sortDrag >div", $("#mappingProcessorDiv")).live('mouseleave', function () {
        var rv = $("a[class='btnRemove']", $(this));
        if (rv) {
            rv.parent().remove();   // 移出包含<td>的标签
        }
    });

    // 删除事件
    $("a[class='btnRemove']", $("#mappingProcessorDiv")).die().live('click', function () {
        var $this = $(this);
        var dlg = getMappingDlg(this);
        if ($this && $this.parent().parent().parent().parent().parent().index() != -1) {
            var removeDiv = $this.parent().parent().parent().parent().parent();
            if ("dLeft" == removeDiv.parent().attr("id")) {
                $("#dRight", dlg).children().eq(removeDiv.index()).remove();
            }
            else {
                $("#dLeft", dlg).children().eq(removeDiv.index()).remove();
            }
            removeDiv.remove();
        }
    });
});

function add_line(dlg){
    setMapInput(0,"","","","", dlg);
    setMapInput(1,"","","","", dlg);
    if ($.fn.sortDragByClass) {
        $("#dLeft, #dRight", dlg).sortDragByClass();
    }
}

function setSubmitIndex(obj, index){
    var dlg = getMappingDlg($(obj));
    if ($("#mapIndex", dlg).val() == index) {
        return;
    }

    $("#mapIndex", dlg).val(index);
    onSubmitCheckBox(dlg);
    $("#mappingDisName", dlg).removeClass("required");
    $("#frmMapping", dlg).attr("action", "/dee/mapping!mappingChg.do");
    $("#frmMapping", dlg).submit();
    $("#frmMapping", dlg).attr("action", "/dee/mapping!saveMappingProcessor.do?callbackType=closeCurrent&navTabId=exchangeBaseNav");
    $("#mappingDisName", dlg).addClass("required");
}

function getJdbcReaderCallBack(json, dlg){
    DWZ.ajaxDone(json);
    if (json.statusCode == DWZ.statusCode.ok) {
        var fields = json.jsonData;
        for(var j = 0 ; j < fields.length ; j++){
            if(fields[j].c.indexOf(".") != -1){
                fields[j].c = fields[j].c.split(".")[1];
            }
        }
        var col = "";
        for (var i = 0; i < fields.length; i++) {
            if(fields[i].c.indexOf("\"") == 0){
                for(var j = 1 ; j< fields[i].c.length-1 ; j++){
                    col  += fields[i].c[j];
                }
                setMapInput(0, fields[i].t, col, fields[i].t, col, dlg);
                col = "";
                setMapInput(1, "", "", "", "", dlg);
            }
            else{
                setMapInput(0, fields[i].t, fields[i].c, fields[i].t, fields[i].c, dlg);
                setMapInput(1, "", "", "", "", dlg);
            }
        }
        if ($.fn.sortDragByClass) $("#dLeft, #dRight", dlg).sortDragByClass();
    }
}

function getJdbcReaderCallBack(dlg){
    var currentPage = getCurrentPage();
    var consoleInfo = $("#debugConsole_info", currentPage).val();
    var consoleMeta = $("#debugConsole_mata", currentPage).val();

    if (!consoleInfo) {
        alertMsg.info("请先调试任务！");
    } else {
        var check = consoleInfo.substr(0, 4);

        if (check == "运行出错") {
            alertMsg.error("任务调试失败，无法生成来源！");
        } else if (consoleMeta == "[]") {
            alertMsg.info("没有数据，无法生成来源！");
        } else {
            if ($("#debugConsole_out", currentPage).val() != "") {
                var title = "载入来源数据会覆盖原有数据，是否要覆盖？";
                alertMsg.confirm(title, {
                    okCall: function () {
                        var tmp = consoleMeta.substr(1, consoleMeta.length - 2);
                        var tables = tmp.split(".");
                        var i= 0,j=0;
                        //debugger;
                        clearMapAll(dlg);
                        for (; i<tables.length; i++){
                            var fields = tables[i].split(",");
                            if(!fields || fields.length < 3){
                                continue;
                            }
                            var tableName = fields[1];
                            j=2;
                            for (;j<fields.length;j++){
                                setMapInput(0, tableName, fields[j], tableName, fields[j], dlg);
                                setMapInput(1, "", "", "", "", dlg);
                            }
                        }
                        if ($.fn.sortDragByClass) $("#dLeft, #dRight", dlg).sortDragByClass();
                    }
                });
            }
        }
    }
}
//inputType:0 源；1 目标
//清理空mapping
function clearMapAll(dlg){

    var $leftObj = $("#dLeft div",dlg);
    var i = $leftObj.length-1;
    while (i >= 0){
        $($leftObj[i]).remove();
        i--;
    }
    var $rightObj = $("#dRight div",dlg);
    i = $rightObj.length-1;
    while (i >= 0){
        $($rightObj[i]).remove();
        i--;
    }
}

function mappingProcessorCallBack(json, dlg) {
    if (json.message) {
        DWZ.ajaxDone(json);
    }
    if (json.statusCode == DWZ.statusCode.ok) {
        addMappingPro(json, dlg);
        if ("closeCurrent" == json.callbackType) {
            $.pdialog.closeCurrent();
        }

        if (json.retFlag != "yes") {
            return;
        }
        var iTabIndex = 0;
        if ($("#mapIndex", dlg).val() == "0") {
            if (json.jsonData) {
                $(".sortDrag >div", $("#mappingProcessorDiv", dlg)).remove(); //移除所有映射
                var map = json.jsonData;
                for (var i = 0; i < map.length; i++) {
                    setMapInput(0, map[i].st, map[i].sc, map[i].st2, map[i].sc2, dlg);
                    setMapInput(1, map[i].tt, map[i].tc, map[i].tt2, map[i].tc2, dlg);
                    if (map[i].m != null && map[i].m != "") {
                        // 设置字典
                        var divObj = $(".sortDrag >div:eq(" + i + ")", $("#mappingProcessorDiv", dlg));
                        $("#selType", divObj).val(map[i].m);
                    }
                }
                if ($.fn.sortDragByClass) {
                    $("#dLeft, #dRight", dlg).sortDragByClass();
                }
            }
            iTabIndex = 0;
        } else if ($("#mapIndex", dlg).val() == "1") {
            var rsCode = getMappingEditor(dlg.data("id"));
            if (json.mapingData) {
                var mapValue = json.mapingData == "" ? "" : unescapeHTML(json.mapingData);
                rsCode.setValue(mapValue);
            } else {
                rsCode.setValue(json.mapingData);
            }
            setTimeout(function () {
                rsCode.autoFormatAll();
            }, 100);
            iTabIndex = 1;
        }
        // 处理切换
        var options = {};
        options.currentIndex = iTabIndex;
        options.eventType = "abc"; // eventType可以为（非click，hover）任意值都可以，目的使切换失效
        $("#mappingTabs", dlg).tabs(options);
    } else {
        if (json.retFlag != "yes") {
            return;
        }
        //如果切换失败，将mapIndex值还原
        $("#mapIndex", dlg).val($("#mapIndex", dlg).val() == "0" ? "1" : "0");
    }
}

// inputType:0 源；1 目标
function setMapInput(inputType, tableName, colName, tableName2, colName2, dlg) {
    var dispName = "";
    if (tableName2 != "" && colName2 != "")
        dispName = tableName2 + "/" + colName2;
    // 注意字符串拼接如果使用append会导致显示换行
    if (inputType == 0) {
        var sourceStr = "<div style='border:0 solid #B8D0D6;padding:5px;margin:1px;width:350px;overflow:hidden;height:21px;'><table><tr><td>";
        sourceStr += "<input type='text' class='textInput valid' name='mapLeft' value='" + dispName + "' title='" + dispName + "'/>";
        sourceStr += "<input type='hidden' name='sourceTableName' value='" + tableName + "'/>";
        sourceStr += "<input type='hidden' name='sourceColumnName' value='" + colName + "'/>";
        sourceStr += "<input type='hidden' name='sourceTableName2' value='" + tableName2 + "'/>";
        sourceStr += "<input type='hidden' name='sourceColumnName2' value='" + colName2 + "'/>";
        sourceStr += "</td><td>" + $("#selectDict").html() + "</td><td><a id='shwMap'></a></td><td><a href='javascript:;' class='map_drag'></a></td></tr></table></div>";
        $("#dLeft", dlg).append(sourceStr);
    }
    else if (inputType == 1) {
        var cInf = "";
        if (colName != "") {
            //字段名#表类型(表中文名)#字段中文#表名
            cInf = colName + "#" + tableName2 + "#" + colName2 + "#" + tableName;
        }
        var targetStr = "<div style='border:0 solid #B8D0D6;padding:5px;margin:1px;width:270px;overflow:hidden;height:21px;'><table><tr><td>";
        targetStr += "<input type='text' class='textInput valid' name='mapRight' value='" + dispName + "' title='" + dispName + "'/>";
        targetStr += "<input type='hidden' name='targetTableName' value='" + tableName + "'/>";
        targetStr += "<input type='hidden' name='targetColumnName' value='" + colName + "'/>";
        targetStr += "<input type='hidden' name='targetTableName2' value='" + tableName2 + "'/>";
        targetStr += "<input type='hidden' name='targetColumnName2' value='" + colName2 + "'/>";
        targetStr += "<input type='hidden' name='targetColumnInfo' value='" + cInf + "'/>";
        targetStr += "</td><td><a id='shwMap'></a></td><td><a href='javascript:;' class='map_drag'></a></td></tr></table></div>";
        $("#dRight", dlg).append(targetStr);
    }
}

//inputType:0 源；1 目标
//清理空mapping
function clearMapInput(inputType,dlg){
    var ret=0;
    if (inputType == 0) {
        var $obj = $("#dLeft div",dlg);
        var i = $obj.length-1;
        while(i >= 0){
            if($("input[name='mapLeft']",$obj[i]).val() == ""){
                $($obj[i]).remove();
                ret++;
            }
            else{
                break;
            }
            i--;
        }
        return ret;
    }
    else if(inputType == 1){
        var $obj = $("#dRight div",dlg);
        var i = $obj.length-1;
        while(i >= 0){
            if($("input[name='mapRight']",$obj[i]).val() == ""){
                $($obj[i]).remove();
                ret++;
            }
            else{
                break;
            }
            i--;
        }
        return ret;
    }
}



function unescapeHTML(target) {
    // 还原为可被文档解析的HTML标签
    return target.replace(/&quot;/g, '"').replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&amp;/g, "&")
        // 处理转义的中文和实体字符
        .replace(/&#([\d]+);/g, function ($0, $1) {
            return String.fromCharCode(parseInt($1, 10));
        });
}

function onSubmitCheckBox(dlg){
    var tObj = $("#transNoMapping", dlg);
    if (tObj.attr("checked")) {
        tObj.val("1");
    } else {
        tObj.val("");
    }

    var cObj = $("#chk", dlg);
    if (cObj.attr("checked")) {
        cObj.val("CHN");
    } else {
        cObj.val("");
    }
}

function checkInput(obj){
    var dlg = getFormMappingDlg(obj);
    var rsCode = getMappingEditor(dlg.data("id"));
    rsCode.save();
    onSubmitCheckBox(dlg);
    var inputObj = $("input.required", obj);
    var len = inputObj.length;
    for (var i = 0; i < len; i++) {
        if (hasSpecialChar(inputObj[i].value)) {
            alertMsg.error(DWZ.msg("alertHasSpecialChar"));
            return false;
        }
    }
    return validateCallback(obj, function (json) {
        mappingProcessorCallBack(json, dlg);
    });
}

function addMappingPro(json, dlg) {
    var sourceName = $("input[name='bean.dis_name']", dlg).val();
    var content;
    var imgObj;
    var imageUrl;
    // -----------------------------动态添加sortDrag--------------------------------------------------------------------------------------
    var infor = (json.uuid).split(",");
    var resId = infor[0];
    var saveOrUpdate = infor[1];
    var sort = infor[2];
    var flowId = infor[3];
    var flowDiv = $("div[name='" + flowId + "']");
    // 保存则增加div
    if (saveOrUpdate == "0") {
        if (sort != null && sort != "") {
            if (sort >= 1000 && sort < 2000) {
                content = $("#sourceProcess",flowDiv);
                if ($("div.sortDrag", content).children().length == 0) {
                    imgObj = $("#source_img",flowDiv);
                    imageUrl = "/styles/dee/themes/images/source_h.jpg";
                }
            } else if (sort >= 2000 && sort < 3000) {
                content = $("#exchangeProcess",flowDiv);
                if ($("div.sortDrag", content).children().length == 0) {
                    imgObj = $("#exchange_img",flowDiv);
                    imageUrl = "/styles/dee/themes/images/exchange_h.jpg";
                }
            } else if (sort >= 3000) {
                content = $("#targetProcess",flowDiv);
                if ($("div.sortDrag", content).children().length == 0) {
                    imgObj = $("#target_img",flowDiv);
                    imageUrl = "/styles/dee/themes/images/target_h.jpg";
                }
            } else {
                alertMsg.error("sort错误!");
                return;
            }
            var sortDragName = content.attr("id");
            $("div.sortDrag", content).append("<div id=" + resId + " name=" + sortDragName + " isDrag=\"false\" style=\"border:1px solid #B8D0D6;padding:5px;margin:5px;height:20px;display:none;cursor:pointer;position:relative;\"><h2>" + sourceName + "</h2></div>");
            $("div.sortDrag >div:last", content).delay(500).slideDown("slow");
            if (imgObj != null && imageUrl != null) {
                imgObj.attr("src", imageUrl);
            }
            if ($.fn.sortDragOne) {
                $("div.sortDrag", content).sortDragOne(null, $("div.sortDrag >div:last", content));
            }
        }
        else {
            alertMsg.error("sort为空！");
        }
    }
    // 修改则刷新
    if (saveOrUpdate == "1") {
        $("#" + resId + "").html("<h2>" + sourceName + "</h2>");
    }
}

function getMappingDlg(obj) {
    var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}

function getFormMappingDlg(obj) {
    var resourceId = $(obj).find("input[name='resourceId']").val();
    if (!resourceId) {
        resourceId = "resourceDlg";
    }
    return $("body").data(resourceId);
}

function getMappingEditor(key) {
    if (g_mappingEditors) {
        for (var index in g_mappingEditors) {
            if (index == key) {
                return g_mappingEditors[index];
            }
        }
    }
    return null;
}