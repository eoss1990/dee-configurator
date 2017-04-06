<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><s:text name="ui.title" /></title>

    <link href="/styles/management/themes/default/style.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="/styles/management/themes/css/core.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="/styles/management/themes/css/print.css" rel="stylesheet" type="text/css" media="print"/>
    <link href="/styles/uploadify/css/uploadify.css" rel="stylesheet" type="text/css" />

    <style type="text/css">
        #header {
            height: 85px
        }

        #leftside,#container,#splitBar,#splitBarProxy {
            top: 90px
        }
    </style>

    <script src="/styles/management/js/speedup.js" type="text/javascript"></script> <!--加速-->
    <script src="/styles/management/js/jquery-1.7.2.js" type="text/javascript"></script><!--jquery-->
    <script src="/styles/management/js/jquery.cookie.js" type="text/javascript"></script><!--记住所选风格-->
    <script src="/styles/management/js/jquery.validate.js" type="text/javascript"></script><!--验证-->

    <script src="/styles/management/js/jquery.bgiframe.js" type="text/javascript"></script>
    <script src="/styles/xheditor/xheditor-1.1.9-zh-cn.min.js" type="text/javascript"></script>
    <script src="/styles/uploadify/scripts/swfobject.js" type="text/javascript"></script>
    <script src="/styles/uploadify/scripts/jquery.uploadify.v2.1.0.js" type="text/javascript"></script>

    <script src="/styles/management/js/jquery.ztree.core-3.5.js" type="text/javascript"></script>
    <script src="/styles/management/js/dee.ztree.js" type="text/javascript"></script>
    <script src="/styles/management/js/jquery.ztree.excheck-3.5.js" type="text/javascript"></script>

    <script src="/styles/management/js/dwz.core.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.util.date.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.validate.method.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.regional.zh.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.barDrag.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.drag.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.tree.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.accordion.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.ui.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.theme.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.switchEnv.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.alertMsg.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.contextmenu.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.navTab.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.tab.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.resize.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.dialog.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.dialogDrag.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.sortDrag.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.cssTable.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.stable.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.taskBar.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.ajax.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.pagination.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.database.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.datepicker.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.effects.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.panel.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.checkbox.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.history.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.combox.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.print.js" type="text/javascript"></script>
    <script src="/styles/management/js/dwz.regional.zh.js" type="text/javascript"></script>
    <script src="/styles/management/js/index/index.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(function(){
            DWZ.init("/styles/management/dwz.frag.xml", {
                debug:false,
                callback:function(){
                    initEnv();
                    if ($.trim("${errorMsg}")) {
                        var options = { width:"600", height:"300", mask:true, maxable:false, closable:false};
                        $.pdialog.open("/dee/serial!viewReg.do", "serial_reg", "注册授权", options);
                    }
                }
            });
        });

    </script>
</head>

<body scroll="no">
<div id="layout">
    <div id="container" style="display:none;">
        <div id="navTab" class="tabsPage">
            <div class="tabsPageHeader">
                <div class="tabsPageHeaderContent">
                    <ul class="navTab-tab">
                    </ul>
                </div>
                <div class="tabsLeft">left</div>
                <div class="tabsRight">right</div>
                <div class="tabsMore">more</div>
            </div>
        </div>
    </div>
</div>
<input type="hidden" id="errorMsg" value="${errorMsg}"/>
</body>
</html>