<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        
        <title>Users | ${user.getUsername()}</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="users" scope="request"/>
        <c:set var="paths" value="${['Users','Details']}" scope="request" />
        <c:set var="urls" value="${['users','details']}" scope="request" />
        <%@ include file="/views/components/header.jsp" %>

        <c:set var="endPoint" value="users" scope="request"/>
        <c:set var="user" value="${user}" scope="request"/>
        <c:set var="accountsList" value="${bankAccounts}" scope="request"/>
        <c:set var="savingsList" value="${savingAccounts}" scope="request"/>
        <c:set var="checkingsList" value="${checkingAccounts}" scope="request"/>
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
                "/views/components/accounts/list_bank_account.jsp",
                "/views/components/accounts/list_saving_account.jsp",
                "/views/components/accounts/list_checking_account.jsp"
            });
        %>
        <%@ include file="/views/components/content.jsp" %>
        
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>