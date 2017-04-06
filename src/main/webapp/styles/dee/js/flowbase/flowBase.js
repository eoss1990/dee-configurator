$(document).ready(function(){
	if ($.fn.sortDrag) $("div.sortDrag", $("#flowbaseTab", getCurrentPage())).sortDragAct();
	
});

$(".back").click(function () {
    var currentPage = getCurrentPage();
    if ($("#panelBase .panelContent", currentPage).height() == 0) {
        $(".expandable", currentPage).click();
        var uppanel = $("#panelBase .panelContent", currentPage).height();
        var dheight = $("#inpanel", currentPage).height();
        var outheight = $("#outpanel", currentPage).height();
        $("#inpanel", currentPage).css("height", "" + (dheight - uppanel) + "px");
        $("#outpanel", currentPage).css("height", "" + (outheight - uppanel) + "px");
    }
});

/*鼠标移动到sortDrag上增加编辑和删除按钮*/
$(".sortDrag >div",$("#flowbaseTab")).live('mouseover', function() {
	var de = $("a.proDel",$(this));
	if(!de.length)
		$("<a title=\"编辑\"  class=\"proEdit\"></a><a title=\"删除\"  class=\"proDel\"></a>").appendTo($(this));
});
/*鼠标离开sortDrag范围移除编辑和删除按钮*/
$(".sortDrag >div",$("#flowbaseTab")).live('mouseleave', function() {
	var del = $(this);
	$("a.proDel",del).remove();$("a.proEdit",del).remove();
});
/*打开适配器*/
$("a.proEdit",$("#flowbaseTab")).die().live('click', function() {
	var flowId = $("#uid", getCurrentPage()).val();
	var resourceId = $(this).parent().attr("id");
//	var resourceName = $(this).parent().attr("name");
	var title = $(this).prev().html();
	if(flowId!=null&&flowId!=""&&resourceId!=null&&resourceId!="")
	{
		var url="/dee/adapterall!edit.do?flowId="+flowId+"&resourceId="+resourceId;
//		var dlgId="resourceDlg";
//		var title=null;
//		if(resourceName=="sourceProcess")
//			title="编辑来源配置";
//		else if(resourceName=="exchangeProcess")
//			title="编辑转换配置";
//		else if(resourceName=="targetProcess")
//			title="编辑目标配置";
		var options={width:850,height:550,maxable:false};
        var flowName = $(".unitBox.page:visible input[name='real_flow_name']").val();
		$.pdialog.open(url, resourceId, title + "(" + flowName + ")", options);
	}
	else
	{
		alertMsg.error("信息异常，任务不存在！");
	}
});
/*删除适配器并且修改image*/
$("a.proDel", $("#flowbaseTab")).die().live('click', function () {
    var currentPage = getCurrentPage();

	var div = $(this).parent();
	var sortDrag = div.parent();
	var flowId = $("#uid", currentPage).val();
    var resId = div.attr("id");
    var imgObj;
    var imgUrl;
    if (flowId != null && flowId != "" && resId != null && resId != "") {
        function _doPost() {
            var order = "{'flowId':'" + flowId + "','resId':'" + resId + "'}";
            $.ajax({
                async: false,
                type: "post",
                dataType: "json",
                data: {order: order},
                url: "/dee/flowbase!deleteResource.do",
                cache: false,
                success: function (json) {
                    if (json.statusCode == "200") {
                        div.remove();
                        if (sortDrag.children().length == 0) {
                            var imageUrl;
                            if (div.attr("name") == "sourceProcess") {
                                imgObj = $("#source_img", currentPage);
                                imageUrl = "/styles/dee/themes/images/source_l.jpg";
                            } else if (div.attr("name") == "exchangeProcess") {
                                imgObj = $("#exchange_img", currentPage);
                                imageUrl = "/styles/dee/themes/images/exchange_l.jpg";
                            } else if (div.attr("name") == "targetProcess") {
                                imgObj = $("#target_img", currentPage);
                                imageUrl = "/styles/dee/themes/images/target_l.jpg";
                            }
                            imgObj.attr("src", imageUrl);
                        }
                        DWZ.ajaxDone(json);
                    }
                    else {
                        alertMsg.error("后台删除失败");
                    }
                },
                error: function () {
                    alertMsg.error("ajax删除失败！");
                }
            });
        }

        alertMsg.confirm("是否要删除适配器？", {okCall: _doPost});
    }
});

function flowBaseFormDone(json) {
   
    DWZ.ajaxDone(json);
    if (json.statusCode == DWZ.statusCode.ok) {
        if (json.navTabId){ //把指定navTab页面标记为需要“重新载入”。注意navTabId不能是当前navTab页面的
            navTab.reloadFlag(json.navTabId);
        } else { //重新载入当前navTab页面
            var $pagerForm = $("#pagerForm", navTab.getCurrentPanel());
            var args = $pagerForm.size()>0 ? $pagerForm.serializeArray() : {};
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
       
        var currentPage = getCurrentPage();
        var name = $("input[name='flow_name']", currentPage).val();
        $("input[name='real_flow_name']", currentPage).val(name);
        if (json.uuid) {
            $("#uid", currentPage).val(json.uuid);
            var action = "/dee/flowbase!debug.do?flowId=" + json.uuid;
            $("#debug", currentPage).attr("href", action);
            $(".pageFormContent", currentPage).attr("name", json.uuid);

        }
    }
}

function treeBringBack()
{
	var url="/dee/flowbase!treeBringBack.do";
	var dlgId="flowtype_tree";
	var title="交换任务分类";
	var options={width:400,height:400};
	$.pdialog.open(url, dlgId, title, options);
}

function submitFlowBaseForm(obj) {
    var currentPage = getCurrentPage();
    var flow_type_name = $('#flow_type_name', currentPage);
    if ($.trim(flow_type_name.val()) == "") {
        alertMsg.error("请选择交换任务分类!");
        return false;
    }
    if (hasSpecialChar($("input[name='flow_name']", $("#flowBaseForm", currentPage)).val())) {
        alertMsg.error(DWZ.msg("alertHasSpecialChar"));
        return false;
    }
    var form = $("#flowBaseForm", currentPage);
    form.submit();
}

function getCurrentPage() {
    return $(".unitBox.page:visible");
}