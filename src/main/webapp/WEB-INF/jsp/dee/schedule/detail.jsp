<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <%--信息提示标志位 --%>
<%--     <input type="hidden" id="msgID" value="${param.msg}"/> --%>
<style>.hide{ display:none;}</style>
<script src="/styles/dee/js/schedule/detail.js" type="text/javascript"></script>
<div class="pageContent">
    <form action="/dee/schedule!save.do?callbackType=closeCurrent&navTabId=scheduleList" id="sourceform" class="pageForm required-validate" onSubmit="return validateCallback(this,dialogAjaxDone)" method="post">
    	<div class="pageFormContent nowrap" layoutH="58" style="overflow: auto;"><!-- style="height: 190px; overflow: auto;" -->
    		<dl>
				<dd>
					<label for="dis_name" style="width:110px;">定时器名称：</label>
					<input type="text" id="dis_name" name="bean.dis_name" class="required" value="${bean.dis_name}"/>
				</dd>
			</dl>
			<dl>
				<dd>
					<label for="quartz_code" style="width:110px;">执行方式：</label>
					<input name="bean.quartz_code" id="quartz_code" value="${bean.quartz_code}" type="hidden"/>
					<select  style="width:80px" id="quartz_type" name="quartz_type">
						<option value="0">时间间隔</option>
						<option value="1">固定时间</option>
					</select>
					<span id="intervalCtl" class="<c:if test="${retFixed == 1}"> hide</c:if>">
					<select style="width:165px" id="quartz_cnt" name="qaertz_cnt">
						<c:forEach var="i" begin="1" end="60" step="1"> 
						<option value="${i}">${i}</option>
						</c:forEach>
					</select>	
					<select style="width:60px" id="quartz_qty" name="quartz_qty">
						<option value="1">分钟</option>
						<option value="2">小时</option>
						<option value="3">天</option>
						<option value="4">周</option>
						<option value="5">月</option>
					</select>	
					</span>			
		            <span id="fixedCtl" class="<c:if test="${retFixed == 0}"> hide</c:if>">
		            	<label style="width:15px;">每</label>
						<select style="width:40px" id="quartz_mt" name="quartz_mt">
							<option value="1">天</option>
							<option value="2">周</option>
							<option value="3">月</option>
						</select>
						<span id="fixedInnerCtl">
						<select style="width:40px" id="quartz_day" name="quartz_day">
							<c:forEach var="i" begin="1" end="31" step="1"> 
							<option value="${i}">${i}</option>
							</c:forEach>
						</select>
						<label style="width:15px;">日</label>
						</span>	
						<span id="fixedInnerOth">
						<select style="width:55px" id="quartz_week" name="quartz_week">
							<option value="1">星期一</option>
							<option value="2">星期二</option>
							<option value="3">星期三</option>
							<option value="4">星期四</option>
							<option value="5">星期五</option>
							<option value="6">星期六</option>
							<option value="7">星期日</option>
						</select>
						</span>	
						<select style="width:40px" id="quartz_hours" name="quartz_hours">
							<c:forEach var="i" begin="0" end="23" step="1"> 
							<option value="${i}">${i}</option>
							</c:forEach>
						</select>
						<label style="width:15px;">时</label>
						<select style="width:40px" id="quartz_mint" name="quartz_mint">
							<c:forEach var="i" begin="0" end="59" step="1"> 
							<option value="${i}">${i}</option>
							</c:forEach>
						</select>
						<label style="width:15px;">分</label>	
					</span>	
				</dd>
				
			</dl>
			<dl>
				<dd>
					<label for="isEnable" style="width:110px;">是否启用：</label>
					<input type="radio" id="isEnable" name="isEnable" value="1" <c:if test="${bean.enable}">checked</c:if>/> 启用
					<input type="radio" id="isEnable" name="isEnable" value="0" <c:if test="${!bean.enable}">checked</c:if>/> 停用
				</dd>
			</dl>
			<dl>
				<dd>
					<label for="address" style="width:110px;">交换任务：</label>
					<input type="hidden" id="flow_id" name="bean.flow.FLOW_ID" value="${bean.flow.FLOW_ID}">
					<input type="text" id="flow_name" name="bean.flow.DIS_NAME" maxlength="32" class="required" value="${bean.flow.DIS_NAME}" disabled/>					
					<a href="/dee/schedule!deeList.do" id="flowList" class="btnLook"  lookupGroup="flow">交换任务</a>	
				</dd>
			</dl>
		</div>
       	<input type="hidden" id="schedule_id" name="bean.schedule_id" value="${bean.schedule_id}"/>
	    <input type="hidden" id="schedule_name" name="bean.schedule_name" value="${bean.schedule_name}"/>

		<div class="formBar">
			<ul>
				<li>
		              <div class="button">
			            <div class="buttonContent">
			              <button type="button" id="submitButton"  name="submitButton">保存</button>
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