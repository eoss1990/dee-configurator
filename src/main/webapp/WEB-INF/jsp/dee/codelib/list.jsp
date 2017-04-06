<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/include.inc.jsp" %>

<div class="accordion" style="width:250px;float:left;margin:5px;">
    <div class="accordionHeader">
        <h2 style="background-image:none;"><span>icon</span>包名管理</h2>
    </div>
    <div class="accordionContent" layoutH="35">
        <ul id="packageList" class="ztree"></ul>
    </div>
</div>

<div id="codelib_content" class="unitBox"></div>


<script type="text/javascript">
    $(function () {
        loadPackageContent("");

        var packageSetting = {
            view: {
                addHoverDom: addHoverDom,
                removeHoverDom: removeHoverDom,
                selectedMulti: false
            },
            edit: {
                enable: true,
                editNameSelectAll: true,
                drag: {
                    isCopy: false,
                    isMove: false
                }
            },
            callback: {
                onClick: refreshPackageList,
                beforeRename: beforeRename
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        };

        $.ajax({
            async: false,
            type: "post",
            dataType: "json",
            url: "/dee/codelib!listPkg.do",
            success: function (result) {
                var zNodes = [];
                var list = result.jsonData;
                for (var i = 0; i < list.length; i++) {
                    zNodes[i] = {
                        id: list[i].id,
                        pId: list[i].parentId,
                        name: list[i].name,
                        nocheck: "true",
                        open: (list[i].parentId == '') ? true : false
                    };
                }
                $.fn.zTree.init($("#packageList"), packageSetting, zNodes);
            },
            error: function () {
                alert("其他错误");
            }
        });
    });

    function addHoverDom(treeId, treeNode) {
        var btn = $("#" + treeNode.tId + "_add");
        btn.unbind().bind("click", function () {
            addPkg(treeNode);
        });

        btn = $("#" + treeNode.tId + "_remove");
        btn.unbind().bind("click", function () {
            if (treeNode.getParentNode() != null) {
                deletePkg(treeNode);
            }else {
                return false;
            }
        });
        btn = $("#" + treeNode.tId + "_edit");
        btn.unbind().bind("click", function () {
            if (treeNode.getParentNode() != null) {
                editPkg(treeNode);
            } else {
                return false;
            }
        });
    }

    function removeHoverDom(treeId, treeNode) {
        $("#" + treeNode.tId + "_add").unbind().remove();
        $("#" + treeNode.tId + "_remove").unbind().remove();
        $("#" + treeNode.tId + "_edit").unbind().remove();
    }

    function addPkg(treeNode) {
        var flowTypename = "pkg" + current();
        $.ajax({
            async: false,
            type: "post",
            dataType: "json",
            data: "newPkgName=" + getWholePackage(treeNode) + "." + flowTypename,
            url: "/dee/codelib!savePkg.do",
            success: function (result) {
                if (result.statusCode == 200) {
                    var zTree = $.fn.zTree.getZTreeObj("packageList");
                    var newNode = zTree.addNodes(treeNode, {id: result.uuid, pId: treeNode.id, name: flowTypename});
                    zTree.editName(newNode[0]);
                } else if (result.statusCode == 300) {
                    alertMsg.error(result.message);
                } else {
                    alertMsg.error("其他错误");
                }
            },
            error: function () {
                alert("其他错误");
            }
        });
    }

    function current() {
        var d = new Date(), str = '';
        str += d.getHours();
        str += d.getMinutes();
        str += d.getSeconds();
        return str;
    }

    function deletePkg(treeNode) {
        if (treeNode.pId == null) {
            alertMsg.info("根节点不能删除");
            return;
        }
        var packageName = getWholePackage(treeNode);

        alertMsg.confirm("确定删除包 " + packageName + " ？", {
            okCall: function () {
                $.ajax({
                    async: false,
                    type: "post",
                    dataType: "json",
                    data: 'pkgName=' + packageName,
                    url: "/dee/codelib!deletePkg.do",
                    success: function (result) {
                        if (result.statusCode == 200) {
                            var zTree = $.fn.zTree.getZTreeObj("packageList");
                            zTree.removeNode(treeNode);
                            alertMsg.correct(result.message);
                        } else if (result.statusCode == 300) {
                            alertMsg.error(result.message);
                        } else {
                            alertMsg.error("其他错误");
                        }
                    },
                    error: function () {
                        alert("其他错误");
                    }
                });
            }
        });
    }

    function editPkg(treeNode) {
        var zTree = $.fn.zTree.getZTreeObj("packageList");
        zTree.editName(treeNode);
    }

    // 重命名
    function beforeRename(treeId, treeNode, newName) {
        if (newName.length == 0) {
            setTimeout(function () {
                alertMsg.error("名称不能为空");
            }, 50);
            return false;
        }

        var ret = false;

        var packageName = getWholePackage(treeNode.getParentNode());

        $.ajax({
            async: false,
            type: "post",
            dataType: "json",
            data: 'oldPkgName=' + packageName + "." + treeNode.name + "&newPkgName=" + packageName + "." + newName,
            url: "/dee/codelib!savePkg.do",
            success: function (result) {
                if (result.statusCode == 200) {
                    ret = true;
                    loadPackageContent(packageName + "." + newName);
                } else if (result.statusCode == 300) {
                    setTimeout(function () {
                        alertMsg.error(result.message);
                    }, 50);
                } else {
                    setTimeout(function () {
                        alertMsg.error("其他错误");
                    }, 50);
                }
            },
            error: function () {
                setTimeout(function () {
                    alertMsg.error("其他错误");
                }, 50);
            }
        });
        return ret;
    }

    // 刷新分类任务
    function refreshPackageList(event, treeId, treeNode) {
        var pkgName = getWholePackage(treeNode);
        loadPackageContent(pkgName);
    }

    function loadPackageContent(pkgName) {
        var url = "/dee/codelib!listClass.do?pkgName=" + pkgName;
        $("#codelib_content").loadUrl(url, {}, function() {
            $("#codelib_packagename").val(pkgName);
            $("#codelib_content").find("[layoutH]").layoutH();
        });
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
</script>