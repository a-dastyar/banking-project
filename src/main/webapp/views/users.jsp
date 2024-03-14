<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <%@ include file="header.jsp" %>
        <title>Users | page ${users.page()}</title>
    </head>

    <body>
        <div class="container-sm">

            <table class="table ">
                <thead>
                    <tr>
                        <th scope="col">Username</th>
                        <th scope="col">Email</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users.list()}">
                        <tr>
                            <td>${user.getUsername()}</td>
                            <td>${user.getEmail()}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#add-user-form"
                aria-expanded="false" aria-controls="add-user-form">Add user</button>
            <form class="collapse" method="POST" action="users" id="add-user-form" >
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
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        </div>
        <%@ include file="footer.jsp" %>
    </body>

</html>