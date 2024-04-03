<!DOCTYPE html>
    <html lang="en">
    <head>
        <%@ include file="/views/components/commons/imports.jsp" %> 
        <%@ include file="/views/components/commons/meta.jsp" %> 
        <title>500 | Internal Server Error</title>
    </head>
    <body>
        <c:set var="code" value="500" scope="request"/>
        <c:set var="message" value="Internal server error." scope="request"/>
        <%@ include file="/views/components/commons/message_box.jsp" %> 
        <%@ include file="/views/components/commons/debug_box.jsp" %> 
    </body>
</html>