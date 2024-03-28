<!DOCTYPE html>
    <html lang="en">
    <head>
        <%@ include file="/views/components/commons/imports.jsp" %> 
        <%@ include file="/views/components/commons/meta.jsp" %> 
        <title>404 | Not Found</title>
    </head>
    <body>
        <c:set var="code" value="404" scope="request"/>
        <c:set var="message" value="The page you requested was not found." scope="request"/>
        <%@ include file="/views/components/commons/message_box.jsp" %> 
        <%@ include file="/views/components/commons/debug_box.jsp" %> 
    </body>
</html>