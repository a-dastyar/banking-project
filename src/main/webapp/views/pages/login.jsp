<!DOCTYPE html>
<html lang="en">
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <title>Login</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center" >
            <div class="container-sm shadow-lg p-3 mb-5 rounded" style="max-width: 400px;background: #b6bbbf70;">
                <form method="POST" action="j_security_check" id="add-user-form">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="j_username">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="j_password">
                    </div>
                    <div>
                        <p class="mb-0">Don't have an account? <a
                                class="link-dark link-underline-opacity-25 link-underline-opacity-100-hover"
                                href="signup">Sign Up</a>
                        </p>
                    </div>
                    <c:if test="${param.failed}">
                        <div class="row alert alert-danger d-flex align-items-center p-2 m-2" role="alert">
                            <div>Invalid credentials!</div>
                        </div>
                    </c:if>
                    <button type="submit" class="btn btn-secondary">Login</button>
                </form>
            </div>
        </div>
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>