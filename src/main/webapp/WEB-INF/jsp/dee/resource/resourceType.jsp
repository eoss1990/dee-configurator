<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<style type="text/css">
    .uInput {width:160px;}
    .uSelect {width:167px;}
</style>
<script type="text/javascript">
    $(document).ready(function() {
        var dlg = $(">.dialog:last-child", "body");
    	$("#resourceType", dlg).change(function() {
            var url = "";
            var resourceType = $(this).val();
            switch (resourceType) {
            case "0":          // JDBC Reader
				url = "/dee/jdbcreader!jdbcreadershow.do";
                break;
            case "21":         // WebService Reader
                url = "/dee/wsprocessor!view.do";
                break;
            case "25":         // SapJco Reader
                url = "/dee/sapjco!view.do";
                break;
            case "30":         // SAPWSReader
                url="/dee/sapws!view.do";
                break;
            case "40":         // REST 适配器
                url = "/dee/staticrestprocesser!view.do";
                break;
//            case "34":         // 枚举值导入
//                url = "/dee/a8enumreader!view.do";
//                break;
//            case "36":         // Rest Reader
//                url = "/dee/restprocessor!view.do";
//                break;
            case "1001":         // A8接口 Reader
                url = "/dee/a8enumreader!view.do";//新建默认指向枚举 "/dee/a8interfacereader!view.do";
                break;
            case "14":         // 输入脚本
                url = "/dee/script!view.do";
                break;
            case "17":         // 输入自定义配置
            	url = "/dee/custom!view.do";
                break;
            case "3":          // 字段映射
                url = "/dee/mapping!mappingShow.do";
                break;
            case "4":          // XSLT格式转换
                url = "/dee/xslt!view.do";
                break;
            case "15":         // 转换脚本
                url = "/dee/script!view.do";
                break;
            case "18":         // 转换自定义配置
            	url = "/dee/custom!view.do";
                break;
            case "2":          // JDBC
                url = "/dee/jdbcwriter!jdbcWritershow.do";
                break;
//            case "8":          // A8WS接口(发起表单流程)
//                url = "/dee/a8source!view.do";
//                break;
            case "1002":         // A8接口 writer 默认转向 A8WS接口(发起表单流程)
                url = "/dee/a8source!view.do";
                break;
//            case "20":         // A8WS接口(无流程表单)
//                url = "/dee/a8commonws!view.do";
//                break;
//            case "28":         // A8WS接口(组织机构同步)
//                url = "/dee/orgSyncWriter!view.do";
//                break;
            case "23":         // WebService适配器
                url = "/dee/wsprocessor!view.do";
                break;
            case "27":
                url="/dee/sapjcowriter!view.do";
                break;
            case "32":         // SAP WebService
                url="/dee/sapws!view.do";
                break;
//            case "35":         // 枚举值导出
//                url = "/dee/a8enumwriter!view.do";
//                break;
//            case "37":         // Rest Writer
//                url = "/dee/restprocessor!view.do";
//                break;
            case "16":         // 目标脚本
                url = "/dee/script!view.do";
                break;
            case "19":         // 目标自定义配置
            	url = "/dee/custom!view.do";
                break;
//            case "38":         // A8消息发送
//            	url = "/dee/a8msgwriter!view.do";
//                break;
            case "39":         // A8表单字段回填
                url = "/dee/a8form_writeback_writer!view.do";
                break;
            case "42":         // REST 适配器
                url = "/dee/staticrestprocesser!view.do";
                break;
            default:
                return;
            }
        
            var param = "?flowId=${flowId}&sort=${sort}&resourceType=" + resourceType;
            url += param;
            var dlgId = "resourceDlg";
            var title = "适配器配置";
            var options = {
                width:"800px",
                height:"600px"
            };
            $.pdialog.open(url, dlgId, title, options);
        });
    	
    	var t_tmp_id = "${bean.deeResourceTemplate.resource_template_id}";
    	var resourceId = "${resourceId}";
    	var resourceType = "${resourceType}";
    	if (resourceId) {
    		var tmpId = t_tmp_id;
    		if(t_tmp_id == "34" || t_tmp_id == "36"){
    			tmpId = "1001";
    		}
    		if(t_tmp_id=="8" || t_tmp_id=="20" || t_tmp_id=="28" || t_tmp_id=="35" || t_tmp_id=="37" || t_tmp_id=="38"){
    			tmpId = "1002";
    		}
    		$("#resourceType", dlg).val(tmpId);
    		$("#resourceType", dlg).attr("disabled", "disabled");
    	} else {
    		if (resourceType) {
    			$("#resourceType", dlg).val(resourceType);
    		}
    	}
    });
