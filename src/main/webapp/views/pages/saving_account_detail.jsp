<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <title>Saving account | ${account.getAccountNumber()}</title>
    </head>

    <body>
        <div class="background blurred"></div>
        <%@ include file="/views/components/header.jsp" %>

        <div class="container-sm  mt-3 mid-container rounded" >
            <div class="row h-100">
                <div class="col-3 m-1 p-1 rounded" style="background-color: #65666638;">
                    <div class="d-flex align-items-start">
                        <div class="nav flex-column nav-pills me-3 w-100" id="v-pills-tab" role="tablist" aria-orientation="vertical">
                          <button class="nav-link active" id="v-pills-info-tab" data-bs-toggle="pill" data-bs-target="#v-pills-info" type="button" role="tab" aria-controls="v-pills-info" aria-selected="true">Account info</button>
                          <button class="nav-link" id="v-pills-add-tab" data-bs-toggle="pill" data-bs-target="#v-pills-add" type="button" role="tab" aria-controls="v-pills-add" aria-selected="false">Deposit/Withdraw</button>
                        </div>
                    </div>
                </div>
              <div class="col m-1 p-1 rounded" style="background-color: #65666638;">
                <div class="d-flex align-items-start">
                    <div class="tab-content w-100" id="v-pills-tabContent">
                      <div class="tab-pane rounded show active" id="v-pills-info" role="tabpanel" aria-labelledby="v-pills-info-tab" tabindex="0">
                        <dl class="row">
                            <dt class="col-sm-3">Account number</dt>
                            <dd class="col-sm-9">${account.getAccountNumber()}</dd>
                            <dt class="col-sm-3">Owner</dt>
                            <dd class="col-sm-9">${account.getAccountHolder().getUsername()}</dd>
                            <dt class="col-sm-3">Current balance</dt>
                            <dd class="col-sm-9">${account.getBalance()}</dd>
                            <dt class="col-sm-3">Minimum balance</dt>
                            <dd class="col-sm-9">${account.getMinimumBalance()}</dd>
                            <dt class="col-sm-3">Interest rate</dt>
                            <dd class="col-sm-9">${account.getInterestRate()}</dd>
                            <dt class="col-sm-3">Interest period</dt>
                            <dd class="col-sm-9">${account.getInterestPeriod()}</dd>
                        </dl>
                        <table class="table table-secondary">
                            <thead>
                                <tr>
                                    <th scope="col">Amount</th>
                                    <th scope="col">Type</th>
                                    <th scope="col">Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="transaction" items="${transactions.list()}">
                                    <fmt:parseDate  value="${transaction.date}"  type="both" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                                    <fmt:formatDate value="${parsedDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" var="date" />
                                    <tr>
                                        <td>${transaction.getAmount()}</td>
                                        <td>${transaction.getType()}</td>
                                        <td>${date}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                      </div>
                      <div class="tab-pane fade" id="v-pills-add" role="tabpanel" aria-labelledby="v-pills-add-tab" tabindex="0">
                        <div class="row">
                            <div class="col rounded m-3 p-3 border">
                                <div id="bank_account_deposit" class="" data-bs-parent="#accordionExample">
                                    <div class="accordion-body">
                                        <form method="POST" action="${pageContext.request.contextPath}/saving-accounts/balance">
                                            <div class="mb-3">
                                                <label for="amount" class="form-label">Amount</label>
                                                <input type="number" class="form-control" id="mount" name="amount">
                                                <input hidden class="form-control" id="account_number" name="account_number" value="${account.accountNumber}">
                                                <input hidden class="form-control" id="type" name="type" value="DEPOSIT">
                                            </div>
                                            <button type="submit" class="btn btn-primary">Deposit</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div class="col rounded m-3 p-3 border">
                                <div id="bank_account_withdraw" class="" data-bs-parent="#accordionExample">
                                    <div class="accordion-body">
                                        <form method="POST" action="${pageContext.request.contextPath}/saving-accounts/balance">
                                            <div class="mb-3">
                                                <label for="amount" class="form-label">Amount</label>
                                                <input type="number" class="form-control" id="amount" name="amount">
                                                <input hidden class="form-control" id="account_number" name="account_number" value="${account.accountNumber}">
                                                <input hidden class="form-control" id="type" name="type" value="WITHDRAW">
                                            </div>
                                            <button type="submit" class="btn btn-primary">Withdraw</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
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
