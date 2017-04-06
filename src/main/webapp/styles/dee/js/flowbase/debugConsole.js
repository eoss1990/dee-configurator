function debugDone(json){
    if (json.statusCode == DWZ.statusCode.ok){
        if (json.navTabId){ //把指定navTab页面标记为需要“重新载入”。注意navTabId不能是当前navTab页面的
            navTab.reloadFlag(json.navTabId);
        } else { //重新载入当前navTab页面
            var $pagerForm = $("#pagerForm", navTab.getCurrentPanel());
            var args = $pagerForm.size()>0 ? $pagerForm.serializeArray() : {}
            navTabPageBreak(args, json.rel);
        }

        if ("closeCurrent" == json.callbackType) {
            setTimeout(function(){navTab.closeCurrentTab(json.navTabId);}, 100);
        } else if ("forward" == json.callbackType) {
            navTab.reload(json.forwardUrl);
        } else if ("forwardConfirm" == json.callbackType) {
            alertMsg.confirm(json.confirmMsg || DWZ.msg("forwardConfirmMsg"), {
                okCall: function(){
                    navTab.reload(json.forwardUrl);
                }
            });
        } else {
            navTab.getCurrentPanel().find(":input[initValue]").each(function(){
                var initVal = $(this).attr("initValue");
                $(this).val(initVal);
            });
        }
        /*将调试结果回写到界面*/
        var currentPage = getCurrentPage();

        if(json.jsonData)
            updateAdapter(json.jsonData);

        if(json.error=="true")
        {
            //$("#blockpanel",$("#debugResult", currentPage)).html("<a id='keyWord' href='#' style='text-decoration:underline;color:red' onclick='openFaq()'></a>")
            //$("#keyWord",currentPage).html(unescapeHTML(json.console_info));
            $("#blockpanel",$("#debugResult", currentPage)).html("<textarea id='debugConsole_info' class='textInput readonly valid' name='debugConsole_info' style='width:99%;height:90%;' readonly='true'></textarea><img onclick='openFaq()' src='/styles/dee/themes/images/exceptionFaq.png'/><a id='keyWord' href='#' style='text-decoration:underline;color:red;' onclick='openFaq()'>点击搜索异常</a>");
            $("#debugConsole_info",$("#debugResult", currentPage)).val(unescapeHTML(json.console_info));

        }else
        {
            $("#blockpanel",$("#debugResult", currentPage)).html("<div style='height:100%;'><textarea id='debugConsole_info' class='textInput readonly valid' name='debugConsole_info' style='width:99%;height:97%;' readonly='true'></textarea></div>");
            $("#debugConsole_info",$("#debugResult", currentPage)).val(unescapeHTML(json.console_info));
        }
        $("#debugConsole_out",$("#debugResult", currentPage)).val(formatXml(unescapeHTML(json.console_out)));
        $("#debugConsole_args",$("#debugResult", currentPage)).val(unescapeHTML(json.console_args));
        $("#debugConsole_context",$("#debugResult", currentPage)).val(unescapeHTML(json.console_context));
        $("#debugConsole_mata", currentPage).val(unescapeHTML(json.console_mata));
        $("#debug_infor", currentPage).trigger("click");

        if(json.debugFlag=="T")
        {
            var url="/dee/flowbase!scriptDebug.do";
            var options={width:850,height:550,maxable:false};
            var flowName = $(".unitBox.page:visible input[name='real_flow_name']").val();
            $.pdialog.open(url, "scriptDebug", flowName + "(参数调试)", options);
        }
    }
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

$(document).ready(function (){
    var outheight = $("#outpanel").height();
    var inheight = $("#inpanel").height();
    var uppanel =$("#panelBase .panelContent").height();
    var pageheight = $("#pageheight").height();
    var debugheight = $("#debugheight").height();

    $(".collapsable").click(function() {

        var debugConsole_info = $("#debugConsole_info").height();
        var outdiv = $("#outdiv").height();
        $(".panelContent").fadeIn("2000");
        var debugresult = $("#debugResult").height();
        var outheightchange = $("#outpanel").height();
        var inheightchange = $("#inpanel").height();
        var blockpanel = $("#blockpanel").height();
        if(outheight != outheightchange && inheight != inheightchange){

            $("#inpanel").css("height",""+(inheightchange-uppanel)+"px");
            $("#outpanel").css("height",""+outheight+"px");
            $("#outdiv").css("height",""+(outdiv-uppanel)+"px");
            $("#debugResult").css("height",""+(debugresult-uppanel)+"px");
            $("#debugConsole_info").css("height",""+(debugConsole_info-uppanel)+"px");
            $("#pageheight").css("height",""+pageheight+"px");
            $("#debugheight").css("height",""+debugheight+"px");
            $("#blockpanel").css("height",""+(blockpanel-uppanel)+"px");
        }

    });

});


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

function submitDebug() {

    var currentPage = getCurrentPage();

    var outdiv = $("#outdiv", currentPage).height();
    var dheight = $("#inpanel", currentPage).height();
    var outheight = $("#outpanel", currentPage).height();
    var uppanel = $("#panelBase .panelContent", currentPage).height();

    if (uppanel != 0){
        $(".collapsable", currentPage).click();
        $("#inpanel", currentPage).css("height", "" + (dheight + uppanel) + "px");
        $("#outpanel", currentPage).css("height", "" + (outheight + uppanel) + "px");
        var blockpanel = $("#blockpanel", currentPage).height();
        var outdivchange = $("#outdiv", currentPage).height();
        var debugResultchange = $("#debugResult", currentPage).height();
        var debugConsole_infochange = $("#debugConsole_info", currentPage).height();
        if (outdivchange == outdiv) {
            $("#outdiv", currentPage).css("height", "" + (outdivchange + uppanel) + "px");
            $("#debugResult", currentPage).css("height", "" + (debugResultchange + uppanel) + "px");
            $("#debugConsole_info", currentPage).css("height", "" + (debugConsole_infochange + uppanel) + "px");
            $("#blockpanel", currentPage).css("height", "" + (blockpanel + uppanel) + "px");
        }
    }


    var uuid = $("#uid", currentPage).val();
    if (uuid != null && uuid != "") {
        if (hasSpecialChar(getGroupItems("debugRes", "params"))) {
            alertMsg.error(DWZ.msg("alertHasSpecialChar"));
            return false;
        }
        $("#debugRes", currentPage).submit();
    }
    else {
        alertMsg.error("请先保存基础信息！");
    }
}

function getCurrentPage() {
    return $(".unitBox.page:visible");
}

function openFaq(){
    var keyWord = $("#debugConsole_info",getCurrentPage()).val();
    var tmp=window.open("http://www.baidu.com/s?wd="+keyWord);
}

function updateAdapter(json){
    var currentPage = getCurrentPage();
    var form = $("#flowbaseTab",currentPage);
    var list = $("[name='adapterState']",$(form));
    for(var i=0;i<list.length;i++){
        $(list[i]).remove();
    }

    for(var data in json)
    {
        if(json[data] == "T")
        {
            $("<a  name=\"adapterState\" title=\"调试成功\" class=\"adapterSuccess\"></a>").appendTo($("#"+data,currentPage));
        }
        else
        {
            $("<a  name=\"adapterState\" title=\"调试失败\" class=\"adapterFalse\"></a>").appendTo($("#"+data,currentPage));
        }
    }
}