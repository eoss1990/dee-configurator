$(function() {
	var regFlagVal=$("#inputRegFlag").val();
	if(regFlagVal!="true")  //add by yangyu 
	$("a.close").remove();
    $("a.maximize").remove();
    $('#confirmSerial').click(function() {
        submitSerial();
    });
    $('#getKeycode').click(function() {
    	alertMsg.confirm("此操作将更新授权信息！", {
    		okCall: getKeycode
    	});
    });
    $("#errorMsgShow").html($("#errorMsg").val());
});

function getKeycode() {
	var AuthorizeDate = $('#AuthorizeDate').val();
    var Deadline = $('#Deadline').val();
    if(!$.trim(AuthorizeDate)) {
    	alertMsg.info("请输入授权日期！");
        $('#AuthorizeDate').focus();
        return;
    }
    if(!$.trim(Deadline)) {
    	alertMsg.info("请输入用户类型！");
        $('#Deadline').focus();
        return;
    }
    $('#getKeycode').attr("disabled","disabled");
    window.open("/dee/serial!getKeycode.do?AuthorizeDate="+AuthorizeDate+"&Deadline="+Deadline);
}

function submitSerial() {
	var AuthorizeDate = $('#AuthorizeDate').val();
    var Deadline = $('#Deadline').val();
    var SerialNumber = $('#SerialNumber').val();
    if (!$.trim(AuthorizeDate)) {
    	alertMsg.info("请输入授权日期！");
        $('#AuthorizeDate').focus();
        return;
    }
    if (!$.trim(Deadline)) {
    	alertMsg.info("请输入授权期限！");
        $('#Deadline').focus();
        return;
    }
    if (!$.trim(SerialNumber)) {
    	alertMsg.info("请输入授权码！");
        $('#SerialNumber').focus();
        return;
    }
    $.ajax({
		async: false,
		type:"post",
		dataType: "json",
		data:'AuthorizeDate='+AuthorizeDate+"&Deadline="+Deadline+"&SerialNumber="+SerialNumber,
		url: "dee/serial!doSerial.do",
		success: function(result) {
			if (result.statusCode == 200) {
				alertMsg.correct(result.message);
				setTimeout(function() {
					top.location.href = "/dee/login!welcome.do";
				}, 1000); 
			} else {
				alertMsg.info(result.message);
				$("#errorMsgShow").html(result.message);
			}
		},
		error: function(result) {
			alert("error:" + result);
		}	
	});
}
