<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <%@ include file="/views/components/imports.jsp" %>

    <c:set var="checkingAccountExists" value="${checking_account != null}"/>
    <c:if test="${checkingAccountExists}">
        <c:set var="account" value="${checking_account}"/>
    </c:if>
    <c:set var="sumExists" value="${sum != null}"/>
    <c:set var="limitExists" value="${limit != null}"/>

    <head>
        <%@ include file="/views/components/meta.jsp" %>
        <title>Checking Account<c:if test="${checkingAccountExists}"> | page of ${account.getAccountNumber()}</c:if></title>
    </head>

    <body>
        <%@ include file="/views/components/header.jsp" %>
        <div class="container-sm">

            <a class="btn btn-primary" href="/banking">Go Home</a>
            <a class="btn btn-primary" href="bank_account">Go To Bank Account</a>
            <a class="btn btn-primary" href="saving_account">Go To Saving Account</a>

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

            <c:if test="${checkingAccountExists}">
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col">Account Number</th>
                            <th scope="col">Account Holder Name</th>
                            <th scope="col">Over Draft Limit</th>
                            <th scope="col">Debt</th>
                            <th scope="col">Balance</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${account.getAccountNumber()}</td>
                            <td>${account.getAccountHolderName()}</td>
                            <td>${account.getOverDraftLimit()}</td>
                            <td>${account.getDebt()}</td>
                            <td>${account.getBalance()}</td>
                        </tr>
                    </tbody>
                </table>
            </c:if>

            <div class="row">
                <c:choose>
                    <c:when test="${checkingAccountExists}">
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
                        <div id="bank_account_sum_balance" class="accordion-collapse collapse" data-bs-parent="#accordion">
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
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#checking_account_read" aria-expanded="false" aria-controls="bank_account_read">
                                Read Checking Account
                            </button>
                        </h2>
                        <div id="checking_account_read" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="checking_account">
                                    <div class="mb-3">
                                        <label for="checking_account_number_r" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="checking_account_number_r" name="checking_account_number_r">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#checking_account_create" aria-expanded="false" aria-controls="checking_account_create">
                                Create Checking Account
                            </button>
                        </h2>
                        <div id="checking_account_create" class="accordion-collapse collapse" data-bs-parent="#accordion">
                            <div class="accordion-body">
                                <form method="POST" action="checking_account_create">
                                    <div class="mb-3">
                                        <label for="checking_account_number" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="checking_account_number" name="checking_account_number">
                                    </div>
                                    <div class="mb-3">
                                        <label for="checking_account_holder_name" class="form-label">Account Holder Name</label>
                                        <input type="text" class="form-control" id="checking_account_holder_name" name="checking_account_holder_name">
                                    </div>
                                    <div class="mb-3">
                                        <label for="checking_balance" class="form-label">Balance</label>
                                        <input type="number" class="form-control" id="checking_balance" name="checking_balance">
                                    </div>
                                    <div class="mb-3">
                                        <label for="checking_over_draft_limit" class="form-label">Over Draft Limit</label>
                                        <input type="number" class="form-control" id="checking_over_draft_limit" name="checking_over_draft_limit">
                                    </div>
                                    <div class="mb-3">
                                        <label for="checking_debt" class="form-label">Debt</label>
                                        <input type="number" class="form-control" id="checking_debt" name="checking_debt">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
    
                <c:if test="${accountExists || checkingAccountExists || savingAccountExists}">
                    <div class="accordion col-6" id="accordionExample">
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#checking_account_deposit" aria-expanded="true" aria-controls="checking_account_deposit">
                                    Deposit
                                </button>
                            </h2>
                            <div id="checking_account_deposit" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form method="POST" action="checking_account_deposit">
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
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#checking_account_withdraw" aria-expanded="false" aria-controls="checking_account_withdraw">
                                    Withdraw
                                </button>
                            </h2>
                            <div id="checking_account_withdraw" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form method="POST" action="checking_account_withdraw">
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
