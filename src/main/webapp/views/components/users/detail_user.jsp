<%@ include file="/views/components/commons/imports.jsp" %>
<dl class="row">
    <dt class="col-sm-3">Username</dt>
    <dd class="col-sm-9">${user.getUsername()}</dd>
    <dt class="col-sm-3">Email</dt>
    <dd class="col-sm-9">${user.getEmail()}</dl>
    <c:if test="${!userDashboard}">
        <dt class="col-sm-3">Roles</dt>
        <c:forEach var="role" items="${user.getRoles()}">
            <dd class="col-sm-9">${role}</dd>
        </c:forEach>
    </c:if>
</dl>