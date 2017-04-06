<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>

<link rel="stylesheet" href="/codemirror-4.2/lib/codemirror.css">
<link rel="stylesheet" href="/codemirror-4.2/addon/hint/show-hint.css">
<script src="/styles/dee/js/resource/script-data.js" type="text/javascript"></script>
<script src="/codemirror-4.2/lib/codemirror.js"></script>
<script src="/codemirror-4.2/addon/hint/show-hint.js"></script>
<script src="/styles/dee/js/resource/groovy-hint.js" type="text/javascript"></script>
<script src="/codemirror-4.2/addon/edit/matchbrackets.js"></script>
<script src="/codemirror-4.2/mode/groovy/groovy.js"></script> 
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/scriptprocessor.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>
<style>
    .CodeMirror {border-top: 1px solid #d0d0d0; border-bottom: 1px solid #d0d0d0;}
  
	.ztree li span.button.icon03{ float: none; margin:0; background: url(/styles/dee/themes/images/iconfont-xiangyou.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
</style>

<div class="pageContent">
  <form method="post" action="/dee/script!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
        id="scriptProcessorForm" name="scriptProcessorForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="../resource/resourceType.jsp"></jsp:include>
      <div class="unit">
        <label>名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" class="required uInput" maxlength="50"/>
      </div>
      <br/>
      <br/>
     
     <div class="unit" style="height:378px;" >
      	<div id="treeDiv" style="float:left; width:29%; height:378px; border: 1px solid #d0d0d0;">
      		<input type="text" style="width:200px" id="search"/><a type="button" href="javascript:void(0);" class="btnLook" onclick="searchTreeValue()"></a>
      		 
      		<div id="two" style="height: 350px;position: absolute;top: 30px;left: 3px;width: 28%;overflow: auto;">
      			<ul id="tree" class="ztree" ></ul>
      		</div>
      	 
      		 
      		 
      	</div>
      	<div style="float:left; width:70%; height:100%; border: 1px solid #d0d0d0;margin-left: 2px;">
      		<textarea id="scriptInfo" name="scriptBean.script">${scriptBean.script}</textarea>
      	</div>
      </div> 
 
    </div>
    <div class="formBar">
      <ul>
      	<li>
                <div class="button">
                    <div class="buttonContent">
                        <button type="button" onclick="javascript:compileScriptForm(this);">编译校验</button>
                    </div>
                </div>
            </li>
        <li>
          <div class="buttonActive">
            <div class="buttonContent">
              <input type="hidden" name="flowId" value="${flowId}" />
              <input type="hidden" name="resourceId" value="${resourceId}" />
              <input type="hidden" name="sort" value="${sort}" />
              <button type="button" onclick="javascript:submitScriptProcessorForm(this);">保存</button>
            </div>
          </div>
        </li>
        <li>
          <div class="button">
            <div class="buttonContent">
              <button type="button" class="close">取消</button>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </form>
</div>
<script>

var setting = {
		view:{
			addHoverDom: addHoverDomScript,
			removeHoverDom: removeHoverDomScript
		},
		data: {
			simpleData: {
				enable: true,
				idKey: "id",
				pIdKey: "pId",
				rootPId: 0
			}
		},
		callback: {
			onClick:clk,
			onDblClick: zTreeOnDblClick
		}
	};
 
var functionTrees = '${functionTree}';
//debugger;
 
var zNodes = eval("(" + functionTrees + ")");

var twoHeght =  $("#two").height();
 
var dialogId = "${resourceId}";	
jQuery(function(){	
	
	$.fn.zTree.init($("#tree"), setting,zNodes);
	
	if(dialogId==null||dialogId=="")
		dialogId = "resourceDlg";
	var scriptDialog = $("body").data(dialogId);

    setTimeout(function () {
        var codeEditor = CodeMirror.fromTextArea($("#scriptInfo", scriptDialog)[0], {
            lineNumbers: true,
            matchBrackets: true,
            extraKeys: {"Tab": "autocomplete"},
            mode: {name: "groovy", globalVars: true}
        });
        codeEditor.setSize("100%", "100%");
        editor[dialogId] = codeEditor;
        codeEditor.on('change', function(editor, obj) {
            if (obj.text == '.') {
                codeEditor.showHint();
            }
        });
    }, 50);
    
    $("body").click( function(e) { 
   // 	 if( e.stopPropagation){
  //  		e.stopPropagation();
    		var _con = e.target;   // 设置目标区域
    		if(_con.id =="two" || _con.nodeName== "LI" || _con.nodeName =="UL" ){
    			var descDiv = $("#descDiv");
    			if( descDiv && "none" != descDiv.css("display") ){
    				var tw = $("#two");
            		tw.height(twoHeght);
            		descDiv.css("display","none");
    			}
    		}
 //   	}
    });  
});

    
function zTreeBeforeAsync(treeId, treeNode) {
    return true;
}
 
 

function clk(event,treeId,treeNode){
	var descDiv = document.getElementById("descDiv");
	if( descDiv && descDiv.style.display=="none" ){
		$("#two").height(twoHeght - 100);
		descDiv.style.display="";
	}
	var description = document.getElementById("description");
	if(description){
		description.value=treeNode.description;
	}else{
		var tw = $("#two");
		var newheight = twoHeght - 100;
		var str = "<div id=\"descDiv\" style=\"width: 28%;height:100px;"+
		"position: absolute;bottom: 5px;left: 5px;\"><textarea id=\"description\" readonly=\"readonly\"  style=\"width: 98%;height: 95px;max-width: 98%;max-height:95px\">"+treeNode.description+"</textarea></div>";
		tw.height(newheight);
		tw.after(str);	
	}
}

 
function searchTreeValue(){
	var value = document.getElementById("search").value;
	//if(value && value != "")
	functionZTreeSearch("tree",value);

}


function zTreeOnDblClick(event, treeId, treeNode){
	if(treeNode.isClick == "false"){
		return;
	}
	if(treeNode.type == "system"){
		var lineNum = editor[dialogId].getCursor().line + 1;
		var str = treeNode.content+"("+treeNode.params+");";
		if(str.indexOf("debug")!= -1){
			str = str.replace("lineNum","\""+lineNum+"\"");
		}
		editor[dialogId].replaceSelection(str); 
	}else if(treeNode.type == "user"){
		editor[dialogId].replaceSelection(treeNode.content); 
	}
}

function addHoverDomScript(treeId, treeNode) {
	
	if(treeNode.isClick == "false"){
		return;
	}
	
 	debugger;
	var aObj = $("#"+ treeNode.tId + "_a");
	 
	if ($("#diyBtn_"+treeNode.tId).length>0) return;
	var editStr = "<span id='diyBtn_space_" +treeNode.tId+ "' >&nbsp;</span><span class='button icon03' id='diyBtn_" +treeNode.tId+ "' title='"+treeNode.name+"' onfocus='this.blur();'></span>";
	aObj.append(editStr);
	var btn = $("#diyBtn_"+treeNode.tId);
	if (btn) btn.bind("click", function(){ 
	//	debugger;
		if(treeNode.isClick == "false"){
			return;
		}
		if(treeNode.type == "system"){
			var lineNum = editor[dialogId].getCursor().line + 1;
			var str = treeNode.content+"("+treeNode.params+");";
			if(str.indexOf("debug")!= -1){
				str = str.replace("lineNum","\""+lineNum+"\"");
	//			console.log(str);
			}
			editor[dialogId].replaceSelection(str); 
		}else if(treeNode.type == "user"){
			editor[dialogId].replaceSelection(treeNode.content); 
		}
		
	});
 
};
function removeHoverDomScript(treeId, treeNode) {
	$("#diyBtn_"+treeNode.tId).unbind().remove();
	$("#diyBtn_space_" +treeNode.tId).unbind().remove();
};




 
</script>
    