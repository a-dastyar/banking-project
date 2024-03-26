<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/commons/imports.jsp" %>

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
        <title>Saving account | ${account.getAccountNumber()}</title>
    </head>

    <body>
        <div class="background blurred"></div>

        <c:set var="menu" value="savings" scope="request"/>
        <c:set var="paths" value="${['Saving accounts','Details']}" scope="request" />
        <c:set var="urls" value="${['saving-accounts','details']}" scope="request" />
        <%@ include file="/views/components/commons/header.jsp" %>

        <c:set var="endpoint" value="saving-accounts" scope="request"/>
        <c:set var="account" value="${accountDetails.account()}" scope="request"/>
        <c:set var="maxWithdraw" value="${accountDetails.maxWithdraw()}" scope="request"/>
        <c:set var="minDeposit" value="${accountDetails.minDeposit()}" scope="request"/>
        <c:set var="transactions" value="${accountDetails.transactions()}" scope="request"/>
        <%
            pageContext.setAttribute("panelItems",new String[]{
                "Account info",
                "Withdraw/Deposit"
            });
            pageContext.setAttribute("panelContents",new String[]{
                "/views/components/accounts/detail_saving_account.jsp",
                "/views/components/accounts/do_withdraw_deposit.jsp"
            });
        %>
        <%@ include file="/views/components/commons/content.jsp" %>

        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>
