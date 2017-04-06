<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript" src="/styles/dee/js/restSrv/flowRestAdr.js"></script>
<div id="restFlow" class="pageContent">
    <div class="pageFormContent nowrap" layoutH="58" style="overflow: auto;"><!-- style="height: 190px; overflow: auto;" -->
		<dl>
			<dd>
			<label for="flow_name" style="width:110px;">交换任务：</label>
			<input type="hidden" id="flow_id" value="">
			<input type="text" id="flow_name" maxlength="32" value="" readonly="readonly"/>					
			<a href="/dee/restSrv!deeList.do" id="flowList" class="btnLook"  lookupGroup="flow">交换任务</a>	
      		</dd>
      	</dl>
      	<dl>
      		<dd>
			<label>参数列表：</label>
        <table id="restTb" class="list nowrap" width="100%">
          <thead>
            <tr>
              <th type="text" width="120">参数名称</th>
              <th type="text" width="120">参数类型</th>
            </tr>
          </thead>
          <tbody>
          </tbody>
        </table>
      		</dd>
      	</dl>
      <div class="unit">
			<label id="restLab" style="width:310px;"></label>
      </div>
	</div>
    <div class="formBar">
      <div class="unit">
				<label>服务地址：</label>
				<input type="text" id="rest_url" name="rest_url" value="" style="width:410px;" readonly="readonly"/>
			    <button type="button" id="btnRestCopy">复制</button>
			    <button type="button" class="close">取消</button>
      </div>
    </div>
</div>