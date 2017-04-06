<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
 <script type="text/javascript" src="/styles/dee/js/flowbase/debugConsole.js"></script>  
	<form id="debugRes" method="post" action="/dee/flowbase!debugRes.do" class="pageForm required-validate" onsubmit="return validateCallback(this,debugDone)">		
		<input type="hidden" name="flowId" value="${flowId}"/>
		<div class="pageFormContent nowrap" id="outdiv" style="height:80%">
			<div class="tabs" width="100%" id="debugResult">
					<div class="tabsHeader">
						<div class="tabsHeaderContent">
							<ul>
								<li><a href="javascript:void(0)"><span>输入参数</span></a></li>
								<li><a href="javascript:void(0)"><span>输入document</span></a></li>
								<li><a id="debug_infor" href="javascript:void(0)"><span>信息</span></a></li>
								<li><a href="javascript:void(0)"><span>输出</span></a></li>
								<li><a href="javascript:void(0)"><span>参数</span></a></li>
								<li><a href="javascript:void(0)"><span>上下文</span></a></li>
								<li><input id="debugConsole_mata" name="debugConsole_mata" type="hidden"></li>
							</ul>
						</div>
					</div>
					<div class="tabsContent" id="inpanel" layoutH="325">
					<div>
							<table>
								<tbody>
									<c:forEach var="item" items="${params}" varStatus="status">
									<tr target="slt_uid" rel="${item.PARA_ID}">
										<td style="${item.DIS_NAME!=null&&item.DIS_NAME!=''?'display:none':''}"><input name="params[${status.index}].paraName" value="${item.PARA_NAME}" type="text" readonly></td>
										<td style="${item.DIS_NAME!=null&&item.DIS_NAME!=''?'':'display:none'}"><input name="params[${status.index}].disName" value="${item.DIS_NAME}" type="text" readonly></td>
										<td><input name="params[${status.index}].defaultValue" value="${item.PARA_VALUE}" type="text"></td>
									</tr>
									</c:forEach>	
								</tbody>
							</table>				
					</div>
					<div style="height:100%;"><textarea name="document" style="width:99%;height:97%;" value="${document}"></textarea></div>
					<div id="blockpanel" style="height:100%;"></div>
					<div style="height:100%;"><textarea id="debugConsole_out" name="debugConsole_out"  style="width:99%;height:97%;" readonly="true"></textarea></div>
					<div style="height:100%;"><textarea id="debugConsole_args" name="debugConsole_args" style="width:99%;height:97%;" readonly="true"></textarea></div>
					<div style="height:100%;"><textarea id="debugConsole_context" name="debugConsole_context" style="width:99%;height:97%;" readonly="true"></textarea></div>
					</div>
					<div class="tabsFooter">
						<div class="tabsFooterContent"></div>
					</div>
			</div>
			<div style="height:10%" id="pageheight">
				<table>
					<tr>
						<td>
							<label style="width:50px;">分页：第</label>
						</td>
						<td>
							<input name="Parameter_Paging_pageNumber" value="1" size="2" maxlength="8"/>
						</td>
						<td>
							<label style="width:50px;">页，每页</label>
						</td>
						<td>
							<input name="Parameter_Paging_pageSize" value="10" size="2" maxlength="8"/>
						</td>
						<td>
							<label>条</label>
						</td>
					</tr>
				</table>
			</div>
<!-- 			分页：第<input name="Parameter_Paging_pageNumber" value="1" size="2" maxlength="8"/>页，每页<input name="Parameter_Paging_pageSize" value="10" size="2" maxlength="8"/>条	 -->		
			<div class="formBar" style="height:10%" id="debugheight" >
				<ul>
					<li><div class="buttonActive" >
							<div class="buttonContent">
								<button type="button" onclick="submitDebug()">调试</button>
							</div>
						</div></li>
				</ul>
			</div>
	</div>
	</form>
