<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/commons/imports.jsp" %>

    <head>
        <%@ include file="/views/components/commons/meta.jsp" %>
        <title>Login</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center">
            <div class="container-sm shadow-lg p-3 mb-5 rounded" style="max-width: 400px;background: #b6bbbf70;">
                <form method="POST" action="${pageContext.request.contextPath}/signup" id="add-user-form" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" pattern=".{3,}" maxlength="20" required>
                        <div class="invalid-feedback">
                            Please choose a username with [3-20] characters.
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email address</label>
                        <input type="email" class="form-control" id="email" aria-describedby="emailHelp" name="email" pattern=".+@.+\..{2,}" maxlength="20" required>
                        <div class="invalid-feedback">
                            Please enter a valid email address.
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" pattern=".{4,}" maxlength="50" required>
                        <div class="invalid-feedback">
                            Please choose a password with [4-50] characters.
                        </div>
                    </div>
                    <div class="row justify-content-center align-items-center">
                        <button type="submit" class="btn btn-primary col-sm-3 mt-2">Sign up</button>
                        <a href="${pageContext.request.contextPath}" class="link-dark link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover">Home</a>
                    </div>
                </form>
            </div>
        </div>
        <%@ include file="/views/components/commons/footer.jsp" %>
    </body>

</html>