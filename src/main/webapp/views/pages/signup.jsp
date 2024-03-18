<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <title>Login</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center">
            <div class="container-sm shadow-lg p-3 mb-5 rounded" style="max-width: 400px;background: #b6bbbf70;">
                <form method="POST" action="${pageContext.request.contextPath}/signup" id="add-user-form">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username">
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email address</label>
                        <input type="email" class="form-control" id="email" aria-describedby="emailHelp" name="email">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password">
                    </div>
                    <button type="submit" class="btn btn-secondary">Sign up</button>
                </form>
            </div>
        </div>
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>