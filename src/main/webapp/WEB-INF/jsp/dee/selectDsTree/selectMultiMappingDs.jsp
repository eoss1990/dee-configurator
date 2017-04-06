<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/selectDsTree/selectMultiMappingDs.js" type="text/javascript"></script>
<script src="/styles/dee/js/common/ztreeSearch.js" type="text/javascript"></script>
<div id="multiMappingDiv" class="pageContent">
   <table width="100%" height="100%">
    <tr>
      <td width="42%" align="left">
     	<div class="unit">
		<label>数据源：</label>
		<select id="mappingDsType" name="mappingDsType">
        	<option value="2">NC元数据</option>
        	<option value="3">组织机构元数据</option>
        <c:forEach var="ds" items="${dsList}">
        	<option value="${ds.resource_id}">${ds.dis_name}</option>
		</c:forEach>
		</select>			
 		<input type="text" id="searchMultiMappingDs"   size="15" alt="回车查询"/>
     	</div>
		<div id="selectMultiMappingDsZtree" style="float:left; display:block; margin:2px; overflow:auto; width:320px; height:386px; border:solid 1px #CCC; line-height:21px; background:#FFF;">
			<ul id="mappingTree" class="ztree"></ul>
		</div>
	  </td>
      <td width="8%">
	  <a class='button' id="btnSource"><span>来源</span></a><br/><br/><br/>
	  <a class='button' id="btnTarget"><span>目标</span></a>
	  </td>
      <td width="25%">
		<div id="palMultiMappingLeft" class="panel" defH="345" style="width:180px;">
		<h1>来源</h1>
		<div class="pageFormContent">
	  	<div id="dSource" class="sortDrag"></div>
		</div>
		</div>
	  </td>
      <td width="25%">
		<div id="palMultiMappingRight" class="panel" defH="345" style="width:180px;">
		<h1>目标</h1>
		<div class="pageFormContent">
	  	<div id="dTarget" class="sortDrag"></div>
		</div>
		</div>
	  </td>
    </tr>
  </table>
  <div class="formBar">
	<ul>
		<li><div class="button"><div class="buttonContent"><button id="btnMultiOk" type="button">确定</button></div></div></li>
		<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
	</ul>
  </div>
</div>

