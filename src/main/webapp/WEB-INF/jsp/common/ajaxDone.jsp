<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
{
	"statusCode":"${statusCode}", 
	"message":"${message}", 
	"navTabId":"${param.navTabId}", 
	"rel":"${param.rel}",
	"retFlag":"${retFlag}",
<c:if test="${jsonData != null}">
	"jsonData":${jsonData},
</c:if>
<c:if test="${isShowTab != null}">
	"isShowTab":${isShowTab},
</c:if>
<c:if test="${debugFlag != null}">
	"debugFlag":"${debugFlag}",
</c:if>
	"mapingData":"${mapingData}",
	"console_info":"${console_info}",
	"console_out":"${console_out}",
	"console_args":"${console_args}",
	"console_context":"${console_context}",
	"console_mata":"${console_mata}",
	"uuid":"${uuid}",
	"callbackType":"${param.callbackType}",
	"forwardUrl":"${forwardUrl}",
    "error":"${error}"
}