<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/demo/js/depts/depts.js"></script>
<script type="text/javascript">
    var chkStyle = "checkbox";     // 2种状态：checkbox radio
    var setting = {
        check: {
            enable: true,
            chkStyle: chkStyle,
            radioType: "level"
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onCheck: zTreeOnCheck,         // check事件
            beforeCheck: beforeCheck
        }
    };
    
    var zNodes =[
             <c:forEach var="item" items="${deptList}" varStatus="status">
                 {   id:"${item.deptId}", 
                    pId:"${item.deptPid}", 
                   name:"${item.deptName}",
                   checked:"${item.checked==1?true:false}",
                   open:"${item.open==1?true:false}",
                   nocheck:"${item.nocheck==1?true:false}"
                 },
             </c:forEach>
    ];
    
    $(document).ready(function(){
        $.fn.zTree.init($("#treeDemo"), setting, zNodes);
        moveToLeaf();
    });
</script>
<div class="pageContent">
  <div class="pageFormContent nowrap">
      <div class="content_wrap">
          <div class="zTreeDemoBackground left">
              <ul id="treeDemo" class="ztree"></ul>
          </div>
          <div class="right">
              <div>
                                              叶子节点:<br/>
                  <div id="selectedLeaf"> 
                      no node!
                  </div>
              </div>
          </div>
      </div>
      <input type="button" value="保存树状图" onclick="javascript:saveTreeState()" />
      <input type="button" value="移动到叶子节点" onclick="javascript:moveToLeaf()" />
  </div>
  <br/>
  <a href="/demo/depts!exceptionDetail.do" target="dialog" rel="w_tabs" width="800" height="600">显示异常列表</a>
</div>
