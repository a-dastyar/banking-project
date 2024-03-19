<!DOCTYPE html>
<html lang="en">
<%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
            <title>Bank account | ${account.getAccountNumber()}</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="banks" scope="request"/>
        <c:set var="paths" value="${['Bank accounts','Details']}" scope="request" />
        <c:set var="urls" value="${['bank-accounts','details']}" scope="request" />
        <%@ include file="/views/components/header.jsp" %>

        <c:set var="endPoint" value="bank-accounts" scope="request"/>
        <c:set var="account" value="${account}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "Account info",
                "Withdraw/Deposit"
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/accounts/detail_bank_account.jsp",
                "/views/components/accounts/do_withdraw_deposit.jsp"
            });
        %>
        <%@ include file="/views/components/content.jsp" %>

        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>