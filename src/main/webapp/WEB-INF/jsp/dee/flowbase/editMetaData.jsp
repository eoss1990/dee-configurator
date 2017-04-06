<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/dee/js/flowbase/metadata.js"></script>
	<c:if test="${empty metaSize}">
		<input type="hidden" value="0" id="metaDataOrderMax" >
	</c:if>
	<c:if test="${!empty metaSize}">
		<input type="hidden" value="${metaSize}" id="metaDataOrderMax" >
	</c:if>
	
	<form id="metaForm" method="post" class="pageForm required-validate" onsubmit="return validateCallback(this)">
		<div class="pageFormContent nowrap"  layoutH="265">
<%-- 			<label for="dis_name" style="width:70px;">表单名称：</label>
			<input type="text" name="formName" maxlength="20" class="required" value="${metaHead.formName}" alt="表单名称"/><br><br>
					<!--<span class="info">class="required"</span>
                    --> --%>
					<table class="list nowrap itemDetail" addButton="新建元数据" id="mata" width="100%">
			
					<div class="buttonActive" style="position:absolute; top:10px; left:85px">
						<div class="buttonContent">

							<button type="button" onclick="loadmetadata()">自动载入元数据</button>
						</div>
					</div>
				
		
						<thead>
							<tr>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].formName" fieldClass="required" fieldAttrs="{maxlength:20}">结果集名称</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].dbFormName" fieldClass="required" fieldAttrs="{maxlength:20}">表名称</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].formDisName" fieldAttrs="{maxlength:20}">表显示名称</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="enum" name="meta[#index#].formType" enumUrl="/dee/flowbase!tabletype.do">表类型</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].name" fieldClass="required" fieldAttrs="{maxlength:20}">字段名称</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].display" fieldClass="required" fieldAttrs="{maxlength:20}">字段显示名称</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].refMainField" fieldStyle="display:none" fieldAttrs="{maxlength:20}">关联主表字段</th>
								<!-- <th type="text" name="meta[#index#].fieldType" size="12" fieldClass="required" fieldAttrs="{maxlength:10}">字段类型</th> -->
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="enum" name="meta[#index#].fieldType" enumUrl="/dee/flowbase!fieldtype.do">字段类型</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].fieldLength" fieldClass="required" fieldAttrs="{maxlength:20}">字段长度</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="enum" name="meta[#index#].isNull" enumUrl="/dee/flowbase!select.do" >是否为空</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="enum" name="meta[#index#].isPrimary" enumUrl="/dee/flowbase!select.do" >是否主键</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="text" name="meta[#index#].orderNo" fieldClass="required"    >排序号</th>
								<th style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" size="9" type="del" width="30">操作</th>
							</tr>
						</thead>
						<tbody id="trcounter">
							<c:forEach var="item" items="${metaList}" varStatus="status" >
								<tr class="unitBox" id="showlist">
									<td>
										<input size="9"  class="required textInput" type="text" maxlength="20" value="${item.formName}" name="meta[${status.index}].formName"/>
									</td>
									<td>
										<input size="9" class="required textInput" type="text" maxlength="20"  value="${item.dbFormName}" name="meta[${status.index}].dbFormName" />
									</td>
									<td>
										<input size="9" class="textInput" type="text" maxlength="20"  value="${item.formDisName}" name="meta[${status.index}].formDisName">
									</td>
									<td>
										<select  name="meta[${status.index}].formType" value="${item.formType}" onchange="setRefFieldNone(this);">
												<option value="master" ${'master' eq item.formType?'selected':''}>主表</option>
												<option value="slave" ${'slave' eq item.formType?'selected':''}>从表</option>
										</select>
									</td>
									<td>
										<input size="9" class="required textInput" type="text" maxlength="20"  value="${item.name}" name="meta[${status.index}].name">
									</td>
									<td>
										<input size="9" class="required textInput" type="text" maxlength="20"  value="${item.display}" name="meta[${status.index}].display">
									</td>
									<td>
  										<input size="9" id="connect" class="textInput"  type="text" maxlength="20" ${'master' eq item.formType?'style="display:none"':''}  value="${item.refMainField}" name="meta[${status.index}].refMainField"> 
									</td>
									<td>
										<select name="meta[${status.index}].fieldType" value="${item.fieldType}">
												<option value="varchar" ${'varchar' eq item.fieldType?'selected':''}>varchar</option>
												<option value="decimal" ${'decimal' eq item.fieldType?'selected':''}>decimal</option>
												<option value="timestamp" ${'timestamp' eq item.fieldType?'selected':''}>timestamp</option>
												<option value="longtext" ${'longtext' eq item.fieldType?'selected':''}>longtext</option>
												<option value="datetime" ${'datetime' eq item.fieldType?'selected':''}>datetime</option>
										</select>
									</td>
									<td>
										<input size="9" class="required textInput" type="text" maxlength="20" value="${item.fieldLength}" name="meta[${status.index}].fieldLength">
									</td>
									<td>
										<select name="meta[${status.index}].isNull">
											<c:if test="${item.isNull=='true'}">
												<option value="true" selected="selected">是</option>
												<option value="false">否</option>
											</c:if>
											<c:if test="${item.isNull=='false'}">
												<option value="true" >是</option>
												<option value="false" selected="selected">否</option>
											</c:if>
											<c:if test="${empty item.isNull}">
												<option value="true" >是</option>
												<option value="false" selected="selected">否</option>
											</c:if>
										</select>
									</td>
									<td>
										<select name="meta[${status.index}].isPrimary">
											<c:if test="${item.isPrimary=='true'}">
												<option value="true" selected="selected">是</option>
												<option value="false">否</option>
											</c:if>
											<c:if test="${item.isPrimary=='false'}">
												<option value="true" >是</option>
												<option value="false" selected="selected">否</option>
											</c:if>
											<c:if test="${empty item.isPrimary}">
												<option value="true" >是</option>
												<option value="false" selected="selected">否</option>
											</c:if>
										</select>
									</td>
									<td>
										<input size="9" class="required textInput" type="text"  onblur="CalcOrder(this);" value="${item.orderNo}"   name="meta[${status.index}].orderNo">
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
						
						<button type="button" onclick="submitMeta(this)">保存</button>
					</div>
				</div>
			</ul>
		</div>
	</form>