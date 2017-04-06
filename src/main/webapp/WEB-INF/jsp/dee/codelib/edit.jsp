<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/include.inc.jsp" %>
  

<style>
    span.error {
        display: inline-block;
    }
</style>
<script src="/styles/dee/js/resource/script-data.js" type="text/javascript"></script>

<link rel=stylesheet" href="/codemirror-4.2//doc/docs.css">
<link rel="stylesheet" href="/codemirror-4.2/lib/codemirror.css">

<script src="/codemirror-4.2/lib/codemirror.js"></script>
<script src="/codemirror-4.2/addon/edit/matchbrackets.js"></script>

<link rel="stylesheet" href="/codemirror-4.2/addon/hint/show-hint.css">
<script src="/codemirror-4.2/addon/hint/show-hint.js"></script>
<script  src="/codemirror-4.2/mode/clike/clike.js"></script> 
 
 
 

 
<form method="post" action="/dee/codelib!saveClass.do?callbackType=closeCurrent&navTabId=codelib"
      id="codeLibForm" class="pageForm required-validate" onSubmit="return checkInput(this);">

    <div style="margin: auto auto 10px;">
        <p style="margin-bottom: 5px;">
            <label>包名：</label>
            <input type="text" id="edit_packagename" name="codeLibDto.pkgName" class="required" style="width: 300px;" value="${codeLibBean.pkgName}" onclick="javascript:selectPackageName(this);" readonly/>
            <div id="packageNameSelect" class="pageForm" style="display:none;position: absolute; width:300px; height:150px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
                <ul id="packageNameTree" class="ztree" ></ul>
            </div>
        </p>

        <p style="margin-bottom: 5px;">
            <label>类名：</label>
            <input type="text" name="codeLibDto.className" class="required" style="width: 300px;" maxlength="50" value="${codeLibBean.className}"/>
        </p>

        <p style="margin-bottom: 5px;">
            <label>简介：</label>
            <textarea name="codeLibDto.simpleDesc" style="width: 300px; height: 70px; resize: none;" maxlength="500">${codeLibBean.simpleDesc}</textarea>
        </p>
    </div>

    <div class="tabs">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li><a href="javascript:refreshScriptInfo();"><span class="back">代码</span></a></li>
                </ul>
            </div>
        </div>
        <div class="tabsContent">
            <div >
                <textarea id="codeLibScriptInfo" name="codeLibDto.code">${codeLibBean.code}</textarea>
            </div>
        </div>
        <div class="tabsFooter">
            <div class="tabsFooterContent"></div>
        </div>
    </div>

    <div class="formBar">
        <ul>
            <li>
                <div class="button">
                    <div class="buttonContent">
                        <button type="button" onclick="javascript:compileCodeLibForm(this);">编译校验</button>
                    </div>
                </div>
            </li>
            <li>
                <div class="buttonActive">
                    <div class="buttonContent">
                        <input type="hidden" name="codeLibDto.id" value="${codeLibBean.id}"/>
                        <button type="button" onclick="javascript:submitCodeLibForm(this);">保存</button>
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

<script>
    var codeLibEditor;
 
    $(function () {
        setTimeout(function () {
            codeLibEditor = CodeMirror.fromTextArea(document.getElementById("codeLibScriptInfo"), {
                lineNumbers: true,
                matchBrackets: true,
             //   extraKeys: {"Tab": "autocomplete"},
                mode:   "text/x-java"
            });
           codeLibEditor.on('change', function (editor, obj) {
                if (obj.text == '.') {
                    codeLibEditor.showHint();
                }
            });  
            codeLibEditor.setSize(780, 240);
 	     }, 50);
        if (!$("#edit_packagename").val()) {
            $("#edit_packagename").val($("#codelib_packagename").val());
        }
    });  

    function submitCodeLibForm() {
        if (codeLibEditor) {
            codeLibEditor.save();
        }

        if (compileCodeLibForm()) {
            $("#codeLibForm").submit();
        } else {
            alertMsg.confirm("校验失败，是否保存？", {
                okCall : function() {
                    $("#codeLibForm").submit();
                }
            });
        }
    }

    function checkInput(obj){
        var inputObj = $("input.required", obj);
        var len = inputObj.length;
        for(var i=0;i<len;i++){
            if(hasSpecialChar(inputObj[i].value)){
                alertMsg.error(DWZ.msg("alertHasSpecialChar"));
                return false;
            }
        }
        return validateCallback(obj,dsShowDone);
    }

    function dsShowDone(json){
        //真正提交保存
        DWZ.ajaxDone(json);
        if (json.statusCode == DWZ.statusCode.ok){
            if (json.navTabId) {
                loadPackageContent($("#codelib_packagename").val());
            }
            if ("closeCurrent" == json.callbackType) {
               $.pdialog.closeCurrent();
            }
        }
    }

    function refreshScriptInfo() {
        codeLibEditor.refresh();
    }

    function selectPackageName(obj) {
        $("#packageNameSelect").css({left: "45px", top: "55px"}).slideDown("fast");
        ajaxTodo("/dee/codelib!listPkg.do", function (json) {
            packageSelectCallBack(json);
        });
        $("body").bind("mousedown", packageNameSelectMDown);
    }

    function packageNameSelectMDown(event) {
        if (!(event.target.id == "packageNameSelect" || $(event.target).parents("#packageNameSelect").length>0)) {
            hideWriteBackMenu();
        }
    }

    function hideWriteBackMenu() {
        $.fn.zTree.destroy("packageNameTree");
        $("#packageNameSelect").fadeOut("fast");
        $("body").unbind("mousedown", packageNameSelectMDown);
    }

    var packageSetting = {
        data: {
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "parentId",
                rootPId: 0
            }
        },
        callback: {
            onClick: function(event, treeId, treeNode) {
                $("#edit_packagename").val(getWholePackage(treeNode));
                hideWriteBackMenu();
            }
        }
    };

    function packageSelectCallBack(json) {
        if (json.statusCode == DWZ.statusCode.ok) {
            var zNodes = json.jsonData;
            for (var i = 0; i < zNodes.length; i++) {
                if (zNodes[i].parentId == '') {
                    zNodes[i].open = true;
                }
            }

            $.fn.zTree.init($("#packageNameTree"), packageSetting, zNodes);
        } else {
            DWZ.ajaxDone(json);
        }
    }

    function getWholePackage(treeNode) {
        var tmp = treeNode;
        var packageName = treeNode.name;
        while (tmp.getParentNode() != null) {
            tmp = tmp.getParentNode();
            packageName = tmp.name + "." + packageName;
        }
        return packageName;
    }

    function compileCodeLibForm(obj) {
        if (codeLibEditor) {
            codeLibEditor.save();
        }

        var ret = false;

        $.ajax({
            async: false,
            type: "post",
            dataType: "json",
            data: {
                "codeLibDto.pkgName":$("input[name='codeLibDto\\.pkgName']").val(),
                "codeLibDto.className":$("input[name='codeLibDto\\.className']").val(),
                "codeLibDto.code":$("#codeLibScriptInfo").val()
            },
            url: "/dee/codelib!compileClass.do",
            success: function (result) {
                if (result.statusCode == 200) {
                    alertMsg.correct(result.message);
                    ret = true;
                } else {
                    alertMsg.error(result.message);
                }
            },
            error: function () {
                alertMsg.error("编译失败，请查看日志！");
            }
        });

        return ret;
    }  
</script>