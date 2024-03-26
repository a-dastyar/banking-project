<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75 mt-3 p-3 border rounded">
    <form method="POST" action="${pageContext.request.contextPath}/${endpoint}" class="needs-validation" novalidate>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-sm-3" for="username">Username</label>
            <div class="col-sm-6" >
                <input type="text" class="form-control" id="username" name="username" pattern=".{3,20}" required>
                <div class="invalid-feedback">
                    Please choose a username with [3-20] characters.
                </div>
            </div>
        </div>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-sm-3" for="email">Email</label>
            <div class="col-sm-6" >
                <input type="email" class="form-control" id="email" name="email" pattern=".+@.+\..{2,}" maxlength="20" required>
                <div class="invalid-feedback">
                    Please enter a valid email address.
                </div>
            </div>
        </div>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-sm-3" for="password">Password</label>
            <div class="col-sm-6" >
                <input type="password" class="form-control" id="password" name="password" pattern=".{4,50}" required>
                <div class="invalid-feedback">
                    Please enter a password with [4-50] characters.
                </div>
            </div>
        </div>
        <div class="mb-3 w-75 row offset-sm-2 form-group">
            <label for="roles" class="col-sm-3" >Roles</label>
            <select multiple class="form-control col-sm" id="roles" name="roles" required>
                <c:forEach var="role" items="${roles}">
                    <option>${role}</option>
                </c:forEach>
            </select>
            <div class="invalid-feedback">
                Please select at least one role.
            </div>
        </div>
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>