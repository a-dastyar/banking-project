<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        
        <title>Users</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="users" scope="request"/>
        <c:set var="paths" value="${['Users']}" scope="request" />
        <c:set var="urls" value="${['users']}" scope="request" />
        <%@ include file="/views/components/header.jsp" %>


        <c:set var="endPoint" value="bank-accounts" scope="request"/>
        <c:set var="usersList" value="${users.list()}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "List users",
                "Add user"
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/users/list_user.jsp",
                "/views/components/users/add_user.jsp"
            });
        %>
        <%@ include file="/views/components/content.jsp" %>
         

        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>