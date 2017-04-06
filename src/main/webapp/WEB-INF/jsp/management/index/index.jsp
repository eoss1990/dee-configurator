<!DOCTYPE  html PUBLIC "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- <meta http-equiv="X-UA-Compatible" content="IE=8" /> -->
<title><s:text name="ui.title" /></title>


<script src="/styles/dee/js/resource/script-data.js" type="text/javascript"></script>




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
<script src="/styles/management/js/jquery.ztree.exhide-3.5.js" type="text/javascript"></script>

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
var editor = [];

$(function(){
	DWZ.init("/styles/management/dwz.frag.xml", {
		loginUrl:"/management/index!login.do",
		loginTitle:"Login",	// 弹出登录对话框
		debug:false,	// 调试模式 【true|false】
		callback:function(){
			initEnv();
			$("#themeList").theme({themeBase:"/styles/management/themes"});
			setTimeout(openFlowNavTab, 100);

  			 /*if ($.trim("${errorMsg}")) {
		        var options = { width:"600", height:"300", mask:true, maxable:false, closable:false};
		        $.pdialog.open("/dee/serial!viewReg.do", "serial_reg", "注册授权", options);
		    }*/
		}
	});
});
//清理浏览器内存,只对IE起效,FF不需要
if ($.browser.msie) {
	window.setInterval("CollectGarbage();", 10000);
}

function closeDee(event) {
//    debugger;
//	event.preventDefault();
//	event.stopPropagation();
	alertMsg.confirm("是否要离开此页面？", {okCall: returnToLogin,cancelCall:returnFalse});
	} 
	
function returnToLogin()
{
	window.location.href = "/dee/login!logout.do";
	}
	
function returnFalse()
{
	return false;}

</script>
</head>

<body scroll="no">
	<div id="layout">
		<div id="header">
			<div class="headerNav">
				<!-- <a class="logo" href="javascript:void(0);">Logo</a> -->
				<img src="/styles/dee/themes/css/login/images/dee_top_2.1sp1.png">
				<ul class="nav">
					<li style="background-image: none;"><a href="/dee/version!view.do" target="dialog" rel="w_about" title="关于" width="500" height="270"><img src="/styles/dee/themes/images/ic_about_dee_2.1sp1.png"></img></a></li>
					<li><a href="javascript:void(0)" title="退出系统" onclick="javascript:closeDee(event);"><img src="/styles/dee/themes/images/ic_quite.png"></img></a></li>
				</ul>
				<ul class="themeList" id="themeList" style="display:none;">
					<li theme="default"><div class="selected">blue</div></li>
					<li theme="green"><div>green</div></li>
					<li theme="purple"><div>purple</div></li>
					<li theme="silver"><div>silver</div></li>
					<li theme="azure"><div>天蓝</div></li>
				</ul>
			</div>
            <div id="navMenu">
                <ul>
                    <li class="selected"><a href="/dee/index!serial.do?num=0" id="navMenu_flow"><span>任务管理</span></a></li>
                    <li><a href="/dee/index!serial.do?num=1" id="navMenu_base"><span>基础设置</span></a></li>
                    <li><a href="/dee/index!serial.do?num=2" id="navMenu_sys"><span>系统设置</span></a></li>
                    <li><a href="/dee/index!serial.do?num=3" id="navMenu_func"><span>扩展功能</span></a></li>
                </ul>
            </div>
		</div>
		
		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse"><div></div></div>
				</div>
			</div>
			<div id="sidebar">
				<div class="toggleCollapse"><h2>
                    <span class="back_img">&nbsp;</span>
                    <span class="back_text">任务管理</span></h2><div>collapse</div>
                </div>
                <jsp:include page="index-accordion.jsp"></jsp:include>
			</div>
		</div>
		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent"><!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
						<ul class="navTab-tab">
							<li tabid="main" class="main" style="display:none;"><a href="javascript:void(0)"><span><span class="home_icon">My Home</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div><!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
					<div class="tabsRight">right</div><!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li style="display:none;"><a href="javascript:void(0)">My Home</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div></div>
				</div>
			</div>
		</div>

	</div>
	
	<div id="footer"><s:text name="ui.copyrights" /></div>
</body>
</html>