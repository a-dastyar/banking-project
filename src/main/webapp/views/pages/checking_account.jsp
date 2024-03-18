<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>
    <c:set var="sumExists" value="${sum != null}"/>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <title>Checking accounts</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <%@ include file="/views/components/header.jsp" %>

        <div class="container-sm  mt-3 mid-container rounded" >
            <div class="row h-100">
                <div class="col-3 m-1 p-1 rounded" style="background-color: #65666638;">
                    <div class="d-flex align-items-start">
                        <div class="nav flex-column nav-pills me-3 w-100" id="v-pills-tab" role="tablist" aria-orientation="vertical">
                          <button class="nav-link ${sumExists?'':'active'}" id="v-pills-list-tab" data-bs-toggle="pill" data-bs-target="#v-pills-list" type="button" role="tab" aria-controls="v-pills-list" aria-selected="true">List accounts</button>
                          <button class="nav-link" id="v-pills-add-tab" data-bs-toggle="pill" data-bs-target="#v-pills-add" type="button" role="tab" aria-controls="v-pills-add" aria-selected="false">Add account</button>
                          <button class="nav-link  ${sumExists?'active':''}" id="v-pills-sum-tab" data-bs-toggle="pill" data-bs-target="#v-pills-sum" type="button" role="tab" aria-controls="v-pills-sum" aria-selected="false">Sum balance</button>
                        </div>
                    </div>
                </div>
              <div class="col m-1 p-1 rounded" style="background-color: #65666638;">
                <div class="d-flex align-items-start">
                    <div class="tab-content w-100" id="v-pills-tabContent">
                      <div class="tab-pane rounded fade ${sumExists?'':'show active'}" id="v-pills-list" role="tabpanel" aria-labelledby="v-pills-list-tab" tabindex="0">
                        <table class="table table-secondary">
                            <thead>
                                <tr>
                                    <th scope="col">Account number</th>
                                    <th scope="col">Owner</th>
                                    <th scope="col">Balance</th>
                                    <th scope="col">Overdraft limit</th>
                                    <th scope="col">Debt</th>
                                    <th scope="col">Details</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="account" items="${accounts.list()}">
                                    <tr>
                                        <td>${account.getAccountNumber()}</td>
                                        <td>${account.getAccountHolder().getUsername()}</td>
                                        <td>${account.getBalance()}</td>
                                        <td>${account.getOverdraftLimit()}</td>
                                        <td>${account.getDebt()}</td>
                                        <td><a class="link-secondary" href="${pageContext.request.contextPath}/checking-accounts/details?account_number=${account.accountNumber}">
                                            Details
                                        </a></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                      </div>
                      <div class="tab-pane fade" id="v-pills-add" role="tabpanel" aria-labelledby="v-pills-add-tab" tabindex="0">
                        <div class="container-sm w-75">
                            <form method="POST" action="${pageContext.request.contextPath}/checking-accounts">
                                <div class="mb-3 row">
                                    <label class="col-form-label col-sm-3" for="account_number">Account number</label>
                                    <div class="col-sm-6" >
                                        <input  type="text" class="form-control" id="account_number" name="account_number">
                                    </div>
                                </div>
                                <div class="mb-3 row">
                                    <label class="col-form-label col-sm-3" for="username" >Holder username</label>
                                    <div class="col-sm-6" >
                                        <input type="text" class="form-control" id="username" name="username">
                                    </div>
                                </div>
                                <div class="mb-3 row">
                                    <label class="col-form-label col-sm-3" for="balance">Balance</label>
                                    <div class="col-sm-6" >
                                        <input type="number" class="form-control" id="balance" name="balance">
                                    </div>
                                </div>
                                <div class="mb-3 row">
                                    <label class="col-form-label col-sm-3" for="overdraft_limit">Overdraft limit</label>
                                    <div class="col-sm-6" >
                                        <input type="number" class="form-control" id="overdraft_limit" name="overdraft_limit">
                                    </div>
                                </div>
                                <div class="mb-3 row">
                                    <label class="col-form-label col-sm-3" for="debt">Debt</label>
                                    <div class="col-sm-6" >
                                        <input type="number" class="form-control" id="debt" name="debt">
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary">Submit</button>
                            </form>
                        </div>
                            
                      </div>
                      <div class="tab-pane fade ${sumExists?'show active':''}" id="v-pills-sum" role="tabpanel" aria-labelledby="v-pills-sum-tab" tabindex="0">
                            <div class="m-3">
                                <form method="GET" action="${pageContext.request.contextPath}/checking-accounts">
                                    <div class="row mb-3">
                                        <label for="sum_min" class="col-sm-2 col-form-label">Min balance:</label>
                                        <div class="col-sm">
                                            <input type="text" class="form-control" id="sum_min" name="sum_min">
                                        </div>
                                        <div class="col">
                                            <button type="submit" class="btn btn-primary">Submit</button>
                                        </div>
                                    </div>
                                </form>
                            </div>    
                            <c:if test="${sumExists}">
                                <table class="table table-secondary">
                                    <thead>
                                        <tr>
                                            <th scope="col">Sum balance</th>
                                            <th scope="col">Higher than</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>${sum}</td>
                                            <td>${min}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </c:if>
                      </div>
                    </div>
                  </div>
              </div>
            </div>
          </div>
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>
