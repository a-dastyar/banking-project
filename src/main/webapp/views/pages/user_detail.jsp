<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        
        <title>Users | ${user.getUsername()}</title>
    </head>

    <body>
        <c:set var="menu" value="users"/>
        <div class="background blurred"></div>
        <%@ include file="/views/components/header.jsp" %>
        <div class="container-sm  mt-3 mid-container rounded" >
            <div class="row h-100">
                <div class="col-3 m-1 p-1 rounded" style="background-color: #65666638;">
                    <div class="d-flex align-items-start">
                        <div class="nav flex-column nav-pills me-3 w-100" id="v-pills-tab" role="tablist" aria-orientation="vertical">
                          <button class="nav-link ${sumExists?'':'active'}" id="v-pills-info-tab" data-bs-toggle="pill" data-bs-target="#v-pills-info" type="button" role="tab" aria-controls="v-pills-info" aria-selected="true">User information</button>
                          <button class="nav-link" id="v-pills-add-tab" data-bs-toggle="pill" data-bs-target="#v-pills-add" type="button" role="tab" aria-controls="v-pills-add" aria-selected="false">Update</button>
                        </div>
                    </div>
                </div>
              <div class="col m-1 p-1 rounded" style="background-color: #65666638;">
                <div class="d-flex align-items-start">
                    <div class="tab-content w-100" id="v-pills-tabContent">
                        <div class="tab-pane rounded fade ${sumExists?'':'show active'}" id="v-pills-info" role="tabpanel" aria-labelledby="v-pills-info-tab" tabindex="0">
                        <table class="table table-secondary">
                            <dl class="row">
                                <dt class="col-sm-3">Username</dt>
                                <dd class="col-sm-9">${user.getUsername()}</dd>
                                <dt class="col-sm-3">Email</dt>
                                <dd class="col-sm-9">${user.getEmail()}</dl>
                                <dt class="col-sm-3">Roles</dt>
                                <c:forEach var="role" items="${user.getRoles()}">
                                    <dd class="col-sm-9">${role}</dd>
                                </c:forEach>
                            </dl>
                        </table>
                        </div>
                        <div class="tab-pane fade" id="v-pills-add" role="tabpanel" aria-labelledby="v-pills-add-tab" tabindex="0">
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
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                  </div>
              </div>
            </div>
          </div>
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>