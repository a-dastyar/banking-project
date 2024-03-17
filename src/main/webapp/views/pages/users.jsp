<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        
        <title>Users | page ${users.page()}</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <%@ include file="/views/components/header.jsp" %>
        <div
            class="container-sm d-flex flex-column justify-content-center shadow-lg p-3 mt-5 mb-5 rounded" style="background: #b5b5b594;">

            <table class="table">
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
        </div>
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>