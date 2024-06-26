<%@ include file="/views/components/commons/imports.jsp" %>
<c:if test="${accounts.list().size() > 0}">
    <table class="table border-dark">
        <thead>
            <tr>
                <th scope="col">Account number</th>
                <th scope="col">Owner</th>
                <th scope="col">Balance</th>
                <th scope="col">Created</th>
                <th scope="col">Details</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="account" items="${accounts.list()}">
                <fmt:parseDate  value="${account.getCreatedAt()}"  type="both" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" parseLocale="fa_IR" />
                <fmt:formatDate value="${parsedDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" var="date" />
                <c:set var="detailEndpoint" value="${accountDetailEndpoint==null?'bank-accounts/details':accountDetailEndpoint}"/>
                <c:set var="params" value="?account_number=${account.accountNumber}${accountDetailEndpoint==null?'':'&account_type=BANK'}"/>
                <tr>
                    <td>${account.getAccountNumber()}</td>
                    <td>${account.getAccountHolder().getUsername()}</td>
                    <td>${account.getBalance()}</td>
                    <td>${date}</td>
                    <td><a class="link-secondary" href="${pageContext.request.contextPath}/${detailEndpoint}${params}&size=11">
                        Details
                    </a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${isTypeParamRequired}">
    <c:set var="typeParam" value="&account_type=BANK" scope="request" />
</c:if>
<c:set var="page" value="${accounts}"/>
<c:set var="pageParamName" value="page"/>
<%@ include file="/views/components/commons/pagination.jsp" %>
<c:if test="${accounts.list() == null || accounts.list().size() == 0}">
    <div class="container-sm row text-center justify-content-center" role="alert">
        <div class="col-auto mt-4 mx-auto alert alert-dark">
            There is no bank account yet!
        </div>
    </div>
</c:if>
