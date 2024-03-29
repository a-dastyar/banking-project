<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/commons/imports.jsp" %>

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
        <title>Checking account | ${accountDetails.account().getAccountNumber()}</title>
    </head>

    <body>
        <div class="background blurred"></div>
        
        <c:set var="menu" value="checkings" scope="request"/>
        <c:set var="paths" value="${['Checking accounts','Details']}" scope="request" />
        <c:set var="urls" value="${['checking-accounts','details']}" scope="request" />
        <%@ include file="/views/components/commons/header.jsp" %>

        <c:set var="endpoint" value="checking-accounts/details" scope="request"/>
        <c:set var="balanceEndpoint" value="checking-accounts/balance" scope="request"/>
        <c:set var="additionalParam" value="&account_number=${accountDetails.account().getAccountNumber()}" scope="request"/>
        <c:set var="account" value="${accountDetails.account()}" scope="request"/>
        <c:set var="maxWithdraw" value="${accountDetails.maxWithdraw()}" scope="request"/>
        <c:set var="minWithdraw" value="${accountDetails.minWithdraw()}" scope="request"/>
        <c:set var="minDeposit" value="${accountDetails.minDeposit()}" scope="request"/>
        <c:set var="transactions" value="${accountDetails.transactions()}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "Account info",
                "Withdraw/Deposit"
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/accounts/detail_checking_account.jsp",
                "/views/components/accounts/do_withdraw_deposit.jsp"
            });
        %>
        <%@ include file="/views/components/commons/content.jsp" %>

        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>
