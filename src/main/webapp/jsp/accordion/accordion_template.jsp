<%-- The template that the accordionContainer and ajax accordion use. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="accordion" uri="accordion" %>

<stripes:layout-definition>
	<%-- Try actionBean properties --%>
	<c:if test="${empty headerProperty}">
		<c:set var="headerProperty" value="${actionBean.accordion.headerProperty}"/>
	</c:if>
	<c:if test="${empty itemId}">
		<c:set var="itemId" value="${actionBean.accordion.itemId}" />
	</c:if>
	<c:if test="${empty collectionName}">
		<c:set var="collectionName" value="${actionBean.accordion.collectionName}" />
	</c:if>
	<c:if test="${empty headerJsp}">
		<c:set var="headerJsp" value="${actionBean.accordion.headerJsp}" />
	</c:if>
	<c:if test="${empty contentJsp}">
		<c:set var="contentJsp" value="${actionBean.accordion.contentJsp}" />
	</c:if>
	
	<c:if test="${empty itemId}">
		<%-- Assume new item, set index to size of collection --%>
		<c:set var="itemId" value="${fn:length(accordion:getProperty(actionBean,collectionName))}" />
	</c:if>
	<c:if test="${not empty headerProperty}">
		<c:set var="headerText" value="${item[headerProperty]}" />
	</c:if>
	
	<div class="accordion">
		<div>
			<div class="accordion_header">
				<input type="hidden" class="itemId" value="${itemId}" />
				<input type="hidden" class="isNew" value="${empty accordion:getProperty(actionBean, collectionName)[itemId]}" />
				<h4 class="accordion_right">
						<a href="#" class="accordion_editLink">Edit</a>
						<a href="#" class="accordion_deleteLink"><img src="${pageContext.request.contextPath}/images/cross.png" title="Delete" /></a>
				</h4>
				<stripes:layout-render name="${headerJsp}" item="${accordion:getProperty(actionBean,collectionName)[itemId]}" headerText="${headerText}" itemId="${itemId}"/>
			</div>
		</div>
		<div>
			<div class="accordion_content">
				<stripes:layout-render name="${contentJsp}"
					collectionName="${collectionName}"
					itemId="${itemId}"
					item="${accordion:getProperty(actionBean, collectionName)[itemId]}" />
			</div>
		</div>
	</div>
</stripes:layout-definition>