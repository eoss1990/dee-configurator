<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/a8form_writeback_writer.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>
<script type="text/javascript">
    function delRow(obj) {
        var tr = $(obj).closest("tr");
        tr.remove();
    }
</script>

<div class="pageContent">
    <form method="post" action="/dee/a8form_writeback_writer!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav"
          id="writeBackWriterForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
        <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
            <jsp:include page="resourceType.jsp"></jsp:include>
            <div class="unit">
                <label>名称：</label>
                <input type="text" id="dis_name" name="bean.dis_name" class="uInput required" value="${bean.dis_name}" maxlength="50"/>
            </div>
            <div class="unit">
                <label>数据源：</label>
                <select id="ref_id" name="writerBean.dataSource" class="uSelect required" onchange="javascript:selectWriteBackRefId(this);">
                    <option value="">请选择</option>
                    <c:forEach var="ds" items="${dbList}">
                        <c:choose>
                            <c:when test="${ds.resource_id==writerBean.dataSource}">
                                <option value="${ds.resource_id}" selected>${ds.dis_name}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${ds.resource_id}">${ds.dis_name}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="unit">
                <label>表单：</label>
                <div id="writeBackSelect" class="pageForm" style="display:none;position: absolute; width:422px; height:200px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
                    <ul id="writeBackDsTree" class="ztree" ></ul>
					<div style="position:absolute; right:5px; top:5px;">
						<input type="text" id="searchText${resourceId}"   size="15" />
					</div>
                </div>
                <input id="writerbean_formname" type="text" class="uInput required" name="writerBean.formName" value="${writerBean.formName}" readonly onclick="javascript:setWriteBackSelectTree(this);" />
                <input id="writerbean_formid" type="hidden" name="writerBean.formId" value="${writerBean.formId}" />
            </div>
            <table class="list nowrap" addButton="新建参数" width="100%">
                <thead>
                <tr>
                    <th type="text" width="200" name="items[#index#].key" fieldClass="required uInput" fieldAttrs="{maxlength:100}">字段名</th>
                    <th type="text" width="300" name="items[#index#].value" fieldClass="uInput" fieldAttrs="{maxlength:100}">字段值</th>
                    <th type="del" width="40">操作</th>
                </tr>
                </thead>
                <tbody id="writeback_param">
                <c:forEach items="${formWriteDtos}" var="item" varStatus="var">
                    <tr class="unitBox">
                        <td>
                            <label>${item.name}</label>
                            <input type="hidden" name="formWriteDtos[${var.index}].id" value="${item.id}" />
                            <input type="hidden" name="formWriteDtos[${var.index}].name" value="${item.name}" />
                        </td>
                        <td><input type='text' name="formWriteDtos[${var.index}].value" value='${item.value}' maxlength="100" class='required textInput' /></td>
                        <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="formBar">
            <ul>
                <li>
                    <div class="buttonActive">
                        <div class="buttonContent">
                            <input type="hidden" name="flowId" value="${flowId}" />
                            <input type="hidden" name="resourceId" value="${resourceId}" />
                            <input type="hidden" name="sort" value="${sort}" />
                            <button type="button" onclick="javascript:submitWriteBackWriterForm(this);">保存</button>
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