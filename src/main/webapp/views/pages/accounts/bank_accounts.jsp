<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/commons/imports.jsp" %>
    <c:set var="sumExists" value="${sum != null}" />

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
        <title>Bank accounts</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="banks" scope="request" />
        <c:set var="paths" value="${['Bank accounts']}" scope="request" />
        <c:set var="urls" value="${['bank-accounts']}" scope="request" />
        <%@ include file="/views/components/commons/header.jsp" %>
        
        <c:if test="${min!=null}">
            <c:set var="activePanel" value="2" scope="request"/>
        </c:if>

        <c:set var="endPoint" value="bank-accounts" scope="request"/>
        <c:set var="accountsList" value="${accounts.list()}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "List accounts",
                "Add account",
                "Sum balance",
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/accounts/list_bank_accounts.jsp",
                "/views/components/accounts/add_bank_account.jsp",
                "/views/components/accounts/sum_balance.jsp"
            });
        %>
        <%@ include file="/views/components/commons/content.jsp" %>
         
        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>