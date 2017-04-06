<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/flowBase.js" type="text/javascript"></script>
<script type="text/javascript" src="/styles/dee/js/flowbase/debugConsole.js"></script>
<div class="pageFormContent nowrap" style="padding:5px;">

<div name="${flowBean.FLOW_ID}" class="pageFormContent nowrap" style="padding:5px;">

		<div id="panelBase" class="panel collapse" style="margin-bottom:5px;" >
			<h1>基础信息</h1>
			<div>
				<form id="flowBaseForm" method="post" action="/dee/flowbase!update.do?navTabId=deeList" class="pageForm required-validate" flow_name="${flowBean.DIS_NAME}" onsubmit="return validateCallback(this,flowBaseFormDone)">
						<p>
							<label>任务名称：</label>
							<input type="hidden" name="uid" value="${flowBean.FLOW_ID}" id="uid"/>
							<input type="text" name="flow_name" maxlength="50" class="required" value="${flowBean.DIS_NAME}"/>
                            <input type="hidden" name="real_flow_name" value="${flowBean.DIS_NAME}"/>
						</p>
						<p>
                            <c:if test="${flowBean.EXT1=='24'}">
								<label style="display: none;">允许异常处理：</label>
								<input style="display: none;" type="checkbox" name="listener" value="24" alt="syncListener" ${flowBean.EXT1=='24'?'checked':'checked'}>
                            </c:if>
                            <c:if test="${flowBean.EXT1==null}">
								<label>允许异常处理：</label>
								<input type="checkbox" name="listener" value="24" alt="syncListener" ${flowBean.EXT1=='24'?'checked':'checked'}>
                           	</c:if>
                           	<c:if test="${flowBean.EXT1=='-1'}">
								<label>允许异常处理：</label>
								<input type="checkbox" name="listener" value="24" alt="syncListener" ${flowBean.EXT1=='24'?'checked':'-1'}>
                           	</c:if>
						</p>
						<p>
							<label>交换任务分类：</label>
							<input type="hidden" id="flow_type_id" name="flow_type_id" value="${empty flowBean.flowType.FLOW_TYPE_ID?'0':flowBean.flowType.FLOW_TYPE_ID}"/>
							<input type="hidden" id="parent_id" name="parent_id" value="${empty flowBean.flowType.PARENT_ID?'-1':flowBean.flowType.PARENT_ID}"/>
							<input type="text" id="flow_type_name" name="flow_type_name" maxlength="32" class="required" value="${empty flowBean.flowType.FLOW_TYPE_NAME?'交换任务分类':flowBean.flowType.FLOW_TYPE_NAME}" disabled/>
							<a href="/dee/flowbase!treeBringBack.do" id="flowTypeBringBack" class="btnLook"  lookupGroup="FlowTypeBean"  width="350" height="400">任务分类</a>	
						</p>
					<div class="formBar">
						<ul>
							<li><div class="buttonActive">
									<div class="buttonContent">
										<button type="button" onclick="submitFlowBaseForm(this)">保存</button>
									</div>
								</div></li>
						</ul>
					</div>
				</form>
			</div>
		</div>
	<div class="tabs">
		<div class="tabsHeader">
			<div class="tabsHeaderContent">
				<ul>
					<li><a href="javascript:;" ><span class="back">任务配置</span></a></li>
					<li><a href="/dee/flowbase!editMetaData.do?uuid=${flowBean.FLOW_ID}" class="j-ajax"><span class="back">元数据</span></a></li>
					<li><a href="/dee/flowbase!editParameters.do?uuid=${flowBean.FLOW_ID}" class="j-ajax"><span class="back">任务参数</span></a></li>
					<li><a id="debug" name="debug" href="/dee/flowbase!debug.do?flowId=${flowBean.FLOW_ID}" class="j-ajax"><span>调试信息</span></a></li>
				</ul>
			</div>
		</div>
		<div class="tabsContent" id="outpanel" layoutH="210">
		<div style="text-align:center;height:100%">
			<table id="flowbaseTab" width="100%">
			<tr width="100%">
				<td>
					<img id="source_img" style="margin: 0 auto;" src="/styles/dee/themes/images/${empty fsSource?'source_l.jpg':'source_h.jpg'}" alt="来源"/>
				</td>
				<td>
					<img id="exchange_img" style="margin: 0 auto;" src="/styles/dee/themes/images/${empty fsExchange?'exchange_l.jpg':'exchange_h.jpg'}"  alt="转换"/>
				</td>
				<td>
					<img id="target_img" style="margin: 0 auto;" src="/styles/dee/themes/images/${empty fsTarget?'target_l.jpg':'target_h.jpg'}"  alt="目标"/>
				</td>
			</tr>
			<tr width="100%">
			<td width="33.3%">
				<div class="panel addbtn" minH="100" style="width:90%;margin: 0 auto;">
					<h1>来源配置</h1>
					<div id="sourceProcess" class="pageFormContent" layoutH="300">
						<div class="sortDrag">
							<c:forEach var="item" items="${fsSource}" >
								<div id="${item.deeResource.resource_id}" name="sourceProcess" isDrag="false" style="border:1px solid #B8D0D6;padding:5px;margin:5px;height:20px;cursor:pointer;position:relative;"><h2>${item.deeResource.dis_name}</h2></div>
							</c:forEach>
						</div>
					</div>
				</div>
			</td>
			<td width="33.3%">
					<div class="panel addbtn" minH="100" style="width: 90%;margin: 0 auto;">
						<h1>转换配置</h1>
						<div id="exchangeProcess" class="pageFormContent" layoutH="300">
							<div class="sortDrag">
								<c:forEach var="item" items="${fsExchange}" >
									<div id="${item.deeResource.resource_id}" name="exchangeProcess" isDrag="false" style="border:1px solid #B8D0D6;padding:5px;margin:5px;height:20px;cursor:pointer;position:relative;"><h2>${item.deeResource.dis_name}</h2></div>
								</c:forEach>
							</div>
						</div>
					</div>
			</td>
			<td width="33.3%">	
			    <div class="panel addbtn" minH="100" style="width: 90%;margin: 0 auto;">
					<h1>输出目标</h1>
					<div id="targetProcess" class="pageFormContent" layoutH="300">
						<div class="sortDrag">
							<c:forEach var="item" items="${fsTarget}" >
								<div id="${item.deeResource.resource_id}" name="targetProcess" isDrag="false" style="border:1px solid #B8D0D6;padding:5px;margin:5px;height:20px;cursor:pointer;position:relative;"><h2>${item.deeResource.dis_name}</h2></div>
							</c:forEach>
						</div>
					</div>
				</div>
			</td>
			</tr>
			</table>
		</div>
		<div style="height:100%;"></div>
		<div style="height:100%;"></div>
		<div style="height:100%;"></div>
		</div>
		<div class="tabsFooter">
			<div class="tabsFooterContent"></div>
		</div>
	</div>

</div>
</div>