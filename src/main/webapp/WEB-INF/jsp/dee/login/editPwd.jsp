<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>

<div class="pageContent">
    <form id="pwdForm" method="post" action="/dee/login!editPwd.do" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone)">
        <div class="pageFormContent" layoutH="58">
            <div class="unit">
                <label>访问用户：</label>
                <label>dee_admin</label>
            </div>
            <div class="unit">
                <label>原密码：</label>
                <input type="password" id="oldPwd" name="userPwd.oldPwd" size="30" class="required" />
            </div>
            <div class="unit">
                <label>新密码：</label>
                <input type="password" id="newPwd" name="userPwd.newPwd" size="30" class="alphanumeric required"/>
            </div>
            <div class="unit">
                <label>确认新密码：</label>
                <input type="password" id="rnewPwd" name="userPwd.rnewPwd" size="30" equalTo="#newPwd" class="alphanumeric required"/>
            </div>
        </div>
        <div class="formBar">
            <ul>
                <li><div class="buttonActive"><div class="buttonContent"><button type="button" id="changePwdBtn">确定</button></div></div></li>
                <li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
            </ul>
        </div>
    </form>
</div>

<script type="text/javascript">
$(document).ready(function() {
	$("#changePwdBtn").click(function() {
		var oldPwd = $("#oldPwd").val();
		if ($.trim(oldPwd) == "") {
			alertMsg.error("请输入原密码！");
		} else if ($.trim($("#newPwd").val()) == "") {
			alertMsg.error("请输入新密码！");
		} else if ($("#rnewPwd").val() != $("#newPwd").val()) {
			alertMsg.error("2次输入新密码不一致！");
		} else {
			$("#pwdForm").submit();
		}
	});
});
</script>


