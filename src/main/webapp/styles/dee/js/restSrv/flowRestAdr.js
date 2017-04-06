$(function(){
	$("#btnRestCopy").live('click', function() {
		if($("#rest_url",$("#restFlow")).val() == ""){
			alertMsg.error("服务地址为空，不能复制，请选择任务！");
			return;
		}
		window.clipboardData.setData("Text",$("#rest_url",$("#restFlow")).val()); 
		alertMsg.correct("复制成功");
	});
});

