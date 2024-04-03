<!DOCTYPE html>
    <html lang="en">
    <head>
        <%@ include file="/views/components/commons/imports.jsp" %> 
        <%@ include file="/views/components/commons/meta.jsp" %>
        <title>403 | Forbidden</title>
    </head>
    <body>
        <c:set var="code" value="403" scope="request"/>
        <c:set var="message" value="You do not have access to this page." scope="request"/>
        <%@ include file="/views/components/commons/message_box.jsp" %> 
    </body>
</html>