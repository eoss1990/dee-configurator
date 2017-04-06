$(function(){
	initDictShow();
    $('#dictType').change(function(){
    	chgDictPanel($(this).val());
    });
});
function initDictShow(){
	if($('#dictTypeParam').val() == ""){
		chgDictPanel("jdbc");
	}
	else
		chgDictPanel($('#dictTypeParam').val());
//	else if($('#dictTypeParam').val() == "jdbc"){
//	}
//	else if($('#dictTypeParam').val() == "static"){
//	}
//	else if($('#dictTypeParam').val() == "func"){
//	}
//	chgDictPanel($('#dictTypeParam').val());
}

function chgDictPanel(value){
	if(value == "jdbc"){
		$("#jdbcspan").show();
		$("#staticspan").hide();
		$("#funcspan").hide();
	}
	else if(value == "static"){
		$("#jdbcspan").hide();
		$("#staticspan").show();
		$("#funcspan").hide();
	}
	else if(value == "func"){
		$("#jdbcspan").hide();
		$("#staticspan").hide();
		$("#funcspan").show();
	}
	setNewPanel(value);
}

function setNewPanel(value){
	if(value == "jdbc"){
		$(":input",$("#jdbcspan")).addClass("required");
		$(":input",$("#staticspan")).removeClass("required");
		$(":input",$("#funcspan")).removeClass("required");
	}
	else if(value == "static"){
		$(":input",$("#jdbcspan")).removeClass("required");
		$(":input",$("#staticspan")).addClass("required");
		$(":input",$("#funcspan")).removeClass("required");
	}
	else if(value == "func"){
		$(":input",$("#jdbcspan")).removeClass("required");
		$(":input",$("#staticspan")).removeClass("required");
		$(":input",$("#funcspan")).addClass("required");
	}
}


function dictShowDone(json){
	DWZ.ajaxDone(json);
	if (json.statusCode == DWZ.statusCode.ok){
		if (json.navTabId){ //把指定navTab页面标记为需要“重新载入”。注意navTabId不能是当前navTab页面的
			navTab.reloadFlag(json.navTabId);
		}		
		if ("closeCurrent" == json.callbackType) {
			$.pdialog.closeCurrent(); 
//			setTimeout(function(){navTab.closeCurrentTab(json.navTabId);}, 100);
		}		
	}
}

function checkInput(obj){
	var inputObj = $("input.required",obj);
	var len = inputObj.length;
//	$("input.required",$("#frmDsShow")).each(function(){
//		alert($(this).val());
//		if(hasSpecialChar($(this).val())){
//            alertMsg.error("录入框中含特殊字符，不能保存");
//            isHas = 1;
// 			return false;
//		}
//	});
	for(var i=0;i<len;i++){
		if(hasSpecialChar(inputObj[i].value)){
			alertMsg.error(DWZ.msg("alertHasSpecialChar"));
			return false;
		}
	}
	return validateCallback(obj,dictShowDone);
}

