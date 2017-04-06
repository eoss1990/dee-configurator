<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<div class="accordion" fillSpace="sideBar">
    <c:if test="${num==0||num==null}">
        <script src="/styles/management/js/index/index-script.js" type="text/javascript"></script>
    	<div class="accordionContent">
            <ul id="flowList" class="ztree"></ul>
    	</div>
    </c:if>
    <c:if test="${num==1||num==null}">
        <div class="accordionContent">
            <ul class="tree treeFolder">
                <li><a href="/dee/datasource!dslist.do" target="navTab" rel="ds">数据源管理</a></li>
                <li><a href="/dee/dictAndFunc!dictlist.do" target="navTab" rel="dict">字典管理</a></li>
                <li><a href="/dee/schedule!scheduleList.do" target="navTab" rel="scheduleList">定时器管理</a></li>
                <li><a href="/dee/redo!syncListLog.do" target="navTab" rel="deeRedoSyncListLog">任务监控</a></li>
            </ul>
        </div>
    </c:if>
    <c:if test="${num==2||num==null}">
        <div class="accordionContent">
            <ul class="tree treeFolder">
                <li><a href="/dee/login!editPwd.do" target="dialog" mask="true" rel="w_tabs" title="密码修改" width="600" height="300">密码修改</a></li>
                <li><a href="/dee/login!paramsSetting.do" target="navTab" rel="deeParameterSetting">系统参数配置</a></li>
                <li><a href="/dee/serial!viewReg.do" target="dialog" mask="true" rel="w_tabs" title="注册授权" width="600" height="300">注册授权</a></li>
                <li><a href="/dee/restSrv!flowRestAdr.do" target="dialog" mask="true" rel="w_tabs" title="REST服务查询" width="800" height="400">REST服务查询</a></li>
            </ul>
        </div>
    </c:if>
    <c:if test="${num==3||num==null}">
        <div class="accordionContent">
            <ul class="tree treeFolder">
                <li><a href="/dee/codelib!list.do" target="navTab" mask="true" rel="codelib" title="用户代码库" width="600" height="300">用户代码库</a></li>
            </ul>
        </div>
    </c:if>
</div>
