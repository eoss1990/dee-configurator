<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/redo/redoDetail.js" type="text/javascript"></script>
<script type="text/javascript">
    var zNodes = [];
    
    <c:forEach var="item" items="${docCodeList}" varStatus="status">
        var obj = { id:"${item.nodeId}", pId:"${item.nodePId}", 
        		    name:"${item.nodeName}" };
        zNodes.push(obj);
    </c:forEach>
    
    $(document).ready(function(){
    	$.fn.zTree.init($("#treeDemo"), setting, zNodes);

        $("#doc_code").hide();
       if (!$("#treeDemo").html()) {
            $("#treeDemo").hide();
            $("#doc_code").show();
            $("#changeMode").hide();
        }
        $("#changeMode").click(function() {
            if ($("#doc_code").is(":hidden"))  {
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
    });
</script>
<div class="pageContent">
    <form class="pageForm required-validate">
        <div class="pageFormContent nowrap">
            <div class="unit">
                <label>任务名称：</label>
                ${redoBean.syncBean.flow.DIS_NAME}
            </div>
            <div class="unit">
                <label>内容：<a id="changeMode" href="javascript:void(0);" style="color:#f00;">切换</a></label>
            </div>
            <br/>
            <div>
                <ul id="treeDemo" class="ztree" style="height:445px;"></ul>
                <textarea id="doc_code" style="height:445px;width:98%;">${redoBean.doc_code}</textarea>
            </div>
        </div>
        <div class="formBar">
            <ul><li><div class="button"><div class="buttonContent">
                    <button type="button" class="close">关闭</button>
            </div></div></li></ul>
        </div>
    </form>
</div>

