$(document).ready(function() {
	DWZ.init("/styles/management/dwz.frag.xml", {
		debug:false,
		callback:function() {
			resizeWeb();
		}
	});
});

function formSubmit() {
	if (!$("#pwd").val()) {
		alertMsg.error("密码不能为空！");
		$("#pwd").focus();
		return;
	}
	$.ajax({
		async: false,
		type:"post",
		dataType: "json",
		data: {
			"username": $("#username").val(),
			"pwd": $("#pwd").val()
		},
		url: "/dee/login!checkLogin.do",
		success: function(result) {
			if (result.statusCode == 200) {
				top.location.href = "/dee/login!welcome.do";
			} else if (result.statusCode == 300) {
				alertMsg.error(result.message);
				$("#pwd").focus();
			}
		}, error: function(result) {
			alertMsg.error("网络异常，请检查网络连接！");
			$("#pwd").focus();
		}
	});
}

$(window).resize(function () {
    resizeWeb();
});

function resizeWeb() {
    //获取浏览器的宽高
    var webWidth = document.body.clientWidth;
    var webHeight = document.body.clientHeight;

    //计算登陆框上层文字位置
    $(".login_box_title").css("left", (webWidth - 280) / 2 + "px");
    $(".login_box_title").css("top", (webHeight - 387) / 2 + "px");

    //计算登陆框位置
    $(".login_box").css("left", (webWidth - 394) / 2 + "px");
    $(".login_box").css("top", (webHeight - 247) / 2 + "px");

    //计算底部logo位置
    $(".login_logo").css("left", (webWidth - 200) / 2 + "px");
}