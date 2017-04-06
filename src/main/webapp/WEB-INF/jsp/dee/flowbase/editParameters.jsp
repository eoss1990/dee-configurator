<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/dee/js/flowbase/parameters.js"></script>
	<form id="paramsForm" method="post" class="pageForm required-validate" onsubmit="return validateCallback(this)">
		<div class="pageFormContent nowrap" style="overflow: auto;" layoutH="265">
					<table class="list nowrap itemDetail" addButton="新建任务参数" width="100%">
						<thead>
							<tr>
								<th size="50" type="text" name="params[#index#].paraName"  fieldClass="required" fieldAttrs="{maxlength:20}">参数名称</th>
								<th size="50" type="text" name="params[#index#].disName" fieldClass="required" fieldAttrs="{maxlength:20}">显示名称</th>
								<th size="50" type="text" name="params[#index#].defaultValue" fieldAttrs="{maxlength:200}">默认值</th>
								<th type="del" width="30">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="${pbList}" varStatus="status" >
								<tr class="unitBox">
									<td>
										<input size="50" class="required textInput" type="text" maxlength="20" name="params[${status.index}].paraName" value="${item.PARA_NAME}">
									</td>
									<td>
										<input size="50" class="required textInput" type="text" maxlength="20" name="params[${status.index}].disName" value="${item.DIS_NAME}">
									</td>
									<td>
										<input size="50" class="textInput" type="text" maxlength="200" name="params[${status.index}].defaultValue" value="${item.PARA_VALUE}">
									</td>
									<td>
										<a class="btnDel " href="javascript:void(0)" onClick="javascript:delRow(this);">删除</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
		</div>
		<div class="formBar">
			<ul>
				<div class="buttonActive">
					<div class="buttonContent">
						<button type="button" onclick="submitParams(this)">保存</button>
					</div>
				</div>
			</ul>
		</div>
	</form>