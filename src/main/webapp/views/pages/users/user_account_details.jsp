<!DOCTYPE html>
<html lang="en">
<%@ include file="/views/components/commons/imports.jsp" %>

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
            <title>User account | ${accountDetails.account().getAccountNumber()}</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <c:choose>
            <c:when test="${accountType=='BANK'}">
                <c:set var="detailPage" value="detail_bank_account.jsp" />
            </c:when>
            <c:when test="${accountType=='SAVING'}">
                <c:set var="detailPage" value="detail_saving_account.jsp" />
            </c:when>
            <c:when test="${accountType=='CHECKING'}">
                <c:set var="detailPage" value="detail_checking_account.jsp" />
            </c:when>
        </c:choose>

        <c:set var="menu" value="home" scope="request"/>
        <c:set var="paths" value="${['Dashboard','Account details']}" scope="request" />
        <c:set var="urls" value="${['dashboard','account-details']}" scope="request" />
        <%@ include file="/views/components/commons/header.jsp" %>

        <c:set var="endpoint" value="dashboard/account-details" scope="request"/>
        <c:set var="account" value="${accountDetails.account()}" scope="request"/>
        <c:set var="maxWithdraw" value="${accountDetails.maxWithdraw()}" scope="request"/>
        <c:set var="transactions" value="${accountDetails.transactions()}" scope="request"/>
        <c:set var="additionalParam" value="&account_number=${accountDetails.account().getAccountNumber()}&account_type=${accountType}" scope="request"/>
        
        <c:set var="panelItems" value="Account info" scope="request"/>
        <c:set var="panelContents" value="/views/components/accounts/${detailPage}" scope="request"/>
        <%@ include file="/views/components/commons/content.jsp" %>

        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>