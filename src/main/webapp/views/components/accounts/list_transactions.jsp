<%@ include file="/views/components/commons/imports.jsp" %>
<table class="table border-dark caption-top">
    <caption class="text-dark rounded-end p-1 pr-2 pr-2 mb-2">Transactions</caption>
    <thead>
        <tr>
            <th scope="col">Amount</th>
            <th scope="col">Type</th>
            <th scope="col">Date</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="transaction" items="${transactions.list()}">
            <fmt:parseDate  value="${transaction.date}"  type="both" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" parseLocale="fa_IR" />
            <fmt:formatDate value="${parsedDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" var="date" />
            <tr>
                <td>${transaction.getAmount()}</td>
                <td>${transaction.getType()}</td>
                <td>${date}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>