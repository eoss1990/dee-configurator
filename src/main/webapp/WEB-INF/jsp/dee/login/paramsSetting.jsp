<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include.inc.jsp"%>
<style type="text/css">
    .uInput {width:160px;}
    .uSelect {width:167px;}
</style>
<script type="text/javascript">
function delRow(obj) {
	var tr = $(obj).closest("tr");
	var key = $(tr.find("td input")[0]).val();
	if ("rmisrv" == key || "JDBCReader.pageSize" == key) {
		alertMsg.info("不能删除关键字段！");
	}
	tr.remove();
}
</script>
<div class="tabs" style="margin-top:5px;margin-left:5px;margin-right:5px;">
   <div class="tabsHeader">
       <div class="tabsHeaderContent">
           <ul>
               <li class="selected"><a href="javascript:void(0);"><span>系统参数</span></a></li>
               <li><a href="javascript:void(0);"><span>自定义全局参数</span></a></li>
           </ul>
       </div>
   </div>
   <div class="tabsContent" layoutH="50">
       <div>
           <form method="post" action="/dee/login!saveParamsSetting.do?paramType=0"
              class="pageForm required-validate" onsubmit="return validateCallback(this)">
              <div class="pageFormContent nowrap" layoutH="105">
                      <div>
                          <br/><br/>
                          <table class="list nowrap" width="98%">
                              <thead>
                                  <tr>
                                      <th type="text" width="200" name="items[#index#].key" fieldClass="required uInput" fieldAttrs="{maxlength:100}">参数名称</th>
                                      <th type="text" width="300" name="items[#index#].value" fieldClass="uInput" fieldAttrs="{maxlength:100}">参数值</th>
                                  </tr>
                              </thead>
                              <tbody>
                                  <c:forEach items="${systemParams}" var="item" varStatus="var">
                                  <tr class="unitBox">
                                      <td>
                                          <input type="text" class="required textInput uInput" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" readonly="readonly" />
                                      </td>
                                      <td>
                                      	<c:choose>
                                      	  <c:when test="${item.value=='true' or item.value=='false'}">
                                          <select name="items[${var.index}].value" submitName="items[${var.index}].value" class="uSelect">
                                              <option value="true" <c:if test="${item.value=='true'}">selected</c:if> >开启</option>
                                              <option value="false" <c:if test="${item.value=='false'}">selected</c:if> >关闭</option>
                                          </select>
                                      	  </c:when>
                                      	  <c:otherwise>
                                      	  	<c:choose>
                                      	  	  <c:when test="${item.key=='exeDate'}">
                                      	  	  	  <input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" class="required uInput" readonly="readonly" />
                                      	      </c:when>
                                      	      <c:otherwise>
                                          		  <input type="text" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" maxlength="10" class="required uInput positiveinteger" />
                                      	      </c:otherwise>
                                      		</c:choose>
                                      	  </c:otherwise>
                                      	</c:choose>
                                      </td>
                                  </tr>
                                  </c:forEach>
                              </tbody>
                          </table>
                      </div>
              </div>
      
              <div class="formBar">
                  <ul>
                      <li><div class="buttonActive">
                              <div class="buttonContent">
                                  <button type="submit">保存</button>
                              </div>
                          </div></li>
                      <li><div class="button">
                              <div class="buttonContent">
                                  <button type="button" class="close">取消</button>
                              </div>
                          </div></li>
                  </ul>
              </div>
          </form>
       </div>
       <div>
           <form method="post" action="/dee/login!saveParamsSetting.do?paramType=1"
               class="pageForm required-validate" onsubmit="return validateCallback(this)">
               <div class="pageFormContent nowrap" layoutH="105">
                       <div>
                           <table class="list nowrap itemDetail" addButton="新建" width="98%">
                               <thead>
                                   <tr>
                                       <th type="text" width="200" name="items[#index#].key" fieldClass="required uInput" fieldAttrs="{maxlength:100}">参数名称</th>
                                       <th type="text" width="300" name="items[#index#].value" fieldClass="uInput" fieldAttrs="{maxlength:100}">参数值</th>
                                       <th type="del" width="60">操作</th>
                                   </tr>
                               </thead>
                               <tbody>
                                   <c:forEach items="${customParams}" var="item" varStatus="var">
                                   <tr class="unitBox">
                                       <td>
                                          <input type="text" class="required textInput uInput" name="items[${var.index}].key" submitName="items[${var.index}].key" value="${item.key}" <c:if test="${item.key=='dee.default.pagesize'}"> readonly="readonly" </c:if> />
                                       </td>
                                       <td>
                                           <input type="text" class="uInput <c:if test="${item.key=='dee.default.pagesize'}">required positiveinteger</c:if>" name="items[${var.index}].value" submitName="items[${var.index}].value" value="${item.value}" />
                                       </td>
                                       <td>
                                           <c:if test="${item.key!='dee.default.pagesize'}">
                                           <a href="javascript:void(0);" onclick="javascript:delRow(this);" class="btnDel"></a>
                                           </c:if>
                                       </td>
                                   </tr>
                                   </c:forEach>
                               </tbody>
                           </table>
                       </div>
               </div>
      
               <div class="formBar">
                   <ul>
                       <li><div class="buttonActive">
                               <div class="buttonContent">
                                   <button type="submit">保存</button>
                               </div>
                           </div></li>
                       <li><div class="button">
                               <div class="buttonContent">
                                   <button type="button" class="close">取消</button>
                            </div>
                          </div></li>
                  </ul>
              </div>
          </form>
       </div>
   </div>
   <div class="tabsFooter">
       <div class="tabsFooterContent"></div>
   </div>
</div>


