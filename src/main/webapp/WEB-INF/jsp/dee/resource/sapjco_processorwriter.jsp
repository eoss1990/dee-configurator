<%@ page language="java" contentType="text/html;charset=utf-8"%> 
<%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/flowbase/addProcess.js" type="text/javascript"></script>
<script src="/styles/dee/js/resource/sapjco_processor.js" type="text/javascript"></script>

<div class="pageContent">
  <form method="post" action="/dee/sapjcowriter!save.do?callbackType=closeCurrent&navTabId=exchangeBaseNav&sjpBean.pr_type=writer"
        id="sapjcoProcessorForm" class="pageForm required-validate" onSubmit="return validateCallback(this, processDone)">
    <div class="pageFormContent nowrap" style="height: 450px; overflow: auto;">
      <jsp:include page="resourceType.jsp"></jsp:include>
      <p></p>
      <p>
        <label>目标名称：</label>
        <input type="text" name="bean.dis_name" value="${bean.dis_name}" maxlength="50" class="required uInput" />
      </p>
      <p>
        <label>SAP服务器IP：</label>
        <input type="text" name="sjpBean.jco_ashost" value="${sjpBean.jco_ashost}" maxlength="200" class="required uInput" />
      </p>
      <p>
        <label>系统编号：</label>
        <input type="text" name="sjpBean.jco_sysnr" value="${sjpBean.jco_sysnr}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>SAP集团：</label>
        <input type="text" name="sjpBean.jco_client" value="${sjpBean.jco_client}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>用户名称：</label>
        <input type="text" name="sjpBean.jco_user" value="${sjpBean.jco_user}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>密码：</label>
        <input type="password" name="sjpBean.jco_passwd" value="${sjpBean.jco_passwd}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>方法名称：</label>
        <input type="text" name="sjpBean.func" value="${sjpBean.func}" maxlength="200" class="required uInput"/>
      </p>
      <p>
        <label>JCoStructure映射：</label>
        <input type="button" id="jcoStructureBtn" name="jcoStructureBtn" value="映射配置..." onclick="structureClick(event,this);"/>
      </p>
      <p>
        <label>JCoTable映射：</label>
        <input type="button" id="jcoTableBtn" name="jcoTableBtn" value="映射配置..." onclick="tableClick(event,this);"/>
      </p>
      <table class="list nowrap itemDetail" addButton="新建输入参数" width="100%">
        <thead>
          <tr>
            <th type="text" width="48%" name="items[#index#].key" size="40" fieldClass="required" fieldAttrs="{maxlength:100}">参数名称</th>
            <th type="text" width="48%" name="items[#index#].value" size="40" fieldAttrs="{maxlength:100}">参数值</th>
            <th type="del" width="4%">操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${sjpBean.map}" var="item" varStatus="var">
            <tr class="unitBox">
              <td><input size="40" type="text" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" class="required textInput" /></td>
              <td><input size="40" type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" /></td>
              <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a> </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <br/>
<!--新增支持jcostructure与jcotable-->      
      <table class="list nowrap itemDetail" addButton="新建返回参数" width="100%">
        <thead>
          <tr>
          	<th type="text" width="48%" name="jcoReturn[#index#].key" size="40" fieldAttrs="{maxlength:100}" fieldClass="required">名称</th>
            <th type="enum" width="48%" name="jcoReturn[#index#].value" enumUrl="/dee/resource!jcoreturntype.do" >返回参数类型</th> 
            <th type="del" width="4%">操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${sjpBean.jcoReturnMap}" var="item" varStatus="var">
            <tr class="unitBox">
              <td><input size="40" type="text" name="jcoReturn[${var.index}].key" submitName="jcoReturn[${var.index}].key" value="${item.key}" class="required textInput"/></td>
              <td>
              	<select name="jcoReturn[${var.index}].value" value="${item.value}">
					<option value="String" ${'String' eq item.value?'selected':''}>String</option>
					<option value="JCoStructure" ${'JCoStructure' eq item.value?'selected':''}>JCoStructure</option>
					<option value="JCoTable" ${'JCoTable' eq item.value?'selected':''}>JCoTable</option>
				</select>
			  </td>
              <td><a href="javascript:void(0);" onClick="javascript:delRow(this);" class="btnDel"></a> </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>     
    </div>
    <div class="formBar">
      <ul>
        <li>
          <div class="buttonActive">
            <div class="buttonContent">
              <input type="hidden" name="flowId" value="${flowId}" />
              <input type="hidden" name="resourceId" value="${resourceId}" />
              <input type="hidden" name="sort" value="${sort}" />
              <button type="button" onclick="javascript:submitSapjcoProcessorForm(this);">保存</button>
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