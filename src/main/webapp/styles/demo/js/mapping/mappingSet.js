$(function(){
	$("#btnMappingSet").click(function (){
		setBringMapping();
	});
	$("#btnTarget").die().live("click",function (){
		takeTarget();
	});
	$("#btnSource").die().live("click",function (){
		takeSource();
	});
});

function takeTarget(){
	if($("#mySelect").find("option:selected").length > 0){
		$("#mySelect").find("option:selected").each(function (){
			var selvalue = $(this).val();
			if(selvalue.length > 11){
				selvalue = selvalue.substring(0,10)+"..";
			}
			$("#dTarget").append("<div name='tarMappingSet' style='border:1px solid #B8D0D6;padding:5px;margin:5px;width:165px'>"+selvalue+"</div>");
		});
        if ($.fn.sortDragAct) $("div.sortDrag", document).sortDragAct();
	}else{
		alert("请选择需要右移的字段！");
	}
}

function takeSource(){
	if($("#mySelect").find("option:selected").length > 0){
		$("#mySelect").find("option:selected").each(function (){
			var selvalue = $(this).val();
			if(selvalue.length > 11){
				selvalue = selvalue.substring(0,10)+"..";
			}
			$("#dSource").append("<div name='srcMappingSet' style='border:1px solid #B8D0D6;padding:5px;margin:5px;width:165px'>"+selvalue+"</div>");
		});
        if ($.fn.sortDragAct) $("div.sortDrag", document).sortDragAct();
	}else{
		alert("请选择需要右移的字段！");
	}
}

function setBringMapping(){
	$("#dTarget div").each(function (){
		$("#dRight").append("<div style='border:1px solid #B8D0D6;padding:5px;margin:5px;width:300px'><table><tr><td><input type='text' name='mapLeft' value='"+$(this).text()+"'/></td><td><a id='shwMap' class='button'><span>...</span></a></td></tr></table></div>");
	});
	$("#dSource div").each(function (){
		$("#dLeft").append("<div style='border:1px solid #B8D0D6;padding:5px;margin:5px;width:300px'><table><tr><td><input type='text' name='mapRight' value='"+$(this).text()+"'/></td><td>"+$("#selectDict").html()+"</td><td><a id='shwMap' class='button'><span>...</span></a></td></tr></table></div>");
	});
    if ($.fn.sortDragAct) $("div.sortDrag", document).sortDragAct();
}
