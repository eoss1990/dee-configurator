<%@ page pageEncoding="UTF-8"%>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
--%><%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	request.setAttribute("_path", basePath);
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tlds/sitemesh-page.tld" prefix="page"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri='/WEB-INF/tlds/core-server.tld' prefix='core'%>
<%@ taglib uri='/WEB-INF/tlds/core-pager.tld' prefix='pager'%>
<%@ taglib uri='/WEB-INF/tlds/core-elutil.tld' prefix='elutil'%>
<%@ taglib uri='/WEB-INF/tlds/core-info.tld' prefix='info'%>