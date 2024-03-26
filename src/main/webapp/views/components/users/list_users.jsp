<%@ include file="/views/components/commons/imports.jsp" %>
<table class="table border-dark">
    <thead>
        <tr>
            <th scope="col">Username</th>
            <th scope="col">Email</th>
            <th scope="col">Created</th>
            <th scope="col">Details</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="user" items="${users.list()}">
            <fmt:parseDate  value="${user.getCreatedAt()}"  type="both" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" parseLocale="fa_IR" />
            <fmt:formatDate value="${parsedDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" var="date" />
            <tr>
                <td>${user.getUsername()}</td>
                <td>${user.getEmail()}</td>
                <td>${date}</td>
                <td><a class="link-secondary" href="${pageContext.request.contextPath}/users/details?username=${user.username}">
                    Details
                </a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<c:set var="page" value="${users}"/>
<c:set var="pageParamName" value="page"/>
<%@ include file="/views/components/commons/pagination.jsp" %>