<%-- 
	Generic accordion header that will be used by default. 
	This header's text is intended to be manipulated client-side (JavaScript).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<stripes:layout-definition>
	<div>
		<h4>
			<stripes:layout-component name="headerText">
				<a class="headerText">${headerText}</a>
			</stripes:layout-component>
		</h4>
	</div>
</stripes:layout-definition>