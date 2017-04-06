/**
 * @author Roger Wu
 * @version 1.0
 */

var pos_a;
(function($){
	$.extend($.fn, {
		jPanel:function(options){
			var op = $.extend({header:"panelHeader", headerC:"panelHeaderContent", content:"panelContent", coll:"collapsable", exp:"expandable", footer:"panelFooter", footerC:"panelFooterContent"}, options);
			return this.each(function(){
				var $panel = $(this);
				var close = $panel.hasClass("close");
				var collapse = $panel.hasClass("collapse");
//添加－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－	add by yangyu			
				var addbtn = $panel.hasClass("addbtn");
//结束－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－				
				var $content = $(">div", $panel).addClass(op.content);				
				var title = $(">h1",$panel).wrap('<div class="'+op.header+'"><div class="'+op.headerC+'"></div></div>');
				if(collapse)$("<a href=\"\"></a>").addClass(close?op.exp:op.coll).insertAfter(title);
//添加－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－add by yangyu
				if(addbtn)$("<a href=\"\" style=\"background:url(/styles/dee/themes/images/ic_tool_modify.png); width:16px; height:16px; background-repeat:no-repeat; display:block; position:absolute;right:4px;top:4px;color:#3C7FB1;\"></a>").insertAfter(title);
//结束------------------------------------------------------------------------------------------------------------------                
				var header = $(">div:first", $panel);
				var footer = $('<div class="'+op.footer+'"><div class="'+op.footerC+'"></div></div>').appendTo($panel);
				
				var defaultH = $panel.attr("defH")?$panel.attr("defH"):0;
				var minH = $panel.attr("minH")?$panel.attr("minH"):0;
				if (close) 
					$content.css({
						height: "0px",
						display: "none"
					});
				else {
					if (defaultH > 0) 
						$content.height(defaultH + "px");
					else if(minH > 0){
						$content.css("minHeight", minH+"px");
					}
				}
//添加---------------add by yangyu panel右上角添加新建按钮-----------------------------------------------------------------------------------------				
				if(addbtn)
					{
						var pucker=$("a", header);
						pucker.click(function(){
                            var currentPage = $(".unitBox.page:visible");
							var flowId = $("#uid", currentPage).val();
							if(flowId!=null && flowId!="")
								{
									var title=null;
									pos_a = $(this);
									var url="";
									var div = pos_a.parent().parent().next();
									var divName = div.attr("id");
									var sort = $("div.sortDrag >div:last",div).index();
									if(sort<0)
										sort = 0;
									else
										sort = sort+1;
									if(divName == "sourceProcess")
										{
											title="新建来源配置";
											sort = sort+1000;
											/*url="/dee/sapws!view.do?flowId="+flowId+"&sort="+sort;*/
											url="/dee/jdbcreader!jdbcreadershow.do?flowId="+flowId+"&sort="+sort;
										}
									else if(divName == "exchangeProcess")
										{	
											title="新建转换配置";
											sort = sort+2000;
											url="/dee/mapping!mappingShow.do?flowId="+flowId+"&sort="+sort;
										}									
									else if(divName=="targetProcess")
										{
											title="新建目标配置";
											sort = sort+3000;
											url="/dee/jdbcwriter!jdbcWritershow.do?flowId="+flowId+"&sort="+sort;
											/*var ids=[];
											$("div[name='targetProcess']").each(function(i){
												ids[i]=$(this).attr("id");
											});
											$.ajax({
										        async: false,
										        type:"post",
										        dataType: "json",
										        data:{ids:ids.join(",")},
										        url: "/dee/flowbase!checkProcess.do",
										        cache: false,
										        success: function(json) {
										        	sort = sort+3000;
										        	url="/dee/jdbcwriter!jdbcWritershow.do?flowId="+flowId+"&sort="+sort+"&writerExist="+json;
										        	if (json) {
										        		url="/dee/script!view.do?flowId="+flowId+"&sort="+sort+"&writerExist="+json;
										        	}
										        },
										        error:function()
										        {	
										        	alertMsg.error("系统异常！");
										        }
										    });*/
											
										}
										
									
									var dlgId="resourceDlg";
									var options={width:850, height:550,maxable:false};
									$.pdialog.open(url, dlgId, title, options);
								}
							else
								alertMsg.error("请先保存基础信息！");
							return false;
						});
					}
//结束------------------------------------------------------------------------------------------------------------------------------				
				if(!collapse) return;
				var $pucker = $("a", header);
				var inH = $content.innerHeight() - 6;
				if(minH > 0 && minH >= inH) defaultH = minH;
				else defaultH = inH;
				$pucker.click(function(){
					if($pucker.hasClass(op.exp)){
						$content.jBlindDown({to:defaultH,call:function(){
							$pucker.removeClass(op.exp).addClass(op.coll);
							if(minH > 0) $content.css("minHeight",minH+"px");
						}});
					} else { 
						if(minH > 0) $content.css("minHeight","");
						if(minH >= inH) $content.css("height", minH+"px");
						$content.jBlindUp({call:function(){
							$pucker.removeClass(op.coll).addClass(op.exp);
						}});
					}
					return false;
				});
			});
		}
	
	});
})(jQuery);     

