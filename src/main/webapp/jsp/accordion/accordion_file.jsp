<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="accordion" prefix="accordion" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html id="accordion_file">
	<head>
		<title>File Upload</title>
		<%-- Pull in jQuery, etc. May need to be adjusted for each project (override this jsp) --%> 
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" />
		<!--[if lt IE 8]> <style type="text/css">@import "${pageContext.request.contextPath}/css/IEstyle.css";</style> <![endif]-->
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery.ui.all.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fileinput.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/accordion.css" />
		
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.5.1.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.8.11.custom.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.ui.button.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.fileinput.min.js"></script>
		
		<script language="Javascript">
			$(function() {
				$('input[type="file"]').fileinput();
				$('#fileName').val('dummy');
			});
		</script>
		<c:set var="fileProperty" value="${actionBean.accordion.collectionName}[${actionBean.accordion.itemId}].${actionBean.accordion.modelProperty}" />
		<c:set var="file" value="${accordion:getProperty(actionBean, fileProperty)}" />
		<c:choose>
			<c:when test="${not empty file}">
				<script>
					$(function() {
						parent.$('#' + $('#iframeId').val()).trigger('accordion_fileUploadComplete'); // tell the parent this file has uploaded.
					});
				</script>
			</c:when>
			<c:otherwise>
				<script>
					$(function() {
						parent.$('#' + $('#iframeId').val()).trigger('accordion_fileUploadFailed'); // tell the parent this file has not uploaded.
					});
				</script>
			</c:otherwise>
		</c:choose>
	</head>
	<body>
		<stripes:form beanclass="${actionBean.class.name}">
			<stripes:hidden id="iframeId" name="accordion.id" />
			<stripes:hidden name="accordion.collectionName" />
			<stripes:hidden name="accordion.itemId" />
			<stripes:hidden name="accordion.modelProperty" />
			<stripes:hidden name="accordion.actionBeanProperty" />
			<input id="fileName" type="hidden" name="${fileProperty}" />
			<stripes:file name="${actionBean.accordion.actionBeanProperty}" class="accordion_file" />
			<stripes:hidden name="accordion_fileSubmit" />
		</stripes:form>
	</body>
</html>