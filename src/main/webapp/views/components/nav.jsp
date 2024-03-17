<nav class="navbar navbar-expand-lg" style="background-color: #c6c6c600;border-bottom: 1px solid #cbd6df;">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Banking System</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item p-1 pt-0 pb-0 m-1" style="border: 1px solid #8691a294;border-radius: 5px;">
                    <a class="nav-link active"  href=".">Home</a>
                </li>
                <li class="nav-item p-1 pt-0 pb-0 m-1" style="border: 1px solid #8691a294;border-radius: 5px;">
                    <a class="nav-link active"  href="users">Users</a>
                </li>
            </ul>
        </div>
        <c:if test="${pageContext.request.userPrincipal!=null}">
            <div class="dropdown">
                <a class="nav-link dropdown-toggle p-1 text" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    ${pageContext.request.userPrincipal.name}
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li>
                        <form method="post" action="logout">
                            <button type="submit" class="dropdown-item">Logout</button>
                        </form>
                    </li>
                </ul>
            </div>
        </c:if>
        <c:if test="${pageContext.request.userPrincipal==null}">
            <a  class="btn btn-secondary" href="login">Login</a>
        </c:if>
    </div>
</nav>