$(function(){
	//初始化
	parseQuartzCode($.trim($('#quartz_code').val()));

	/*    //信息提示
    if($.trim($('#msgID').val())=='1'){
        alert('定时器保存成功！');
        window.parent.frames["mainFrame"].document.location.reload();
    }*/

	//执行事件 绑定点击事件
	$('#quartz_type').change(function(){
		chgQuartzType();
	});
	$('#quartz_mt').change(function(){
		chgInnerType();
	});
	//选择 绑定点击事件 
	$('#selectID').click(function(){
		getFlowDetail();  
	});
	//提交  绑定点击事件 
	$('#submitButton').click(function(){
		buildQuartzCode();
		if(checkform()){
			$('#sourceform').submit();
		}
	});
});

function parseQuartzCode(code){
	if(code){
		var arr = code.split(',');
		//兼容以前版本
		if(arr.length == 2){
			code = '0,' + code;
			arr = code.split(',');
		}
		//解析QuartzCode
		if(arr.length>2){
			if(arr[0] == '0'){
				//document.getElementById('quartz_cnt').value  = arr[1];
				$('#quartz_cnt').attr('value',arr[1]);
				//document.getElementById('quartz_qty').value  = arr[2];
				$('#quartz_qty').attr('value',arr[2]);
			}
			else{
				//document.getElementById('quartz_mt').value  = arr[1];
				$('#quartz_mt').attr('value',arr[1]);
				//document.getElementById('quartz_day').value  = arr[2];
				$('#quartz_day').attr('value',arr[2]);
				//document.getElementById('quartz_week').value  = arr[2];
				$('#quartz_week').attr('value',arr[2]);
				//document.getElementById('quartz_hours').value  = arr[3];
				$('#quartz_hours').attr('value',arr[3]);
				//document.getElementById('quartz_mint').value  = arr[4];
				$('#quartz_mint').attr('value',arr[4]);
			}
			//document.getElementById("quartz_type").value = arr[0];
			$('#quartz_type').attr('value',arr[0]);
			chgQuartzType();
		}
	}
}

function buildQuartzCode(){
	var code = '';
	if($("#quartz_type").val() == '0'){
		code = '0,' + $("#quartz_cnt").val() +',' +
		$("#quartz_qty").val();
	}
	else{
		var qValue = '0';
		if($("#quartz_mt").val() == '3'){
			qValue =  $("#quartz_day").val();       
		}
		else if($("#quartz_mt").val() == '2'){
			qValue =  $("#quartz_week").val();          
		}
		code = '1,' + $("#quartz_mt").val() + ',' + qValue + ',' + 
		$("#quartz_hours").val() + ',' + $("#quartz_mint").val();
	}
	//document.getElementById('quartz_code').value = code;
	$('#quartz_code').attr('value',code);
}

function getFlowDetail() {
	// add by dkywolf 20120319 begin
	var paramStr = '';
	//var flowId = document.getElementById('flow_id').value;
	var flowId = $('#flow_id').val();
	if(flowId != null && flowId != undefined){
		paramStr = '&flow_id='+flowId;
	}
	var flowInfo = window.showModalDialog(ctx + '/servlet/FlowServlet?method=query4Schedule' + paramStr, '','dialogWidth:900px;dialogHeight:550px;center:yes');
	// add by dkywolf 20120319 end
	if(null!=flowInfo && flowInfo != "") {
		var flow = flowInfo.split(',');
		//document.getElementById('flow_id').value = flow[0];
		$('#flow_id').attr('value',flow[0]);
		//document.getElementById('flow_name').value = flow[1];
		$('#flow_name').attr('value',flow[1]);
	}
}
function checkform(){
	var dis_name = $('#dis_name');
	var flow_name = $('#flow_name');
/*	if($.trim(dis_name.val())==""){
		dis_name.focus();
		alertMsg.error("请填写定时器名称!");
		return false;
	}*/
	if($.trim(flow_name.val())==""){
		alertMsg.error("请选择交换任务!");
		return false;
	}
	if(hasSpecialChar(dis_name.val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	return true;
}
//方式切换
function chgQuartzType(){
	var type=$("#quartz_type").val();
	if(type=='0'){
		$("#fixedCtl").addClass("hide");
		$("#intervalCtl").removeClass("hide");
	}else{
		$("#intervalCtl").addClass("hide");
		$("#fixedCtl").removeClass("hide");
		if($("#quartz_mt").val() == '3'){
			$("#fixedInnerCtl").removeClass("hide");
			$("#fixedInnerOth").addClass("hide");
		}
		else if($("#quartz_mt").val() == '2'){
			$("#fixedInnerCtl").addClass("hide");
			$("#fixedInnerOth").removeClass("hide");
		}
		else{
			$("#fixedInnerCtl").addClass("hide");
			$("#fixedInnerOth").addClass("hide");
		}
	}
}

function chgInnerType(){
	if($("#quartz_mt").val() == '3'){
		$("#fixedInnerCtl").removeClass("hide");
		$("#fixedInnerOth").addClass("hide");
		//document.getElementById('quartz_day').value  = '1';
		$('#quartz_day').attr('value','1');
	}
	else if($("#quartz_mt").val() == '2'){
		$("#fixedInnerCtl").addClass("hide");
		$("#fixedInnerOth").removeClass("hide");
		//document.getElementById('quartz_week').value  = '1';
		$('#quartz_week').attr('value','1');
	}else{
		$("#fixedInnerCtl").addClass("hide");
		$("#fixedInnerOth").addClass("hide");
	}
}
