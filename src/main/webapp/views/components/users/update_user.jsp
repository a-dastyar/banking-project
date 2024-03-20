<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75">
    <form method="POST" action="${pageContext.request.contextPath}/users/details">
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="username">Username</label>
            <div class="col-sm-6" >
                <input type="text" class="form-control" id="username" name="username" value="${user.username}">
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="email">Email</label>
            <div class="col-sm-6" >
                <input type="email" class="form-control" id="email" name="email" value="${user.email}">
            </div>
        </div>
        <div class="form-group">
            <label for="roles">Roles</label>
            <select multiple class="form-control" id="roles" name="roles">
                <c:forEach var="role" items="${roles}">
                    <option ${userRoles[role.toString()]?"selected":""} >${role}</option>
                </c:forEach>
            </select>
        </div>
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>