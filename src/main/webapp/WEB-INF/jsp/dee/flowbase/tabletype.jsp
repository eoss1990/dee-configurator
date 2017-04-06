<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<select name="${param.inputName}" onchange="setRefFieldNone(this);">
	<option value="master">主表</option>
	<option value="slave">从表</option>
</select>