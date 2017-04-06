<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ include file="/include.inc.jsp"%>
<%@ taglib uri="http://dee.seeyon.com/taglib/str" prefix="dee_str"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/restprocessor.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/restprocessor_valid.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>
<script type="text/javascript">
    function delRow(obj) {
        var tr = $(obj).closest("tr");
        tr.remove();
    }
</script>

<div class="pageContent" id="a8restProDiv${resourceId}">
    <form method="post" action="/dee/restprocessor!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
          id="restProcessorForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone);">
        <div id="restBig${resourceId}" class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
            <jsp:include page="resourceType.jsp"></jsp:include>
            <p>
            </p>
            <p>
                <label>名称：</label>
                <input type="text" name="bean.dis_name" value="${bean.dis_name}" class="required uInput" maxlength="50"/>
            </p>
            <p>
                <label>地址：</label>
                <input type="text" id="rest_address" name="restProcessorBean.address" value="${restProcessorBean.address}" class="required uInput"/>
            </p>
            <p>
                <label>用户名：</label>
                <input type="text" id="rest_adminUserName" name="restProcessorBean.adminUserName" value="${restProcessorBean.adminUserName}" class="required uInput" />
            </p>
            <p>
                <label>密码：</label>
                <input type="text" id="rest_adminPassword" name="restProcessorBean.adminPassword" value="${restProcessorBean.adminPassword}" class="required uInput"/>
            </p>
        	<p>
        		<label>接口类型：</label>
        		<select id="selInterface" name="selInterface" class="uSelect"  <c:if test="${resourceId != null && resourceId != '' }">disabled</c:if>>
        		<c:choose>
        			<c:when test="${resourceType=='1001'}">
         				<option value="34" >axis2</option>
        				<option value="36" selected>rest</option>
        			</c:when>
        			<c:otherwise>
          				<option value="8" >axis2</option>
        				<option value="37" selected>rest</option>
        			</c:otherwise>
        		</c:choose>
        		</select>
       		</p>
            <p>
                <label>服务：</label>
                <select id="rest_service" name="restProcessorBean.serviceId" class="uSelect required">
                    <option value="">请选择</option>
                    <c:forEach var="item" items="${serviceBeans}">
                        <option value="${item.serviceId}" <c:if test="${restProcessorBean.serviceId==item.serviceId}">selected </c:if> >${item.serviceName}</option>
                    </c:forEach>
                </select>
            </p>
            <p>
                <label>方法：</label>
                <select id="rest_function" name="restProcessorBean.functionId" class="uSelect required" functionId="${restProcessorBean.functionId}">
                    <option value="">请选择</option>
                </select>
            </p>
            <p>
                <label>返回类型：</label>
                <select id="rest_responseType" name="restProcessorBean.responseType" class="uSelect required">
                    <c:forEach items="${responseTypes}" var="item" varStatus="var">
                        <option value="${item}" <c:if test="${restProcessorBean.responseType == item}">selected </c:if>>${item}</option>
                    </c:forEach>
                </select>
            </p>
            <p>
                <label>返回名称：</label>
                <input type="text" name="restProcessorBean.responseName" value="${restProcessorBean.responseName}" class="required uInput"/>
            </p>
            <p id="mergeToDocumentP" <c:if test="${retFlag!='true'}"> style="display: none;" </c:if> >
                <label>合并到文档：</label>
                <input type="checkbox" name="restProcessorBean.mergeToDocument" value="${restProcessorBean.mergeToDocument}" <c:if test="${restProcessorBean.mergeToDocument =='true'}">checked</c:if> onclick="mergeToDocumentClick(this);" />
            </p>
            <br/>
            <br/>
            <br/>
            <table class="list nowrap" addButton="新建参数" width="100%">
                <thead>
                <tr>
                    <th type="text" width="20%" name="items[#index#].key" fieldClass="required uInput" fieldAttrs="{maxlength:100}">参数名称</th>
                    <th type="text" width="30%" name="items[#index#].value" fieldClass="uInput" fieldAttrs="{maxlength:100}">参数值</th>
                </tr>
                </thead>
                <tbody id="rest_param">
                <c:forEach items="${restParams}" var="item" varStatus="var">
                    <tr class="unitBox">
                        <td><p>${dee_str:dropDeeCast(item.paramName)}</p></td>
                        <td>
                            <input type='text' name='restParams[${var.index}].showValue' value='${item.showValue}' ${dee_str:createDeeHref(item.paramName)} <c:if test="${item.isRequired != '1'}"> class='required textInput' </c:if> />
                            <input type='hidden' value='${item.paramValue}' name='restParams[${var.index}].paramValue' />
                            <input type='hidden' value='${item.paramName}' name='restParams[${var.index}].paramName' />
                            <input type='hidden' value='${item.paramType}' name='restParams[${var.index}].paramType' />
                            <input type='hidden' value='${item.isRequired}' name='restParams[${var.index}].isRequired' />
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div id="pA8flow" <c:if test="${isShowTab != 'true'}"> style="display: none;" </c:if>>
            <table id="a8flow_table${resourceId}" class="list nowrap itemDetail" addButton="新建外键关联" width="100%">
                <thead>
                <tr>
                    <th type="text" width="200" name="keyItems[#index#].key" size="20" fieldClass="required" fieldAttrs="{maxlength:100}">目标表</th>
                    <th type="textarea" width="300" name="keyItems[#index#].value" cols="60" rows="2" fieldClass="required">关联外键</th>
                    <th type="del" width="60">操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${keyItems}" var="item" varStatus="var">
                    <tr class="unitBox">
                        <td><input type="text" name="keyItems[${var.index}].key" submitName="keyItems[${var.index}].key" value="${item.key}" class="required textInput" /></td>
                        <td>
                            <textarea class="required textInput valid" rows="2" cols="60" name="keyItems[${var.index}].value"  submitName="keyItems[${var.index}].value">${item.value}</textarea>
                            <a href="javascript:void(0)"  name="deeTreeSelect">选择</a>
                        </td>
                        <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a> </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </div>
        <div class="formBar">
            <ul>
                <li>
                    <div class="buttonActive">
                        <div class="buttonContent">
               				<input type="hidden" id="flowId" name="flowId" value="${flowId}" />
              				<input type="hidden" id="resourceId" name="resourceId" value="${resourceId}" />
              				<input type="hidden" id="sort" name="sort" value="${sort}" />
                            <button type="button" onclick="javascript:submitRestProcessorForm(this);">保存</button>
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
        <div id="restSelectCopy" class="pageForm" style="display:none;position: absolute; width:400px; height:160px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
            <ul id="restDsTreeCopy" class="ztree" ></ul>
        </div>
        <div id="tableSelect${resourceId}" class="pageForm" style="display:none;position: absolute; width:452px; height:100px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
            <label>请选择数据源：</label><select id="dbList${resourceId}"></select>
            <input type="text" id="searchText${resourceId}"   size="15" alt="回车查询"/><br/>
            <ul id="dsTree${resourceId}" class="ztree" ></ul>
        </div>
    </form>
</div>
<script type="text/javascript">
    $(function () {
        // 窗口ID
        var dialogId = "${resourceId}";
        if (!dialogId) {
            dialogId = "";
        }

        $("a[name='deeTreeSelect']", $("#a8flow_table"+dialogId)).die().live('click', function () {
            showTree($(this).parents('tr:first'));
        });

        $("#dbList"+dialogId).live('change', function () {
            var url = "/dee/a8commonws!selectDs.do?dsId=" + $(this).val();
            ajaxTodo(url, function (json) {
                //清空查询条件
                $("#searchText"+dialogId).val("");

                $.fn.zTree.init($("#dsTree"+dialogId), a8commonWsSetting, json.jsonData);
            });
        });
    });
</script>
