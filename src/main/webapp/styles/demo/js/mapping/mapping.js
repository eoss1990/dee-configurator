$(function(){
	btnIndex = 0;
    //添加
    $('#addBtn').click(function(){
        add_line();
    });
    $('#selType').change(function(){
    	chgPanel($('#selType').val());
    });
		$("a[id^='shwMap']").die().live('click', function() {
//			alert($(this).attr("id"));
			var options = {width:360,height:480,mask:true,close:function(){
//					alert(arguments[0].id);
					if(closeFlag == 1)
						return true;
					var zTree = $.fn.zTree.getZTreeObj("asyncDemo"),
					nodes = zTree.getCheckedNodes(true);
					if(nodes.length < 1){
						alertMsg.warn('请选择节点');
						closeFlag = 1;
						return false;
					}
					$(":input",$('#'+arguments[0].id).parent().parent()).val(nodes[0].name);
					return true;
				},param:{id:$(this).attr("id")}};
			$.pdialog.open("/demo/mapping!chgTree.do", "d_single", "映射配置", options);
		});
		
		$("#moreBtn").live('click', function() {
			$.pdialog.open("/demo/mapping!mappingSet.do", "d_single", "映射配置", {width:800,height:480,mask:true});
		});
		
		$(".sortDrag >div").live('mouseover',function() {
//			alert($(this).html());
			var rv = $("a[class='btnRemove']",$(this));
			var lastTd = $("td:last",$(this));
			if(rv.length == 0)
				$("<td><a class='btnRemove' title='删除'></a></td>").appendTo(lastTd.parent());
		 });
		
		$(".sortDrag >div").live('mouseleave',function(){
			var rv = $("a[class='btnRemove']",$(this));
			if(rv != undefined){
				rv.parent().remove(); //移出包含td的标签
//				setTimeout(function(){rv.remove();},1000);
			}
		  });
		$("a[class='btnRemove']").die().live('click', function() {
			var div = $(this);
			if(div != undefined && div != null && div.parent().parent().parent().parent().parent().index() != -1){
				var removeDiv = div.parent().parent().parent().parent().parent();
				if("dLeft"==removeDiv.parent().attr("id")){
					$("#dRight").children().eq(removeDiv.index()).remove();
				}
				else{
					$("#dLeft").children().eq(removeDiv.index()).remove();
				}
				removeDiv.remove();
			}
		 });

 		$(":input").live('mouseover',function() {
			$(this).focus();
		});
//		$(":input").live('mouseleave',function() {
//			$(this).blur();
//		});
});
var btnIndex = 0;
function add_line(){
	$("#dLeft").append("<div style='border:1px solid #B8D0D6;padding:5px;margin:5px;width:300px'><table><tr><td><input type='text' name='mapLeft'/></td><td>"+$("#selectDict").html()+"</td><td><a id='shwMap"+(btnIndex++)+"' class='button'><span>...</span></a></td></tr></table></div>");
	$("#dRight").append("<div style='border:1px solid #B8D0D6;padding:5px;margin:5px;width:300px'><table><tr><td><input type='text' name='mapRight'/></td><td><a id='shwMap"+(btnIndex++)+"' class='button'><span>...</span></a></td></tr></table></div>");
    if ($.fn.sortDragAct) $("div.sortDrag", document).sortDragAct();
}
function chgPanel(value){
	alert(value);
}
