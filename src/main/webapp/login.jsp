<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="ui.title" /></title>

<link href="/styles/management/themes/default/style.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="/styles/management/themes/css/core.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="/styles/dee/themes/css/login/login.css" rel="stylesheet" type="text/css" />

<script src="/styles/management/js/jquery-1.7.2.js" type="text/javascript"></script>
<script src="/styles/management/js/dwz.core.js" type="text/javascript"></script>
<script src="/styles/management/js/dwz.ui.js" type="text/javascript"></script>
<script src="/styles/management/js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="/styles/dee/js/login/login.js" type="text/javascript"></script>
<script type="text/javascript">
if ("${sessionScope.isLogin}") {
    top.location.href = "/dee/login!welcome.do";
}
</script>
</head>

<body>
    <div class="warp">
        <div class="login_box_title">
            
        </div>
        <form method="post" action="/dee/login!login.do" onsubmit="formSubmit();return false;">
          <div class="login_box">
              <div class="login_box_labelname">Dee管理员:</div>
              <input type="text" id="username" name="username" readonly="readonly" value="dee_admin" class="login_box_name" />
              <div class="login_box_labelpassword">Dee登录口令:</div>
              <input type="password" id="pwd" name="pwd" value="" class="login_box_password" />
              <input type="hidden" id="errorMsg" value="${errorMsg}" />
              <input type="submit" id="loginBtn" value="登 录" class="login_submit" />
          </div>
        </form>
        <div class="login_logo">
        </div>
    </div>
</body>
</html>