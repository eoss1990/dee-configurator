$(function(){
	initDsShow();
	
    $('#dsType').change(function(){
    	chgPanel($(this).val());
    });
    //驱动Driver 绑定点击事件
    $('#dsDrv').change(function(){
        changeTemplate($(this).val());
    });
    //A8元数据 绑定点击事件
    $('#a8metajdbc').click(function(){
    	chgJdbcChk();
    });
    $('#a8metajndi').click(function(){
    	chgJndiChk();
    });
    //测试连接 绑定点击事件
    $('#testBtnCon').click(function(){
    	testCon();
    });
    //保存 绑定点击事件
    $('#btnDsSave').click(function(){
    	saveDs();
    });
});

function initDsShow(){
	//debugger;
	if($('#dsTypeParam').val() == ""){
		chgPanel("jdbc");
		changeTemplate("mysql");
		chgJdbcChk();
	}
	else if($('#dsTypeParam').val() == "jdbc"){
		chgPanel("jdbc");
	    var driverStr = $('#driver').val();
	    if(driverStr != '') {//新建数据源时没有值直接给一个mysql的模板
	        if(driverStr.indexOf('mysql') > -1) {
	            $("#dsDrv option[value='mysql']").attr("selected", 'selected');
	        } else if(driverStr.indexOf('jtds') > -1) {
	            $("#dsDrv option[value='sqlserver']").attr("selected", 'selected');
	        } else if(driverStr.indexOf('oracle') > -1) {
	            $("#dsDrv option[value='oracle']").attr("selected", 'selected');
	        } else if(driverStr.indexOf('postgresql') > -1) {
	            $("#dsDrv option[value='postgresql']").attr("selected", 'selected');
	        } else if(driverStr.indexOf('DB2') > -1) {
	            $("#dsDrv option[value='DB2']").attr("selected", 'selected');
	        }else if(driverStr.indexOf('dm') > -1) {
				$("#dsDrv option[value='DM']").attr("selected", 'selected');
			}
	    }
		
	}
	else{
		chgPanel("jndi");
	}
}

function chgPanel(value){
	if(value == "jdbc"){
		if($('#driver').val() == '')
			changeTemplate($('#dsDrv').val());
		$("#jdbcspan").show();
		$("#jndispan").hide();
	}
	else{
		$("#jdbcspan").hide();
		$("#jndispan").show();
	}
	setNewPanel(value);
}

function setNewPanel(value){
	if(value == "jdbc"){
		$(":input",$("#jdbcspan")).addClass("required");
		$(":input",$("#jndispan")).removeClass("required");
		$("#a8metajdbc").removeClass("required");
	}
	else{
		$(":input",$("#jdbcspan")).removeClass("required");
		$(":input",$("#jndispan")).addClass("required");
		$("#a8metajndi").removeClass("required");
	}
}

function chgJdbcChk(){
	if($('#a8metajdbc').attr("checked")){
		$('#a8metajdbc').val("yes");
	}
	else{
		$('#a8metajdbc').val("no");
	}
}
function chgJndiChk(){
	if($('#a8metajndi').attr("checked")){
		$('#a8metajndi').val("yes");
	}
	else{
		$('#a8metajndi').val("no");
	}
}
/**
 * 根据数据类型 改变数据源模版
 * @param type 数据库类型
 */
function changeTemplate(type){
    if(type=="mysql"){
        $("#driver").val("com.mysql.jdbc.Driver");
        $("#url").val("jdbc:mysql://[host]:[port|3306]/[DB_Name]?autoReconnection=true");
    }else if(type=="oracle"){
        $("#driver").val("oracle.jdbc.driver.OracleDriver");
        $("#url").val("jdbc:oracle:thin:@[host]:[prot|1521]:[DB_Name]");
    }else if(type=="sqlserver") {
        $("#driver").val("net.sourceforge.jtds.jdbc.Driver");
        $("#url").val("jdbc:jtds:sqlserver://[host]:[port|1433]/[DB_Name]");
    }else if(type=="postgresql") {
        $("#driver").val("org.postgresql.Driver");
        $("#url").val("jdbc:postgresql://[host]:[port|5432]/[DB_Name]");
    }else if (type=="DB2") {
    	$("#driver").val("com.ibm.db2.jcc.DB2Driver");
        $("#url").val("jdbc:db2://[host]:[port|50000]/[DB_Name]");
    }else if (type=="DM") {
		$("#driver").val("dm.jdbc.driver.DmDriver");
		$("#url").val("jdbc:dm://[host]:[port|5236]");
	}
}

function dsShowDone(json){
	//真正提交保存
	if(json.navTabId == "ismeta"){
		if(json.statusCode == DWZ.statusCode.ok){
			$("#frmDsShow").attr("action", "/dee/datasource!saveDatasource.do?navTabId=ds&callbackType=closeCurrent");
			$("#frmDsShow").submit();
		}
		else{
			DWZ.ajaxDone(json);
			if(json.retFlag == 'cannot'){
				return;
			}
			alertMsg.confirm(json.message, {
				okCall: function(){
					$("#frmDsShow").attr("action", "/dee/datasource!saveDatasource.do?navTabId=ds&callbackType=closeCurrent");
					$("#frmDsShow").submit();
				}
			});
		}
		return;
	}
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

function testCon(){
	$("#frmDsShow").attr("action", "/dee/datasource!testCon.do");
	$("#frmDsShow").submit();
}

function saveDs(){
	var maxInt =parseInt($("#maxPoolSize").val());
	var minInt =parseInt($("#minPoolSize").val());
	var initInt =parseInt($("#initialPoolSize").val());
	if(maxInt <= minInt){
		alertMsg.error("最大连接数不能小于或等于最小连接数");
		$("#maxPoolSize").focus();
		return;
	}
	if(initInt < minInt){
		alertMsg.error("初始连接数不能小于最小连接数");
		$("#initialPoolSize").focus();
		return;
	}
	if(initInt > maxInt){
		alertMsg.error("初始连接数不能大于最大连接数");
		$("#initialPoolSize").focus();
		return;
	}
	
	$("#frmDsShow").attr("action", "/dee/datasource!isA8meta.do?navTabId=ismeta");
	$("#frmDsShow").submit();
}

function checkInput(obj){
	var inputObj = $("input.required",obj);
	var len = inputObj.length;
	for(var i=0;i<len;i++){
		if(hasSpecialChar(inputObj[i].value)){
			alertMsg.error(DWZ.msg("alertHasSpecialChar"));
			return false;
		}
	}
	return validateCallback(obj,dsShowDone);
}