</script>
<div class="unit">
    <c:set var="t_tmp_id" value="${bean.deeResourceTemplate.resource_template_id}" />
    <label>类型：</label>
    <select name="resourceType" id="resourceType" class="uSelect">
        <c:if test="${(sort >= 1000 && sort < 2000) || t_tmp_id==0 || t_tmp_id==14 || t_tmp_id==21 || t_tmp_id==25 || t_tmp_id==30 || t_tmp_id==17 || t_tmp_id==34 || t_tmp_id==36 || t_tmp_id==40}">
            <option value="0" <c:if test="${t_tmp_id==0 || t_tmp_id==null}">selected</c:if>>JDBC</option>
            <option value="21" <c:if test="${t_tmp_id==21}">selected</c:if>>WebService适配器</option>
            <option value="40" <c:if test="${t_tmp_id==40}">selected</c:if>>REST适配器</option>
            <option value="25" <c:if test="${t_tmp_id==25}">selected</c:if>>SAP JCO</option>
            <option value="30" <c:if test="${t_tmp_id==30}">selected</c:if>>SAP WebService</option>
            <!-- 
             <option value="34" <c:if test="${t_tmp_id==34}">selected</c:if>>A8WS接口(枚举值导出)</option>
            <option value="36" <c:if test="${t_tmp_id==36}">selected</c:if>>REST接口</option>
             -->
            <option value="1001" <c:if test="${t_tmp_id==34 || t_tmp_id==36}">selected</c:if>>A8接口</option>
            <option value="14" <c:if test="${t_tmp_id==14}">selected</c:if>>脚本</option>
            <option value="17" <c:if test="${t_tmp_id==17}">selected</c:if>>自定义配置</option>
        </c:if>
    
        <c:if test="${(sort >= 2000 && sort < 3000) || t_tmp_id==3 || t_tmp_id==4 || t_tmp_id==15 || t_tmp_id==18}">
            <option value="3" <c:if test="${t_tmp_id==3 || t_tmp_id==null}">selected</c:if>>字段映射</option>
            <option value="4" <c:if test="${t_tmp_id==4}">selected</c:if>>XSLT格式转换</option>
            <option value="15" <c:if test="${t_tmp_id==15}">selected</c:if>>脚本</option>
            <option value="18" <c:if test="${t_tmp_id==18}">selected</c:if>>自定义配置</option>
        </c:if>
    	<c:if test="${((sort >= 3000 && sort < 4000) || t_tmp_id==2 || t_tmp_id==8 || t_tmp_id==20 || t_tmp_id==28 || t_tmp_id==23 || t_tmp_id==27 || t_tmp_id==32 || t_tmp_id==16 || t_tmp_id==19 || t_tmp_id==35 || t_tmp_id==37 || t_tmp_id==38 || t_tmp_id==39 || t_tmp_id==42)}">
            <option value="2" <c:if test="${t_tmp_id==2 || t_tmp_id==null}">selected</c:if>>JDBC</option>
            <!--
            <option value="8" <c:if test="${t_tmp_id==8}">selected</c:if>>A8WS接口(发起表单流程)</option>
            <option value="20" <c:if test="${t_tmp_id==20}">selected</c:if>>A8WS接口(无流程表单)</option>
            <option value="28" <c:if test="${t_tmp_id==28}">selected</c:if>>A8WS接口(组织机构同步)</option>
            <option value="35" <c:if test="${t_tmp_id==35}">selected</c:if>>A8WS接口(枚举值导入)</option>
            -->
            <option value="23" <c:if test="${t_tmp_id==23}">selected</c:if>>WebService适配器</option>
            <option value="42" <c:if test="${t_tmp_id==42}">selected</c:if>>REST适配器</option>
            <option value="27" <c:if test="${t_tmp_id==27}">selected</c:if>>SAP JCO</option>
            <option value="32" <c:if test="${t_tmp_id==32}">selected</c:if>>SAP WebService</option>
            <!--
            <option value="37" <c:if test="${t_tmp_id==37}">selected</c:if>>REST接口</option>
            <option value="38" <c:if test="${t_tmp_id==38}">selected</c:if>>A8消息发送</option>
            -->
            <option value="1002" <c:if test="${t_tmp_id==8 || t_tmp_id==20 || t_tmp_id==28 || t_tmp_id==35 || t_tmp_id==37 || t_tmp_id==38}">selected</c:if>>A8接口</option>
            <option value="39" <c:if test="${t_tmp_id==39}">selected</c:if>>回写表单数据</option>
            <option value="16" <c:if test="${t_tmp_id==16}">selected</c:if>>脚本</option>
            <option value="19" <c:if test="${t_tmp_id==19}">selected</c:if>>自定义配置</option>
        </c:if>
