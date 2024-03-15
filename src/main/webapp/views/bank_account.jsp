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

            <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#bank_account_sum_balance"
                aria-expanded="false" aria-controls="bank_account_sum_balance">Sum Higher Than</button>

            <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#bank_account_read_form"
                aria-expanded="false" aria-controls="bank_account_read_form">Read BankAccount</button>

            <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#bank_account_create_form"
                aria-expanded="false" aria-controls="bank_account_create_form">Create BankAccount</button>

            <c:if test="${accountExists}">

                <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#bank_account_deposit_form"
                    aria-expanded="false" aria-controls="bank_account_deposit_form">Deposit</button>
                    
                <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#bank_account_withdraw_form"
                aria-expanded="false" aria-controls="bank_account_withdraw_form">Withdraw</button>

            </c:if>

            <form class="collapse" method="POST" action="bank_account_sum_balance" id="bank_account_sum_balance" >
                <div class="mb-3">
                    <label for="limit" class="form-label">Limit</label>
                    <input type="number" class="form-control" id="limit" name="limit">
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>

            <form class="collapse" method="POST" action="bank_account" id="bank_account_read_form" >
                <div class="mb-3">
                    <label for="account_number" class="form-label">Account Number</label>
                    <input type="text" class="form-control" id="account_number" name="account_number">
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>

            <form class="collapse" method="POST" action="bank_account_create" id="bank_account_create_form" >
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
                <input type="hidden" class="invisible" hidden>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>

            <c:if test="${accountExists}">
    
                <form class="collapse" method="POST" action="bank_account_deposit" id="bank_account_deposit_form" >
                    <div class="mb-3">
                        <label for="deposit_amount" class="form-label">Deposit Amount</label>
                        <input type="number" class="form-control" id="deposit_amount" name="deposit_amount">
                    </div>
                    <input type="hidden" class="hidden" name="account_number" value="${account.getAccountNumber()}">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
    
                <form class="collapse" method="POST" action="bank_account_withdraw" id="bank_account_withdraw_form" >
                    <div class="mb-3">
                        <label for="withdraw_amount" class="form-label">Withdraw Amount</label>
                        <input type="number" class="form-control" id="withdraw_amount" name="withdraw_amount">
                    </div>
                    <input type="hidden" class="hidden" name="account_number" value="${account.getAccountNumber()}">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
            </c:if>
        <%@ include file="footer.jsp" %>
    </body>

</html>
