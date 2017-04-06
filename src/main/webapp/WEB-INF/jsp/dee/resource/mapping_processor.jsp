<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
 
<link rel="stylesheet" href="/codemirror-4.2/lib/codemirror.css">
<script src="/codemirror-4.2/lib/codemirror.js"></script>
<script src="/codemirror-4.2/lib/formatting.js"></script>
<script src="/codemirror-4.2/mode/xml/xml.js"></script>
<style>.CodeMirror {border-top: 1px solid #500; border-bottom: 1px solid #500;}</style>
<style>.mappingBtn{cursor: pointer;display: block;float: left;height: 23px;line-height: 23px;overflow: hidden;font-family: Arial,sans-serif;font-size: 12px;margin: 0;}</style>
<script src="/styles/dee/js/resource/mapping.js" type="text/javascript"></script>
<div class="pageContent">
  <form id="frmMapping" method="post" action="/dee/mapping!saveMappingProcessor.do?callbackType=closeCurrent&navTabId=exchangeBaseNav" 
			class="pageForm required-validate" onsubmit="return checkInput(this);">
        <input type="hidden" id="flowId" name="flowId" value="${flowId}" />
        <input type="hidden" name="resourceId" value="${resourceId}" />
        <input type="hidden" name="sort" value="${sort}" />
		<input type="hidden" id="bean.ref_id" name="bean.ref_id" value="${bean.ref_id}" />
        <input type="hidden" id="mapIndex" name="mapIndex" value="0" />
	<div class="pageFormContent">
    <jsp:include page="resourceType.jsp"></jsp:include>
    <div class="unit">
		<label>名 称：</label>
		<input type="text" id="mappingDisName" name="bean.dis_name" value="${bean.dis_name}" maxlength="50" class="uInput required" alt="请输入名称"/>
    </div>
    <div class="unit">
		<label>过滤无映射数据：</label>
		<input type='checkbox' id='transNoMapping' name='transNoMapping' <c:if test="${bean.dr.transNoMapping}">checked</c:if> value='1'/>
    </div>
    </div>
	<div id="mappingTabs" class="tabs" eventType="abc"><!-- eventType赋值任意(非click,hover)值使切换失效 -->
		<div class="tabsContent">
			<div>
				<div class="panelBar">
					<ul class="toolBar">
						<li><a id="addBtn" class="add"><span>新增</span></a></li>
						<li><a id="moreBtn" class="mappingBtn"><img src="/styles/dee/themes/images/ic_tool_confige.png">批量配置</img></a></li>
						<li><a id="loadBtn" class="mappingBtn"><img src="/styles/dee/themes/images/ic_tool_load_field.png">载入来源字段</img></a></li>
						<input type='checkbox' id="chk" name='chk' value='${refMapRb.chk}' <c:if test="${refMapRb.chk == 'CHN'}">checked</c:if>/>是否使用到A8表单
					</ul>
				</div>
				<div id="mappingProcessorDiv" style="border:1 solid #B8D0D6;overflow:auto;">
				<table width="100%" height="100%">
				<tr>
					<td>
					<div id="palMappingLeft" class="panel"  defH="250" style="width:400px;">
					<h1>来源</h1>
					<div class="pageFormContent">
	  				<div id="dLeft" class="sortDrag">
						<c:if test="${fn:length(refMapRb.sourceTableName)-1>=0}">
	                	<c:forEach var="i" begin="0" end="${fn:length(refMapRb.sourceTableName)-1}">
							<div style='border:0 solid #B8D0D6;padding:5px;margin:1px;width:350px;overflow:hidden;height:21px;'><table><tr>
							<td>
							<input type='text' name='mapLeft' value='${refMapRb.sourceTableDisplay[i]}/${refMapRb.sourceColumnDisplay[i]}' title='${refMapRb.sourceTableDisplay[i]}/${refMapRb.sourceColumnDisplay[i]}'/>
							<input type='hidden' name='sourceTableName' value='${refMapRb.sourceTableName[i]}'/>
							<input type='hidden' name='sourceColumnName' value='${refMapRb.sourceColumnName[i]}'/>
							<input type='hidden' name='sourceTableName2' value='${refMapRb.sourceTableDisplay[i]}'/>
							<input type='hidden' name='sourceColumnName2' value='${refMapRb.sourceColumnDisplay[i]}'/>
							</td>
							<td>
								<select id='selType' name='selType'>
								<c:choose>
								<c:when test="${'' == refMapRb.mapping[i]}">
       								<option value="" selected>无</option>
 								</c:when>
 	 							<c:otherwise>
       								<option value="">无</option>
 	 							</c:otherwise>
								</c:choose>
    							<c:forEach var='ds' items='${dsList}'>
								<c:choose>
								<c:when test="${ds.key == refMapRb.mapping[i]}">
       								<option value="${ds.key}" selected>${ds.value}</option>
 								</c:when>
 	 							<c:otherwise>
       								<option value="${ds.key}">${ds.value}</option>
 	 							</c:otherwise>
								</c:choose>
								</c:forEach>
								</select>
							</td>
							<td><a id='shwMap'></a></td>
                            <td><a href="javascript:;" class='map_drag'></a></td></tr></table></div>
	                	</c:forEach>
						</c:if>
	  				</div>
					</div>
					</div>
					</td>
					<td>
					<div id="palMappingRight" class="panel" defH="250" style="width:320px;">
					<h1>目标</h1>
					<div class="pageFormContent">
	  				<div id="dRight" class="sortDrag">
						<c:if test="${fn:length(refMapRb.targetTableName)-1>=0}">
	                	<c:forEach var="i" begin="0" end="${fn:length(refMapRb.targetTableName)-1}"> 
							<div style='border:0 solid #B8D0D6;padding:5px;margin:1px;width:270px;overflow:hidden;height:21px;'><table><tr>
							<td>
							<input type='text' name='mapRight' value='${refMapRb.targetTableDisplay[i]}/${refMapRb.targetColumnDisplay[i]}' title='${refMapRb.targetTableDisplay[i]}/${refMapRb.targetColumnDisplay[i]}'/>
							<input type='hidden' name='targetTableName' value='${refMapRb.targetTableName[i]}'/>
							<input type='hidden' name='targetColumnName' value='${refMapRb.targetColumnName[i]}'/>
							<input type='hidden' name='targetTableName2' value='${refMapRb.targetTableDisplay[i]}'/>
							<input type='hidden' name='targetColumnName2' value='${refMapRb.targetColumnDisplay[i]}'/>
						    <input type='hidden' name='targetColumnInfo' value='${refMapRb.targetColumnInfo[i]}' />
							</td><td><a id='shwMap'></a></td><td><a href="javascript:;" class='map_drag'></a></td></tr></table></div>
	                	</c:forEach>
						</c:if>
	  				</div>
					</div>
					</div>
					</td>
				</tr>
				</table>
				</div>   
  			</div>
			<div layoutH="180">
				<div class="unit">
					<textarea id="resource_code" name="refBean.resource_code" cols="80" rows="10"></textarea>
				</div>
			</div>
		</div>
		<div class="tabsHeader">
			<div class="tabsHeaderContent">
				<ul>
					<li><a href="javascript:" onclick="setSubmitIndex(this, '0');"><span>字段映射</span></a></li>
					<li><a href="javascript:" onclick="setSubmitIndex(this, '1');"><span>映射脚本</span></a></li>
				</ul>
			</div>
		</div>
	</div>
    <div class="formBar">
		<ul>
		<li><div class="buttonActive"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
		<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
		</ul>
    </div>
  </form>
</div>
<div id="selectDict" style="display:none;">
	<select id="selType" name="selType">
       <option value="">无</option>
    <c:forEach var="ds" items="${dsList}">
       <option value="${ds.key}">${ds.value}</option>
	</c:forEach>
	</select>
</div>
<script type="text/javascript">
  $(function () {
    // 当前窗口
    var dlgId = "${resourceId}";
    if (!dlgId) {
      dlgId = "resourceDlg";
    }
    var mappingDialog = $("body").data(dlgId);

    if (!g_mappingEditors) {
      g_mappingEditors = [];
    }
    var rsCode = CodeMirror.fromTextArea(
            $('#resource_code', mappingDialog)[0], {
				mode: {name: "xml", alignCDATA: true,globalVars: true}, lineNumbers: true});
    g_mappingEditors[mappingDialog.data("id")] = rsCode;
	  rsCode.display.sizer.style.marginLeft = "29px";
	  rsCode.display.scrollbarH.style.left = "29px";
	  rsCode.display.lineGutter.style.width = "29px";
	  rsCode.display.lineNumInnerWidth = "28px";
    if ($.fn.sortDragByClass) {
      $("#dLeft, #dRight", mappingDialog).sortDragByClass();
    }

    // 添加
    $('#addBtn', mappingDialog).click(function () {
      add_line(mappingDialog);
    });

    // 滚动事件
    $("div", $("#mappingProcessorDiv", mappingDialog)).scroll(function () {
      var dlg = getMappingDlg(this);
      if ($(this).parent().attr("id") == "palMappingLeft") {
        $("div", $("#palMappingRight", dlg)).scrollTop($(this).scrollTop());
      } else if ($(this).parent().attr("id") == "palMappingRight") {
        $("div", $("#palMappingLeft", dlg)).scrollTop($(this).scrollTop());
      }
    });
  });
</script>