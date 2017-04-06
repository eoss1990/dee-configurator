<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/redo/syncList.js" type="text/javascript"></script>
<script src="/styles/dee/js/redo/redoDetail.js" type="text/javascript"></script>

<script type="text/javascript">

	
    $(document).ready(function () {
        
        var doc_code = $(".code").html();
       
        if(doc_code != null){
            doc_code = doc_code.length < 50 ? doc_code : doc_code.slice(0, 50) + "...";
            $(".code").html("<xmp>" + doc_code + "</xmp>");

            $("body").die().live("click", function (event) {
                var target = $(event.target);
                if (target.parents('.page.unitBox').length < 1) {
                    var currentPage = $(".unitBox.page:visible");
                    if ($("#doc_code", currentPage).length > 0 && $('#redoDetail').is(':visible')) {
                        recovery();
                    }
                }
            });
        }
     
    });

    function recovery() {
        var tmp = $(".listheight").height() * 2;
        $('.listheight').height(tmp);
        $("#redoDetail").hide();
    }

    function redoDetail() {
       
        if($(".syncName").val() == ""){
            var a = $(".stateflag").html().trim();
            if (a == "成功") {
                alertMsg.info("任务已重发成功，不存在异常信息!");
            } else {
                
                if ($("#redoDetail").is(":hidden")) {
                    var a = $(".listheight").height() / 2;
                    var redo_id = $("#redo_id").val();
                    $(".listheight").height(a);

                    $.ajax({
                        async : false,
                        type : "post",
                        dataType : "json",
                        data : 'redo_id=' + redo_id,
                        url : "/dee/redo!redoDetail.do",
                        success : function(result) {

                            $("#redoDetail").show();
                            var zNodes = [];
                            var jsonData = result.jsonData;
                            var disname = jsonData[1];
                            $(".syncName").val(disname);
                            $("#doc_code").text(jsonData[0]);
                           
                            var rootNode = new Array();

                            rootNode = jsonData[2];

                            if (rootNode.length > 0) {

                                for (var i = 0; i < rootNode.length; i++) {

                                    var obj = {
                                        id : rootNode[i].nodeId,
                                        pId : rootNode[i].nodePId,
                                        name : rootNode[i].nodeName
                                    };

                                    zNodes.push(obj);
                                }

                                $.fn.zTree.init($("#treeDemo"), setting, zNodes);

                            }
                            if (!$("#treeDemo").html()) {
                                $("#treeDemo").hide();
                                $("#doc_code").show();
                                $("#changeMode").hide();
                            } else {
                                $("#doc_code").hide();

                                $("#changeMode").click(function() {
                                    
                                    if ($("#doc_code").is(":hidden")) {
                                        $("#treeDemo").hide();
                                        $("#doc_code").show();
                                    } else {
                                        if (!$("#treeDemo").html()) {
                                            $("#treeDemo").hide();
                                            $("#doc_code").show();
                                        } else {
                                            $("#doc_code").hide();
                                            $("#treeDemo").show();
                                        }
                                    }
                                });
                            }

                        }
                    });
                }
            }
        } 
       
        else{
            if ($("#redoDetail").is(":hidden")){
                $(".listheight").height(($(".listheight").height() / 2));
              	$("#redoDetail").show();
            }
        }

    }
</script>
<body id="pagebody">
        <div class="listheight">
            <div class="panelBar">
                <ul>
                    <li><div style="padding-top:6px; font-weight:bold;">异常信息列表&nbsp;&nbsp;<input type="hidden" class="redopage_syncid" value="${sync_id}"></div></li>
                </ul>
                <ul class="toolBar" style="float:right;">
                    <li><a title="是否确定删除？" target="selectedTodo" rel="redo_id" postType="string" href="/dee/redo!delRedo.do" callback="refreshpage" class="delete"><span>删除</span></a></li>
                    <li><a title="是否确定重发？（修改适配器顺序后可能会导致任务重发失败）" target="selectedTodo" callback="refresh_redo" rel="redo_id" postType="string" href="/dee/redo!executeRedo.do" class="icon"><span>重发</span></a></li>
                </ul>
            </div>
            <table class="list" width="100%" style="word-break:break-all;word-wrap:break-word" layoutH="53" >
                <thead>
                    <tr>
                        <th width="5%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;text-align: center;"><input type="checkbox"  onclick="javascript:checkRedoAll(this);" /></th>
                        <th width="30%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">内容</th>
                        <th width="50%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">错误信息</th>
                        <th width="5%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">重发次数</th>
                        <th width="10%" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">数据状态</th>
                    </tr>
                </thead>
                <tbody id="redoList">
                    <c:forEach var="item" items="${redoPage.items}" varStatus="status">
                     	
                        <tr style="cursor:pointer;" ondblclick="redoDetail()"> 
                        
                            <td style="text-align: center;"><input type="checkbox" name="redo_id" id="redo_id" value="${item.redo_id}" />
                            
                            	<input type="hidden" id="open_sync_id" value="${item.sync_id}"/>
                            </td>
                           
                          	<td class="code">${item.doc_code}</td>
                            <td>${item.errormsg}</td>
                            <td class="redocounter">${item.counter}</td>
                            <td class="stateflag">
                            <c:if test="${item.state_flag == 0}">失败</c:if>
                           	<c:if test="${item.state_flag == 2}">部分失败</c:if>	
                            </td>
                        </tr>
                       
                    </c:forEach>
                </tbody>
            </table>
        </div>
         <div class="panelBar">
        <div class="pages">
            <span>每页</span>
            <select name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">
                <c:forEach begin="10" end="40" step="10" varStatus="s">
                    <option value="${s.index}" ${numPerPage eq s.index ? 'selected="selected"' : ''}>${s.index}</option>
                </c:forEach>
            </select>
            <span>总条数: ${redoPage.context.totalCount}</span>
        </div>
        <div class="pagination" targetType="navTab" totalCount="${redoPage.context.totalCount}" numPerPage="${redoPage.context.pageSize}" pageNumShown="3" currentPage="${redoPage.index}"></div>
    </div>
        <table id="redoDetail" style="display: none; height:0px; width: 100%"  layoutH="270" >
            <tr>
                <td >&nbsp;</td>
            </tr>
            <tr>
                <td width="10%" align="right" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">
                    <h1>任务名称：</h1>
                </td>
                <td width="90%" align="left" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">
                    <input type="text" class="syncName" style="width:500px" readonly="readonly">
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td width="30%" height="145px" align="right">
                    <h1>内容：<a id="changeMode" href="javascript:void(0);" style="color:#f00;">切换</a></h1>
                </td>
                <td align="left">
                	
                	<ul id="treeDemo" class="ztree" style="height:145px;"></ul>
                	
                    <textarea id="doc_code" style="height:145px;width: 500px;" readonly="readonly"></textarea>
                </td>
            </tr>
        </table>
</body>