<%--         <c:if test="${writerExist != 'true' && ((sort >= 3000 && sort < 4000) || t_tmp_id==2 || t_tmp_id==8 || t_tmp_id==20 || t_tmp_id==28 || t_tmp_id==23 || t_tmp_id==27 || t_tmp_id==32 || t_tmp_id==16 || t_tmp_id==19 || t_tmp_id==35 || t_tmp_id==37 || t_tmp_id==38)}">
            <option value="2" <c:if test="${t_tmp_id==2 || t_tmp_id==null}">selected</c:if>>JDBC</option>
            <option value="8" <c:if test="${t_tmp_id==8}">selected</c:if>>A8WS接口(发起表单流程)</option>
            <option value="20" <c:if test="${t_tmp_id==20}">selected</c:if>>A8WS接口(无流程表单)</option>
            <option value="28" <c:if test="${t_tmp_id==28}">selected</c:if>>A8WS接口(组织机构同步)</option>
            <option value="35" <c:if test="${t_tmp_id==35}">selected</c:if>>A8WS接口(枚举值导入)</option>
            <option value="23" <c:if test="${t_tmp_id==23}">selected</c:if>>WebService适配器</option>
            <option value="27" <c:if test="${t_tmp_id==27}">selected</c:if>>SAP JCO</option>
            <option value="32" <c:if test="${t_tmp_id==32}">selected</c:if>>SAP WebService</option>
            <option value="37" <c:if test="${t_tmp_id==32}">selected</c:if>>REST接口</option>
            <option value="38" <c:if test="${t_tmp_id==38}">selected</c:if>>A8消息发送</option>
            <option value="16" <c:if test="${t_tmp_id==16}">selected</c:if>>脚本</option>
            <option value="19" <c:if test="${t_tmp_id==19}">selected</c:if>>自定义配置</option>
        </c:if>
        <c:if test="${writerExist == 'true' && ((sort >= 3000 && sort < 4000) || t_tmp_id==2 || t_tmp_id==8 || t_tmp_id==20 || t_tmp_id==28 || t_tmp_id==23 || t_tmp_id==27 || t_tmp_id==32 || t_tmp_id==16 || t_tmp_id==19)}">
            <option value="16" <c:if test="${t_tmp_id==16}">selected</c:if>>脚本</option>
        </c:if> --%>
    </select>
</div>