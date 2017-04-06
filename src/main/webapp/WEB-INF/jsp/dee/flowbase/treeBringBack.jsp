<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/dee/js/flowbase/treebringback.js"></script>
<script type="text/javascript">
    var zNodes =[
             <c:forEach var="item" items="${ftList}" varStatus="status">
	            <c:choose>
	          		<c:when test="${item.FLOW_TYPE_ID=='0'}">
	          			{ id:"${item.FLOW_TYPE_ID}",pId:"${item.PARENT_ID}",name:"${item.FLOW_TYPE_NAME}",open:"true",nocheck:"true"},
	          		</c:when>
	          	 	<c:otherwise>
	          			{ id:"${item.FLOW_TYPE_ID}",pId:"${item.PARENT_ID}",name:"${item.FLOW_TYPE_NAME}",oncheck:"true"},
	          		</c:otherwise>
	          	</c:choose>
             </c:forEach>
    ];
    
    var chkStyle = "radio";     // 2种状态：checkbox radio
    var setting = {
/*        check: {
            enable: true,
            chkStyle: chkStyle,
            radioType: "level"
        },*/
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
//            onCheck: zTreeOnCheck,         // check事件
			onClick: bringback,
            beforeCheck: beforeCheck
        }
    };
    
    $(document).ready(function(){
        $.fn.zTree.init($("#treeDemo"), setting, zNodes);
    });
    
    function bringback(event, treeId, treeNode)
    {
    	/* $.bringBack({flow_type_name:$(treeNode).attr("name"), flow_type_id:$(treeNode).attr("id"), parent_id:$(treeNode).attr("pId")}); */
        var currentPage = getCurrentPage();
    	$("#flow_type_id",$("#flowBaseForm", currentPage)).val($(treeNode).attr("id"));
    	$("#parent_id",$("#flowBaseForm", currentPage)).val($(treeNode).attr("pId"));
    	$("#flow_type_name",$("#flowBaseForm", currentPage)).val($(treeNode).attr("name"));
    	$.pdialog.closeCurrent();
    }
</script>
<div class="pageContent">
<div style="float:left; display:block; margin:2px; overflow:auto; width:330px; height:356px; border:solid 1px #CCC; line-height:21px;">
		<ul id="treeDemo" class="ztree"></ul>
</div>
</div>
