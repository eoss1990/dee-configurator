function delRow(obj) {
    var tr = $(obj).closest("tr");
    tr.remove();
}

function submitSapjcoProcessorForm(obj) {
	var dialog = returnDlg(obj);
	var resourceId = $("input[name='resourceId']",$("#sapjcoProcessorForm")).val();
	if (resourceId==null||resourceId=="")
		resourceId = "resourceDlg";
	if (hasSpecialChar($("input[name='bean.dis_name']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.jco_ashost']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.jco_sysnr']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.jco_client']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.jco_user']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.jco_passwd']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.func']",dialog).val()) ||
		hasSpecialChar($("input[name='sjpBean.out_param']",dialog).val()) ||
		hasSpecialChar(getGroupItems("sapjcoProcessorForm","jcoReturn",null,resourceId)) ||
		hasSpecialChar(getGroupItems("sapjcoProcessorForm","items",null,resourceId)))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#sapjcoProcessorForm",dialog).submit();
}

function submitJcoStructureMap(obj)
{	
	var resourceId = $("input[name='resourceId']",$("#sapjcoStructureForm")).val();
	if(resourceId!=null && resourceId!="")
	{
		if(hasSpecialChar(getGroupItems("sapjcoStructureForm","jcoStructureData",null,"jcoStructureMapping")))
		{
			alertMsg.error(DWZ.msg("alertHasSpecialChar"));
			return false;
		}
		$("#sapjcoStructureForm").submit();
	}
	else
		alertMsg.error("请先保存适配器！");
}

function submitJcoTableMap(obj)
{	
	var resourceId = $("input[name='resourceId']",$("#sapjcoTableForm")).val();
	if(resourceId!=null && resourceId!="")
	{
		if(hasSpecialChar(getGroupItems("sapjcoTableForm","jcoTableData",null,"jcoTableMapping")))
		{
			alertMsg.error(DWZ.msg("alertHasSpecialChar"));
			return false;
		}
		$("#sapjcoTableForm").submit();
	}
	else
		alertMsg.error("请先保存适配器！");
}

function structureClick(ev,obj){
	var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(resourceId!=null&&resourceId!="")
	{			    
		var url="/dee/sapjcowriter!viewJcoStructureMap.do?resourceId="+resourceId;
		var dlgId="jcoStructureMapping";
		var title="JCoStructure映射配置";
		var options={width:850,height:550,maxable:false};
		$.pdialog.open(url, dlgId, title, options);
		
	}
	else
	{
		alertMsg.error("请先保存适配器！");
	}
	ev.cancelBubble = true;
}
		
function tableClick(ev,obj){	
	var resourceId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(resourceId!=null&&resourceId!="")
	{			    
		var url="/dee/sapjcowriter!viewJcoTableMap.do?resourceId="+resourceId;
		var dlgId="jcoTableMapping";
		var title="JCoTable映射配置";
		var options={width:850,height:550,maxable:false};
		$.pdialog.open(url, dlgId, title, options);

	}
	else
	{
		alertMsg.error("请先保存适配器！");
	}
	ev.cancelBubble = true;
//	ev.stopPropagation();
}