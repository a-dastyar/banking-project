<%@ include file="/views/components/commons/imports.jsp" %>
<c:if test="${savingsList.size() > 0}">
    <table class="table border-dark">
        <thead>
            <tr>
                <th scope="col">Account number</th>
                <th scope="col">Owner</th>
                <th scope="col">Balance</th>
                <th scope="col">Minimum balance</th>
                <th scope="col">Interest</th>
                <th scope="col">Created</th>
                <th scope="col">Details</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="account" items="${savingsList}">
                <fmt:parseDate  value="${account.getCreatedAt()}"  type="both" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                <fmt:formatDate value="${parsedDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" var="date" />
                <tr>
                    <td>${account.getAccountNumber()}</td>
                    <td>${account.getAccountHolder().getUsername()}</td>
                    <td>${account.getBalance()}</td>
                    <td>${account.getMinimumBalance()}</td>
                    <td>${account.getInterestRate()}% / ${account.getInterestPeriod()}</td>
                    <td>${date}</td>
                    <td><a class="link-secondary" href="${pageContext.request.contextPath}/saving-accounts/details?account_number=${account.accountNumber}">
                        Details
                    </a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${savingsList == null || savingsList.size() == 0}">
    <div class="container-sm row text-center justify-content-center" role="alert">
        <div class="col-auto mt-4 mx-auto alert alert-dark">
            There is no saving account yet!
        </div>
    </div>
</c:if>