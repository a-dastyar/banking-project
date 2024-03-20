<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/commons/imports.jsp" %>
    <c:set var="sumExists" value="${sum != null}"/>

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
        <title>Saving accounts</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="savings" scope="request"/>
        <c:set var="paths" value="${['Saving accounts']}" scope="request" />
        <c:set var="urls" value="${['saving-accounts']}" scope="request" />
        <%@ include file="/views/components/commons/header.jsp" %>
        
        <c:if test="${min!=null}">
            <c:set var="activePanel" value="2" scope="request"/>
        </c:if>

        <c:set var="endPoint" value="saving-accounts" scope="request"/>
        <c:set var="savingsList" value="${accounts.list()}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "List accounts",
                "Add account",
                "Sum balance",
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/accounts/list_saving_account.jsp",
                "/views/components/accounts/add_saving_account.jsp",
                "/views/components/accounts/sum_balance.jsp"
            });
        %>
        <%@ include file="/views/components/commons/content.jsp" %>
         
        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>
