/**
 * @author ZhangHuihua@msn.com
 */
(function($){
	var _op = {
		cursor: 'move', // selector 的鼠标手势
		sortBoxs: 'div.sortDrag', //拖动排序项父容器
		replace: true, //2个sortBox之间拖动替换
		items: '> *', //拖动排序项选择器
		selector: '', //拖动排序项用于拖动的子元素的选择器，为空时等于item
		zIndex: 1000
	};
	var startOrder = -1;
	var endOrder = -1;
	
	var sortDrag  = {
		start:function($sortBox, $item, event, op){
//begin add by yangyu; 适配器拖动排序方法--------------------------------------------
			if(($item.attr("name")=="sourceProcess" || $item.attr("name")=="exchangeProcess"||$item.attr("name")=="targetProcess") && $item.attr("isDrag")=="false")
			{
				startOrder = $item.index();
				$item.attr("isDrag","true");
			}
//end--------------------------------------------------------------------------			
			isMouseDownDoing = false;
			if ($sortBox.children('div.sortDragHelper').length > 0)
				return false; 
			var $placeholder = this._createPlaceholder($item);
			var $helper = $item.clone();
			var position = $item.position();
			var sortPos = $item.parent().parent().scrollTop() + position.top;
			$helper.data('$sortBox', $sortBox).data('op', op).data('$item', $item).data('$placeholder', $placeholder);
/*			$helper.addClass('sortDragHelper').css({position:'absolute',top:position.top,left:position.left,zIndex:op.zIndex,width:$item.width()+'px',height:$item.height()+'px'}).jDrag({
				selector:op.selector,
				drag:this.drag,
				stop:this.stop,
				event:event
			});*/
			$helper.addClass('sortDragHelper').css({position:'absolute',top:sortPos,left:position.left,zIndex:op.zIndex,width:$item.width()+'px',height:$item.height()+'px'}).jDrag({
				selector:op.selector,
				drag:this.drag,
				stop:this.stop,
				event:event
			});
			
			$item.before($placeholder).before($helper).hide();
			return false;
		},
		drag:function(){
			var $helper = $(arguments[0]), $sortBox = $helper.data('$sortBox'), $placeholder = $helper.data('$placeholder');
			var $items = $sortBox.find($helper.data('op')['items']).filter(':visible').filter(':not(.sortDragPlaceholder, .sortDragHelper)');
			var helperPos = $helper.position(), firstPos = $items.eq(0).position();
			
			var $overBox = sortDrag._getOverSortBox($helper);
			if ($overBox.length > 0 && $overBox[0] != $sortBox[0]){ //移动到其他容器
				$placeholder.appendTo($overBox);
				$helper.data('$sortBox', $overBox);
			} else {
				
				var sPos = helperPos.top;
				var pHight = $items.parent().parent().height();
					if(sPos > pHight){
						//实际滚动条该偏移量
						$items.parent().parent().scrollTop(sPos-pHight+10);
					}
					else if(sPos < 0){
						$items.parent().parent().scrollTop($items.parent().parent().scrollTop()+ sPos);
					}
				
				for (var i=0; i<$items.length; i++) {
					var $this = $items.eq(i), position = $this.position();
					if (helperPos.top > position.top + 10) {
/*						var percent=helperPos.top/$this.parent().parent()[0].scrollHeight;
						var pos=($this.parent().parent()[0].scrollHeight-$this.parent().parent()[0].offsetHeight)*percent;
						$this.parent().parent().scrollTop(pos);*/
						$this.after($placeholder);
					} else if (helperPos.top <= position.top) {
						$this.before($placeholder);
						break;
					}
				}
			}
		},
		stop:function(){
			var $helper = $(arguments[0]), $item = $helper.data('$item'), $placeholder = $helper.data('$placeholder');

			var position = $placeholder.position();
			var sortPos = $item.parent().parent().scrollTop() + position.top;
			$helper.animate({
				/*top: position.top + "px",*/
				top: sortPos + "px",
				left: position.left + "px"
			}, {
				complete: function(){
					if ($helper.data('op')['replace']){ //2个sortBox之间替换处理
						$srcBox = $item.parents(_op.sortBoxs+":first");
						$destBox = $placeholder.parents(_op.sortBoxs+":first");
						if ($srcBox[0] != $destBox[0]) { //判断是否移动到其他容器中
							$replaceItem = $placeholder.next();
							if ($replaceItem.size() > 0) {
								$replaceItem.insertAfter($item);
							}
						}
					}
					$item.insertAfter($placeholder).show();
					$placeholder.remove();
					$helper.remove();
//begin add by yangyu 拖动排序--------------------------------------------------------------------------- 		
					if(($item.attr("name")=="sourceProcess" || $item.attr("name")=="exchangeProcess"||$item.attr("name")=="targetProcess") && $item.attr("isDrag")=="true")
					{
						endOrder = $item.index();
						$item.attr("isDrag","false");
                        var currentPage = $(".unitBox.page:visible");
						var flowId = $("#uid", currentPage).val();
						var resId = $item.attr("id");
						var sort = $item.attr("name");					
						var order = "{'start':'"+startOrder+"','end':'"+endOrder+"','flowId':'"+flowId+"','resId':'"+resId+"','proName':'"+sort+"'}";
						if(startOrder!=endOrder)
							{
							    $.ajax({
							        async: false,
							        type:"post",
							        dataType: "json",
							        data:{order:order},
							        url: "/dee/flowbase!changeOrder.do",
							        cache: false,
							        success: function(json) {
							        	if(!(json.statusCode == "200"))
							        	{
							        		alertMsg.error("排序失败！");
										    var parent = $item.parent();
											var lastOrder = $("div:last",parent).index();
											$item.remove();
											if(startOrder==lastOrder)
												{
													$item.insertAfter($("div:last",parent));
												}
											else
												{
													$item.insertBefore($("div:eq("+startOrder+")",parent));
												}
											if ($.fn.sortDragOne) $(parent).sortDragOne(null,$item);
							        	}
							        },
							        error:function()
							        {
							        	alertMsg.error("排序失败！");
									    var parent = $item.parent();
										var lastOrder = $("div:last",parent).index();
										$item.remove();
										if(startOrder==lastOrder)
											{
												$item.insertAfter($("div:last",parent));
											}
										else
											{
												$item.insertBefore($("div:eq("+startOrder+")",parent));
											}
										if ($.fn.sortDragOne) $(parent).sortDragOne(null,$item);
							        }
							    });
							}

					}
//end----------------------------------------------------------------------------------------------------------------------------
				},
				duration: 300
			});
		},
		
		
		_createPlaceholder:function($item){
			return $('<'+$item[0].nodeName+' class="sortDragPlaceholder"/>').css({
				width:$item.outerWidth()+'px',
				height:$item.outerHeight()+'px',
				marginTop:$item.css('marginTop'),
				marginRight:$item.css('marginRight'),
				marginBottom:$item.css('marginBottom'),
				marginLeft:$item.css('marginLeft')
			});
		},
		_getOverSortBox:function($item){
			var itemPos = $item.position();
			var y = itemPos.top+($item.height()/2), x = itemPos.left+($item.width()/2);
			return $(_op.sortBoxs).filter(':visible').filter(function(){
				var $sortBox = $(this), sortBoxPos = $sortBox.position();
				return DWZ.isOver(y, 0, sortBoxPos.top, sortBoxPos.left, $sortBox.height(), $sortBox.width());
			});
		}
	};
	
	$.fn.sortDrag = function(options){
				
		return this.each(function(){
			var op = $.extend({}, _op, options);
			var $sortBox = $(this);
			
			if ($sortBox.attr('selector')) op.selector = $sortBox.attr('selector');
			$sortBox.find(op.items).each(function(i){
				var $item = $(this), $selector = $item;
				if (op.selector) {
					$selector = $item.find(op.selector).css({cursor:op.cursor});
				}

				$selector.mousedown(function(event){
					sortDrag.start($sortBox, $item, event, op);
	
					event.preventDefault();
				});
			});
			
			
		});
	};
//sortDragAct add by yangyu; 动态添加sortDrag时初始化方法(批量)
	$.fn.sortDragAct = function(options){
		
		return this.each(function(){
			var op = $.extend({}, _op, options);
			var $sortBox = $(this);
			
			if ($sortBox.attr('selector')) op.selector = $sortBox.attr('selector');
			
			$sortBox.find(op.items).each(function(i){
				var $item = $(this), $selector = $item;
				if (op.selector) {
					$selector = $item.find(op.selector).css({cursor:op.cursor});
				}
				
				var doMouseDownTimmer = null;
				var isMouseDownDoing = false;

				$selector.mousedown(function(event){

					isMouseDownDoing = false;
					doMouseDownTimmer = setTimeout(function(){sortDrag.start($sortBox, $item, event, op);},1000);					
					
					event.preventDefault();
				});
				
				$selector.mouseup(function(event){

				    if (!isMouseDownDoing)
				    {
				        clearTimeout(doMouseDownTimmer); //能进到这里来，不管三七二十一先把doMouseDownTimmer清除，不然200毫秒后doMouseDown方法还是会被调用的
				    }

					event.preventDefault();
				});
			});
			
			
		});
	};
	
	$.fn.sortDragByClass = function(options){
		return this.each(function(){
			var op = $.extend({}, _op, options);
			var $sortBox = $(this);
			if ($sortBox.attr('selector')) op.selector = $sortBox.attr('selector');
			
			$sortBox.find(op.items).each(function(i){
				var $item = $(this), $selector = $item;
				if (op.selector) {
					$selector = $item.find(op.selector).css({cursor:op.cursor});
				}
				
				var doMouseDownTimmer = null;
				var isMouseDownDoing = false;

				$(".map_drag", $selector).mousedown(function(event){

					isMouseDownDoing = false;
					doMouseDownTimmer = setTimeout(function(){sortDrag.start($sortBox, $item, event, op);},1000);					
					
					event.preventDefault();
				});
				
				$(".map_drag", $selector).mouseup(function(event){
				    if (!isMouseDownDoing)
				    {
				        clearTimeout(doMouseDownTimmer); //能进到这里来，不管三七二十一先把doMouseDownTimmer清除，不然200毫秒后doMouseDown方法还是会被调用的
				    }

					event.preventDefault();
				});
			});
		});
	};

//sortDragOne add by yangyu; 动态添加sortDrag时初始化方法(单个)
	$.fn.sortDragOne = function(options,$item){
		
		return this.each(function(){
			var op = $.extend({}, _op, options);
			var $sortBox = $(this);
			
			if ($sortBox.attr('selector')) op.selector = $sortBox.attr('selector');
			
				$selector = $item;
				if (op.selector) {
					$selector = $item.find(op.selector).css({cursor:op.cursor});
				}
				
				var doMouseDownTimmer = null;
				var isMouseDownDoing = false;

				$selector.mousedown(function(event){

					isMouseDownDoing = false;
					doMouseDownTimmer = setTimeout(function(){sortDrag.start($sortBox, $item, event, op);},1000);					
					
					event.preventDefault();
				});
				
				$selector.mouseup(function(event){

				    if (!isMouseDownDoing)
				    {
				        clearTimeout(doMouseDownTimmer); //能进到这里来，不管三七二十一先把doMouseDownTimmer清除，不然200毫秒后doMouseDown方法还是会被调用的
				    }

					event.preventDefault();
				});

		});
	};
})(jQuery);
