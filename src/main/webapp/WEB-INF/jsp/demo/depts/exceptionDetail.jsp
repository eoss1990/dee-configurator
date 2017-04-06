<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<link rel="stylesheet" type="text/css" href="/styles/management/themes/jquery.easyui.min/easyui.css">
<link rel="stylesheet" type="text/css" href="/styles/management/themes/jquery.easyui.min/icon.css">
<script type="text/javascript" src="/styles/management/js/jquery.easyui.min.js"></script>
<script type="text/javascript">
    $(function(){
        $('#tg').treegrid({
            title:'Editable TreeGrid',
            iconCls:'icon-ok',
            width:700,
            height:500,
            rownumbers: true,
            animate:true,
            collapsible:true,
            fitColumns:true,
            url:'/demo/depts!writeExceptionDocument.do',
            idField:'id',
            treeField:'name',
            columns:[[
                {title:'name',field:'name',width:100},
                {field:'value',title:'value',width:200}
            ]]
        });
    })
</script>
<div class="pageContent">
  <div class="pageFormContent nowrap">
    <table id="tg"></table>
  </div>
</div>
