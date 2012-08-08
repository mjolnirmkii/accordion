<%-- The accordion container defines the area in accordion sections go. --%>
<%@page import="net.sourceforge.stripes.action.ActionBean"%>
<%@page import="java.util.Collection"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="accordion" prefix="accordion" %>
    
<stripes:layout-definition>
	<!-- setup parameters -->
	<c:choose> <%-- Choose header jsp: headerJsp attribute > customHeader (className) > generic --%>
		<c:when test="${not empty customHeader}">
			<c:set var="headerJsp" value="/jsp/accordion/${prefix}header.jsp" />
		</c:when>
		<c:otherwise>
			<c:set var="headerJsp" value="/jsp/accordion/accordion_generic_header.jsp" />
		</c:otherwise>
	</c:choose>
	<c:set var="contentJsp" value="/jsp/accordion/${prefix}display.jsp" />
	<c:if test="${empty addButtonText}">
		<c:set var="addButtonText" value="Add" />
	</c:if>
	
	<div class="accordionContainer accordionContainer_${id}">
		<div class="ajaxParams">
			<input type="hidden" name="accordion.customHeader" value="${customHeader}" />
			<input type="hidden" name="accordion.id" value="${id}" />
			<input type="hidden" name="accordion.prefix" value="${prefix}" />
			<input type="hidden" name="accordion.headerProperty" value="${headerProperty}" />
			<input type="hidden" name="accordion.collectionName" value="${collectionName}" />
		</div>
		<div class="accordion_expandCollapse">
			<a class="accordion_expandAllLink" href="#">Expand All</a> | 
			<a class="accordion_collapseAllLink" href="#">Collapse All</a>
		</div>
		<div class="accordions">
			<c:forEach items="${accordion:getProperty(actionBean, collectionName)}" var="item" varStatus="loop">
				<stripes:layout-render 
					name="/jsp/accordion/accordion_template.jsp"
					collectionName="${collectionName}"
					itemId="${loop.index}" 
					headerProperty="${headerProperty}" 
					headerJsp="${headerJsp}" 
					contentJsp="${contentJsp}" />
			</c:forEach>
		</div>
		<p>
			${addText} <input type="button" class="accordion_addButton" value="${addButtonText}">
		</p>
	</div>
</stripes:layout-definition>