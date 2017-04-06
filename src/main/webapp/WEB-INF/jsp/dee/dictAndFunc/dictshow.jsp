<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/dictAndFunc/dictshow.js" type="text/javascript"></script>
<div class="pageContent">
<form id="frmDsShow" method="post" action="/dee/dictAndFunc!saveDict.do?navTabId=dict&callbackType=closeCurrent" class="pageForm required-validate" onsubmit="return checkInput(this);">
<div  layoutH="60" class="pageFormContent nowrap">
    	<input type="hidden" id="dictTypeParam" value="${dictType}">
        <input type="hidden" id="resource_id" name="resource_id" value="${bean.resource_id}"/>
		<input type="hidden" id="resource_name" name="resource_name" value="${bean.resource_name}" />
			<p>
				<label>数据字典名称：</label>
				<input type="text" id="dis_name" name="dis_name" class="required" maxlength="20" value="${bean.dis_name}" alt="请输入数据字典名称"/>
			</p>
			<p>
				<label>数据字典类型：</label>
			<select id="dictType" name="dictType">
				<option value="jdbc" <c:if test="${dictType == 'jdbc'}">selected</c:if> >JDBC字典</option>
				<option value="static" <c:if test="${dictType == 'static'}">selected</c:if> >枚举字典</option>
				<option value="func" <c:if test="${dictType == 'func'}">selected</c:if> >系统函数</option>
			</select>
			</p>
			<table width="100%"><tr><td></td></tr></table>
			<span id="jdbcspan">
			<p>
				<label>数据源：</label>
				<select id="ref_id" name="ref_id">
            	<c:forEach var="ds" items="${dsList}">
        		<c:if test="${ds.resource_id==jdbcDr.dataSource}">
        		<option value="${ds.resource_id}" selected>${ds.dis_name}</option>
        		</c:if>
        		<c:if test="${ds.resource_id!=jdbcDr.dataSource}">
        		<option value="${ds.resource_id}">${ds.dis_name}</option>
        		</c:if>
				</c:forEach>
				</select>			
			</p>
			<p>
				<label>表名：</label>
				<input type="text" id="tableName" name="tableName" value="${jdbcDr.tableName}" size="20"/>
			</p>
			<p>
				<label>    枚举字段：</label>
				<input type="text" id="keyColumn" name="keyColumn" value="${jdbcDr.keyColumn}" />
			</p>
			<p>
				<label>    对应值：</label>
				<input type="text" id="valueColumn" name="valueColumn" value="${jdbcDr.valueColumn}" />
			</p>
			</span>
			<span id="staticspan">
			  <div>
					<table class="list nowrap itemDetail" addButton="新建" width="98%">
						<thead>
							<tr>
								<th type="text" width="120" name="dict[#index#].paramName" size="22" fieldClass="required" fieldAttrs="{maxlength:20}">枚举KEY</th>
								<th type="text" width="120" name="dict[#index#].paramValue" size="22" fieldClass="required" fieldAttrs="{maxlength:20}">枚举值</th>
								<th type="del" width="40">操作</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="item" items="${staticDr}" varStatus="status">
							<tr class="unitBox">
								<td>
									<input class="required textInput" type="text" maxlength="20" size="22" value="${item.paramName}" name="dict[${status.index}].paramName">
								</td>
								<td>
									<input class="required textInput" type="text" maxlength="20" size="22" value="${item.paramValue}" name="dict[${status.index}].paramValue">
								</td>
								<td>
									<a href="javascript:void(0)" class="btnDel">删除</a>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</span>
			<span id="funcspan">
			<p>
				<label>系统函数：</label>
			<select id="func_name" name="func_name">
				<option value="curr" <c:if test="${bean.func_name == 'curr'}">selected</c:if>>CURR</option>
				<option value="seq" <c:if test="${bean.func_name == 'seq'}">selected</c:if>>SEQ</option>
			</select>
			</p>
			<p>
				<label>参数：</label>
				<input type="text" id="resource_para" name="resource_para" value="${funcDr.resource_para}" />
			</p>
			</span>
</div>

<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
			</ul>
</div>
</form>
</div>
