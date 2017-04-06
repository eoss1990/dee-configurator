<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>

<div class="pageContent">
    <form method="post" action="/dee/flowType!saveFlowType.do" class="pageForm required-validate" onsubmit="return validateCallback(this, editThenReloadZtree)">
        <div class="pageFormContent" layoutH="58">
            <div class="unit">
                <label>名称：</label>
                <input type="text" name="flowType.name" value="${flowTypeBean.FLOW_TYPE_NAME}" size="30" maxlength="50" class="required" />
            </div>
            <div class="unit">
                <label>排序：</label>
                <input type="text" name="flowType.sort" value="${flowTypeBean.FLOW_TYPE_ORDER}" size="30" maxlength="8" class="required number" />
            </div>
        </div>
        <div class="formBar">
            <input type="hidden" name="flowType.id" value="${flowTypeBean.FLOW_TYPE_ID}" />
            <ul>
                <li><div class="buttonActive"><div class="buttonContent"><button type="submit">确定</button></div></div></li>
                <li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
            </ul>
        </div>
    </form>
</div>