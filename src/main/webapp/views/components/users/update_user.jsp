<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75 mt-3 p-3 border rounded">
    <form method="POST" action="${pageContext.request.contextPath}/${endpoint}" class="needs-validation" id="update-user" novalidate>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-sm-3" for="username">Username</label>
            <div class="col-sm-6" >
                <input type="text" class="form-control" ${userDashboard?'disabled':''} id="username" name="username" value="${user.username}" original="${user.username}" pattern=".{3,}" maxlength="20" required>
                <div class="invalid-feedback" id="username-feedback">
                    Please choose a username with at least 3 characters.
                </div>
            </div>
        </div>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-sm-3" for="email">Email</label>
            <div class="col-sm-6" >
                <input type="email" class="form-control" id="email" name="email" value="${user.email}" original="${user.email}"  pattern=".+@.+\..{2,}" maxlength="20" required>
                <div class="invalid-feedback" id="email-feedback">
                    Please enter a valid email address.
                </div>
            </div>
        </div>
        <c:if test="${!userDashboard}">
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
        </c:if>
        <div class="mb-3 row justify-content-center align-items-center">
            <div class="collapse col-auto col-offset-3 row alert alert-danger p-2 m-2" id="invalid" style="width: fit-content;"  role="alert">
                <div id="invalid-message"></div>
            </div>
        </div>
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>