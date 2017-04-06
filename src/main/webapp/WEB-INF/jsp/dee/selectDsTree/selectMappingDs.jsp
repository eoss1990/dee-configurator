<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/selectDsTree/selectMappingDs.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>
<div class="pageContent">
     <div class="unit">
		<label>数据源：</label>
		<select id="mappingDsType" name="mappingDsType">
        	<option value="2">NC元数据</option>
        	<option value="3">组织机构元数据</option>
        <c:forEach var="ds" items="${dsList}">
        	<option value="${ds.resource_id}">${ds.dis_name}</option>
		</c:forEach>
		</select>			
		<input type="text" id="searchMappingDs"   size="15" alt="回车查询"/>
     </div>
	<div id="selectMappingDsZtree" style="overflow:auto; width:380px; height:366px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
		<ul id="mappingTree" class="ztree"></ul>
	</div>
	<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button id="btnOk" type="button">确定</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
			</ul>
	</div>
</div>

