//function submitForm()
//{
//var ac = "demo!addFlow.do?callbackType=closeCurrent&uid="+$("input[name='uid']").val();
//$("#addFlowForm").attr("action", ac);
//}

function processDone(json) {
//	var sourceName = $("input[name='bean.dis_name']").val();
    var sourceName = null;
    var content;
    var imgObj;
    var imageUrl;

    DWZ.ajaxDone(json);
    if (json.statusCode == DWZ.statusCode.ok) {
//		------------------------动态添加sortDrag--------------------------------------------------------------------------------------
        var infor = (json.uuid).split(",");
        var resId = infor[0];
        var saveOrUpdate = infor[1];
        var sort = infor[2];
        var flowId = infor[3];
        var flowDiv = $("div[name='" + flowId + "']");
        if ($("#uid", flowDiv).val() != flowId) {
            if ("closeCurrent" == json.callbackType) {
                $.pdialog.closeCurrent();
            }
            return;
        }

        /* 保存则增加div */
        if (saveOrUpdate == "0") {
            var dlg = $("body").data("resourceDlg");
            sourceName = $("input[name='bean.dis_name']", dlg).val();
            if (sort != null && sort != "") {
                if (sort >= 1000 && sort < 2000) {
                    content = $("#sourceProcess", flowDiv);
                    if ($("div.sortDrag", content).children().length == 0) {
                        imgObj = $("#source_img", flowDiv);
                        imageUrl = "/styles/dee/themes/images/source_h.jpg";
                    }
                }
                else if (sort >= 2000 && sort < 3000) {
                    content = $("#exchangeProcess", flowDiv);
                    if ($("div.sortDrag", content).children().length == 0) {
                        imgObj = $("#exchange_img", flowDiv);
                        imageUrl = "/styles/dee/themes/images/exchange_h.jpg";
                    }
                }
                else if (sort >= 3000) {
                    content = $("#targetProcess", flowDiv);
                    if ($("div.sortDrag", content).children().length == 0) {
                        imgObj = $("#target_img", flowDiv);
                        imageUrl = "/styles/dee/themes/images/target_h.jpg";
                    }
                }
                else {
                    alertMsg.error("sort错误!");
                    return;
                }
                var sortDragName = content.attr("id");
                $("div.sortDrag", content).append("<div id=" + resId + " name=" + sortDragName + " isDrag=\"false\" style=\"border:1px solid #B8D0D6;padding:5px;margin:5px;height:20px;display:none;cursor:pointer;position:relative;\"><h2>" + sourceName + "</h2></div>");
                $("div.sortDrag >div:last", content).delay(500).slideDown("slow");
                if (imgObj != null && imageUrl != null)
                    imgObj.attr("src", imageUrl);
                if ($.fn.sortDragOne) $("div.sortDrag", content).sortDragOne(null, $("div.sortDrag >div:last", content));

            }
            else
                alertMsg.error("sort为空！");
        }
        /* 修改则刷新 */
        if (saveOrUpdate == "1") {
            var dlg = $("body").data(resId);
            sourceName = $("input[name='bean.dis_name']", dlg).val();
            $("#" + resId + "").html("<h2>" + sourceName + "</h2>");
        }
        /*------------------------------------------------------------------------------------------------------------------------*/
        if ("closeCurrent" == json.callbackType) {
            $.pdialog.closeCurrent();
        }
    }
}