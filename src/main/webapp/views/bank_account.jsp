<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <%@ include file="header.jsp" %>
        <title>BankAccount<c:if test="${accountExists}"> | page of ${account.getAccountNumber()}</c:if></title>
    </head>

    <c:set var="accountExists" value="${account != null}"/>
    <c:set var="sumExists" value="${sum != null}"/>
    <c:set var="limitExists" value="${limit != null}"/>
    <body>
        <div class="container-sm">

            <a class="btn btn-primary" href="/banking">Go Home</a>

            <c:if test="${sumExists && limitExists}">
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col">Sum Balance</th>
                            <th scope="col">Higher Than</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${sum}</td>
                            <td>${limit}</td>
                        </tr>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${accountExists}">
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col">Account Number</th>
                            <th scope="col">Account Holder Name</th>
                            <th scope="col">Balance</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${account.getAccountNumber()}</td>
                            <td>${account.getAccountHolderName()}</td>
                            <td>${account.getBalance()}</td>
                        </tr>
                    </tbody>
                </table>
            </c:if>

            <div class="row">
                <c:choose>
                    <c:when test="${accountExists}">
                        <div class="accordion col-6" id="accordion">
                    </c:when>
                    <c:otherwise>
                        <div class="accordion" id="accordion">
                    </c:otherwise>
                </c:choose>
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#bank_account_sum_balance1" aria-expanded="true" aria-controls="bank_account_sum_balance1">
                                Sum Higher Than
                            </button>
                        </h2>
                        <div id="bank_account_sum_balance1" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="bank_account_sum_balance">
                                    <div class="mb-3">
                                        <label for="limit" class="form-label">Limit</label>
                                        <input type="number" class="form-control" id="limit" name="limit">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#bank_account_read" aria-expanded="false" aria-controls="bank_account_read">
                                Read BankAccount
                            </button>
                        </h2>
                        <div id="bank_account_read" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="bank_account">
                                    <div class="mb-3">
                                        <label for="account_number" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="account_number" name="account_number">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#bank_account_create" aria-expanded="false" aria-controls="bank_account_create">
                                Create BankAccount
                            </button>
                        </h2>
                        <div id="bank_account_create" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="bank_account_create">
                                    <div class="mb-3">
                                        <label for="account_number" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="account_number" name="account_number">
                                    </div>
                                    <div class="mb-3">
                                        <label for="account_holder_name" class="form-label">Account Holder Name</label>
                                        <input type="text" class="form-control" id="account_holder_name" name="account_holder_name">
                                    </div>
                                    <div class="mb-3">
                                        <label for="balance" class="form-label">Balance</label>
                                        <input type="number" class="form-control" id="balance" name="balance">
                                    </div>
                                    <div class="mb-3">
                                        <label for="type" class="form-label">Type</label>
                                        <select class="form-select" id="type" name="type">
                                            <option value="1" selected>BankAccount</option>
                                            <option value="2">CheckingAccount</option>
                                            <option value="3">SavingAccount</option>
                                        </select>
                                        <c:set var="account_type" value="${param.type}"/>
                                        <c:set var="checking" value="2"/>
                                        <c:set var="saving" value="3"/>
                                        <c:out value="${param.type}"/>
                                        <c:choose>
                                            <c:when test="{account_type == checking}">
                                                <div class="mb-3">
                                                    <label for="over_draft_limit" class="form-label">Over Draft Limit</label>
                                                    <input type="number" class="form-control" id="over_draft_limit" name="over_draft_limit">
                                                </div>
                                                <div class="mb-3">
                                                    <label for="debt" class="form-label">Debt</label>
                                                    <input type="number" class="form-control" id="debt" name="debt">
                                                </div>
                                            </c:when>
                                            <c:when test="${account_type == saving}">
                                                <div class="mb-3">
                                                    <label for="interest_rate" class="form-label">Interest Rate</label>
                                                    <input type="number" class="form-control" id="interest_rate" name="interest_rate">
                                                </div>
                                                <div class="mb-3">
                                                    <label for="interest_period" class="form-label">Interest Period</label>
                                                    <input type="number" class="form-control" id="interest_period" name="interest_period">
                                                </div>
                                                <div class="mb-3">
                                                    <label for="minimum_balance" class="form-label">Minimum Balance</label>
                                                    <input type="number" class="form-control" id="minimum_balance" name="minimum_balance">
                                                </div>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
    
                <c:if test="${accountExists}">
                    <div class="accordion col-6" id="accordionExample">
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#bank_account_deposit" aria-expanded="true" aria-controls="bank_account_deposit">
                                    Deposit
                                </button>
                            </h2>
                            <div id="bank_account_deposit" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form method="POST" action="bank_account_deposit">
                                        <div class="mb-3">
                                            <label for="deposit_amount" class="form-label">Deposit Amount</label>
                                            <input type="number" class="form-control" id="deposit_amount" name="deposit_amount">
                                        </div>
                                        <button type="submit" class="btn btn-primary">Submit</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#bank_account_withdraw" aria-expanded="false" aria-controls="bank_account_withdraw">
                                    Withdraw
                                </button>
                            </h2>
                            <div id="bank_account_withdraw" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form method="POST" action="bank_account_withdraw">
                                        <div class="mb-3">
                                            <label for="withdraw_amount" class="form-label">Withdraw Amount</label>
                                            <input type="number" class="form-control" id="withdraw_amount" name="withdraw_amount">
                                        </div>
                                        <button type="submit" class="btn btn-primary">Submit</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                      </div>
                </c:if>
            </div>

        </div>
        <%@ include file="footer.jsp" %>
    </body>

</html>
