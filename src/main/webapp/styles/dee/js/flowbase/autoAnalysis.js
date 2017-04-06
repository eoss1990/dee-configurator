 var setting = {
	view: {
        dblClickExpand: false
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    callback: {
    	beforeExpand: beforeExpand,
        onExpand: onExpand,
        onClick: onClick
    }
};

 var address;
 var namespace
 $(function(){

     $(document).keydown(function(event){
         if(event.keyCode==13){
             $("#analysis").click();
         }
     });


         $("#myFile").change(function() {
         var file=$("#myFile").val();
         $("#tempFile").val(file);

     });

     $("#analysis").click(function() {
         var wsdl = $("#tempFile").val();
         var check = "wsdl";
         if( wsdl.length == 0 || wsdl.indexOf(check) == -1){
             alertMsg.info("请输入WSDL地址或者选择WSDL文件！");
         }
         else{
             $.ajax({
                 async : false,
                 type : "post",
                 dataType : "json",
                 data : 'address=' + wsdl,
                 fileElementId:'myFile',
                 url : "/dee/wsprocessor!analysis.do",
                 success : function(result){
                     if(result.statusCode == 200){
                         alertMsg.info(result.message);
                         $("#showPanel").css("overflow","auto");
                         var zNodes = [];
                         var jsonData = result.jsonData;
                         address = jsonData[0];
                         namespace = jsonData[1];
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
                     }if(result.statusCode == 300){
                         var msg = result.message;
                         if (msg.indexOf("This file was not found")){
                             alertMsg.error("wsdl地址错误！");
                         }
                        else{
                             alertMsg.error(msg);
                         }
                         $("#treeDemo").empty();
                     }

                 },
                 error:function(result){
                     alertMsg(result.message);
                 }
             });
         }

     });


     $("#ensure").click(function(){
         var methodname = $("#methodName").val();
         var paraVal = $("#para").val();
         if(methodname.length == 0){
             alertMsg.info("请选择方法！");
         }else{
             var title = "选择信息会覆盖原有webservice信息，是否要覆盖？";
             alertMsg.confirm(title, {
                 okCall: function () {
             $("#addPara tbody").empty();
             $.pdialog.closeCurrent();
             $("#mtname").val(methodname);
             $("#namespace").val(namespace);
             $("#address").val(address);
             if(paraVal.length > 0)
             {
                 var paraSplit = paraVal.split(",");
                 var index = 0;
                 for(var i = 0 ; i < paraSplit.length-1 ; i++){
                     $("#addPara tbody").append("<tr class=\"unitBox\">" +
                     "<td><input  class=\"required textInput\" type=\"text\"  name=\"items[" + index + "].key\" submitName=\"items[" + index + "].key\" value="+ paraSplit[i] +"></td>" +
                     "<td><input  class=\"required textInput\" type=\"text\"  name=\"items[" + index + "].value\" submitName=\"items[" + index + "].value\"></td>" +
                     "<td><a href=\"javascript:void(0);\" onClick=\"javascript:delRow(this);\" class=\"btnDel\"></a></td>"+ "</tr>");
                     index++;
                 }
             }
         }
             })
         }
     });
 });

var curExpandNode = null;
function beforeExpand(treeId, treeNode) {
    var pNode = curExpandNode ? curExpandNode.getParentNode():null;
    var treeNodeP = treeNode.parentTId ? treeNode.getParentNode():null;
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    for (var i=0, l=!treeNodeP ? 0:treeNodeP.children.length; i<l; i++ ) {
        if (treeNode !== treeNodeP.children[i]) {
            zTree.expandNode(treeNodeP.children[i], false);
        }
    }
    while (pNode) {
        if (pNode === treeNode) {
            break;
        }
        pNode = pNode.getParentNode();
    }
    if (!pNode) {
        singlePath(treeNode);
    }

}
function singlePath(newNode) {
    if (newNode === curExpandNode) return;
    if (curExpandNode && curExpandNode.open==true) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        if (newNode.parentTId === curExpandNode.parentTId) {
            zTree.expandNode(curExpandNode, false);
        } else {
            var newParents = [];
            while (newNode) {
                newNode = newNode.getParentNode();
                if (newNode === curExpandNode) {
                    newParents = null;
                    break;
                } else if (newNode) {
                    newParents.push(newNode);
                }
            }
            if (newParents!=null) {
                var oldNode = curExpandNode;
                var oldParents = [];
                while (oldNode) {
                    oldNode = oldNode.getParentNode();
                    if (oldNode) {
                        oldParents.push(oldNode);
                    }
                }
                if (newParents.length>0) {
                    zTree.expandNode(oldParents[Math.abs(oldParents.length-newParents.length)-1], false);
                } else {
                    zTree.expandNode(oldParents[oldParents.length-1], false);
                }
            }
        }
    }
    curExpandNode = newNode;
}

function onExpand(event, treeId, treeNode) {
    curExpandNode = treeNode;
}

function onClick(e,treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    var nodes = zTree.getSelectedNodes();
    if (nodes.length > 0) {
        var level = nodes[0].level
    }
    var paraValue ="";
   if(level == 2 ){
        if(nodes[0].check_Child_State == -1){
            $("#methodName").empty();
            $("#methodName").val(nodes[0].name);
        }else{
            $("#methodName").empty();
            $("#methodName").val(nodes[0].name);
            $("#para").empty();
            for(var i = 0 ;i < nodes[0].children.length ; i++){
                paraValue += nodes[0].children[i].name;
                paraValue += ",";
            }
            $("#para").val(paraValue);
        }
    }else if(level != 2){
       $("#methodName").val("");
   }
    zTree.expandNode(treeNode, null, null, null, true);
}
