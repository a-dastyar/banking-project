<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/commons/imports.jsp" %>

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
        
        <title>Users | ${user.getUsername()}</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <c:choose>
            <c:when test="${userDetails.activeTab()=='BANK'}">
                <c:set var="active" value="2" />
            </c:when>
            <c:when test="${userDetails.activeTab()=='SAVING'}">
                <c:set var="active" value="3" />
            </c:when>
            <c:when test="${userDetails.activeTab()=='CHECKING'}">
                <c:set var="active" value="4" />
            </c:when>
            <c:otherwise>
                <c:set var="active" value="0" />
            </c:otherwise>
        </c:choose>
        <c:set var="activePanel" value="${active}" scope="request"/>

        <c:set var="menu" value="users" scope="request"/>
        <c:set var="paths" value="${['Users','Details']}" scope="request" />
        <c:set var="urls" value="${['users','details']}" scope="request" />
        <%@ include file="/views/components/commons/header.jsp" %>

        <c:set var="endpoint" value="users/details" scope="request"/>
        <c:set var="user" value="${userDetails.user()}" scope="request"/>
        <c:set var="accounts" value="${userDetails.bankAccounts()}" scope="request"/>
        <c:set var="savings" value="${userDetails.savingAccounts()}" scope="request"/>
        <c:set var="checkings" value="${userDetails.checkingAccounts()}" scope="request"/>
        <c:set var="additionalParam" value="&username=${userDetails.user().getUsername()}" scope="request"/>
        <c:set var="isTypeParamRequired" value="true" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "User info",
                "Update",
                "Bank accounts",
                "Saving accounts",
                "Checking accounts"
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/users/detail_user.jsp",
                "/views/components/users/update_user.jsp",
                "/views/components/accounts/list_bank_accounts.jsp",
                "/views/components/accounts/list_saving_accounts.jsp",
                "/views/components/accounts/list_checking_accounts.jsp"
            });
        %>
        <%@ include file="/views/components/commons/content.jsp" %>
        
        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>