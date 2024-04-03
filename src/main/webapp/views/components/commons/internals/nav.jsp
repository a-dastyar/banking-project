<c:set var="hasManagementAccess" value="${pageContext.request.isUserInRole('MANAGER') || pageContext.request.isUserInRole('ADMIN')}"/>
<nav class="navbar navbar-expand-lg" style="background-color: #bbc3c62e;border-bottom: 1px solid #cbd6df;">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Banking System</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav nav-underline">
                <li class="nav-item main-nav" >
                    <a class="nav-link mt-1 ${menu=='home'?'active':''}"  href="${pageContext.request.contextPath}/">Home</a>
                </li>
                <c:if test='${hasManagementAccess}'>
                    <li class="nav-item main-nav">
                        <a class="nav-link mt-1 ${menu=='banks'?'active':''}"  href="${pageContext.request.contextPath}/bank-accounts">Bank Accounts</a>
                    </li>
                    <li class="nav-item main-nav">
                        <a class="nav-link mt-1 ${menu=='savings'?'active':''}"  href="${pageContext.request.contextPath}/saving-accounts">Saving Accounts</a>
                    </li>
                    <li class="nav-item main-nav">
                        <a class="nav-link mt-1 ${menu=='checkings'?'active':''}"  href="${pageContext.request.contextPath}/checking-accounts">Checking Accounts</a>
                    </li>
                    <li class="nav-item main-nav">
                        <a class="nav-link mt-1 ${menu=='users'?'active':''}"  href="${pageContext.request.contextPath}/users">Users</a>
                    </li>
                </c:if>
            </ul>
        </div>
        <c:if test="${pageContext.request.userPrincipal!=null}">
            <div class="dropdown">
                <a class="nav-link dropdown-toggle p-1 text" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    ${pageContext.request.userPrincipal.name}
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li>
                        <a class="dropdown-item"  href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                    </li>
                    <li>
                        <form method="post" action="${pageContext.request.contextPath}/logout">
                            <button type="submit" class="dropdown-item">Logout</button>
                        </form>
                    </li>
                </ul>
            </div>
        </c:if>
        <c:if test="${pageContext.request.userPrincipal==null}">
            <a  class="btn btn-primary" href="${pageContext.request.contextPath}/login">Login</a>
        </c:if>
    </div>
</nav>