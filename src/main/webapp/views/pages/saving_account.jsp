<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <c:set var="savingAccountExists" value="${saving_account != null}"/>
    <c:if test="${savingAccountExists}">
        <c:set var="account" value="${saving_account}"/>
    </c:if>
    <c:set var="sumExists" value="${sum != null}"/>
    <c:set var="limitExists" value="${limit != null}"/>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <%@ include file="/views/components/header.jsp" %>
        <title>BankAccount<c:if test="${savingAccountExists}"> | page of ${account.getAccountNumber()}</c:if></title>
    </head>

    <body>
        <%@ include file="/views/components/header.jsp" %>
        <div class="background blurred"></div>
        <div class="container-sm">

            <a class="btn btn-primary" href="/banking">Go Home</a>
            <a class="btn btn-primary" href="bank_account">Go To Bank Account</a>
            <a class="btn btn-primary" href="checking_account">Go To Checking Account</a>

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

            <c:if test="${savingAccountExists}">
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col">Account Number</th>
                            <th scope="col">Account Holder Name</th>
                            <c:if test="${savingAccountExists}">
                                <th scope="col">Interest Rate</th>
                                <th scope="col">Interest Period</th>
                                <th scope="col">Minimum Balance</th>
                            </c:if>
                            <th scope="col">Balance</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${account.getAccountNumber()}</td>
                            <td>${account.getAccountHolderName()}</td>
                            <c:if test="${savingAccountExists}">
                                <td>${account.getInterestRate()}</td>
                                <td>${account.getInterestPeriod()}</td>
                                <td>${account.getMinimumBalance()}</td>
                            </c:if>
                            <td>${account.getBalance()}</td>
                        </tr>
                    </tbody>
                </table>
            </c:if>

            <div class="row">
                <c:choose>
                    <c:when test="${savingAccountExists}">
                        <div class="accordion col-6" id="accordion">
                    </c:when>
                    <c:otherwise>
                        <div class="accordion" id="accordion">
                    </c:otherwise>
                </c:choose>
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#bank_account_sum_balance" aria-expanded="true" aria-controls="bank_account_sum_balance">
                                Sum Higher Than
                            </button>
                        </h2>
                        <div id="saving_account_sum_balance" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="saving_account_sum_balance">
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
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#saving_account_read" aria-expanded="false" aria-controls="saving_account_read">
                                Read Saving Account
                            </button>
                        </h2>
                        <div id="saving_account_read" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="saving_account">
                                    <div class="mb-3">
                                        <label for="saving_account_number_r" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="saving_account_number_r" name="saving_account_number_r">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#saving_account_create" aria-expanded="false" aria-controls="saving_account_create">
                                Create savingAccount
                            </button>
                        </h2>
                        <div id="saving_account_create" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="saving_account_create">
                                    <div class="mb-3">
                                        <label for="saving_account_number" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="saving_account_number" name="saving_account_number">
                                    </div>
                                    <div class="mb-3">
                                        <label for="saving_account_holder_name" class="form-label">Account Holder Name</label>
                                        <input type="text" class="form-control" id="saving_account_holder_name" name="saving_account_holder_name">
                                    </div>
                                    <div class="mb-3">
                                        <label for="saving_balance" class="form-label">Balance</label>
                                        <input type="number" class="form-control" id="saving_balance" name="saving_balance">
                                    </div>
                                    <div class="mb-3">
                                        <label for="saving_interest_rate" class="form-label">Interest Rate</label>
                                        <input type="number" class="form-control" id="saving_interest_rate" name="saving_interest_rate">
                                    </div>
                                    <div class="mb-3">
                                        <label for="saving_interest_period" class="form-label">Interest Period</label>
                                        <select class="form-select" aria-label="Interest Period select" id="saving_interest_period" name="saving_interest_period">
                                            <option value="MONTHLY">MONTHLY</option>
                                            <option value="YEARLY">YEARLY</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="saving_minimum_balance" class="form-label">Minimum Balance</label>
                                        <input type="number" class="form-control" id="saving_minimum_balance" name="saving_minimum_balance">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
    
                <c:if test="${savingAccountExists}">
                    <div class="accordion col-6" id="accordionExample">
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#saving_account_deposit" aria-expanded="true" aria-controls="saving_account_deposit">
                                    Deposit
                                </button>
                            </h2>
                            <div id="saving_account_deposit" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form method="POST" action="saving_account_deposit">
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
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#saving_account_withdraw" aria-expanded="false" aria-controls="saving_account_withdraw">
                                    Withdraw
                                </button>
                            </h2>
                            <div id="saving_account_withdraw" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form method="POST" action="saving_account_withdraw">
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
        <%@ include file="/views/components/footer.jsp" %>
    </body>

</html>
