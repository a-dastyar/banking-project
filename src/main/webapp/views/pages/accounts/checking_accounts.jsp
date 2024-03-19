<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>
    <c:set var="sumExists" value="${sum != null}"/>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <title>Checking accounts</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="checkings" scope="request"/>
        <c:set var="paths" value="${['Checking accounts']}" scope="request" />
        <c:set var="urls" value="${['checking-accounts']}" scope="request" />
        <%@ include file="/views/components/header.jsp" %>
        
        <c:if test="${min!=null}">
            <c:set var="activePanel" value="2" scope="request"/>
        </c:if>

        <c:set var="endPoint" value="checking-accounts" scope="request"/>
        <c:set var="checkingsList" value="${accounts.list()}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "List accounts",
                "Add account",
                "Sum balance",
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/accounts/list_checking_account.jsp",
                "/views/components/accounts/add_checking_account.jsp",
                "/views/components/accounts/sum_balance.jsp"
            });
        %>
        <%@ include file="/views/components/content.jsp" %>
         
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>
