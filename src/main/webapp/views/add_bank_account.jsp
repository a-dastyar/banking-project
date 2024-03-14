<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <%@ include file="header.jsp" %>
        <title>BankAccount<c:if test="${account != null}"> | page of ${account.getAccountNumber()}</c:if></title>
    </head>

    <body>
        <div class="container-sm">

            <a class="btn btn-primary" href="/banking">Go Home</a>
            <a class="btn btn-primary" href="bank_account">Go Get BankAccount</a>

            <table class="table ">
                <thead>
                    <tr>
                        <th scope="col">Account Number</th>
                        <th scope="col">Account Holder Name</th>
                        <th scope="col">Balance</th>
                    </tr>
                </thead>
                <c:if test="${account != null}">
                    <tbody>
                        <tr>
                            <td>${account.getAccountNumber()}</td>
                            <td>${account.getAccountHolderName()}</td>
                            <td>${account.getBalance()}</td>
                        </tr>
                    </tbody>
                </c:if>
            </table>

            <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#add_bank_account_form"
                aria-expanded="false" aria-controls="add_bank_account_form">Add BankAccount</button>

            <form class="collapse" method="POST" action="add_bank_account" id="add_bank_account_form" >
                <div class="mb-3">
                    <label for="accountNumber" class="form-label">Account Number</label>
                    <input type="text" class="form-control" id="accountNumber" name="accountNumber">
                </div>
                <div class="mb-3">
                    <label for="accountHolderName" class="form-label">Account Holder Name</label>
                    <input type="text" class="form-control" id="accountHolderName" name="accountHolderName">
                </div>
                <div class="mb-3">
                    <label for="balance" class="form-label">Balance</label>
                    <input type="number" class="form-control" id="balance" name="balance">
                </div>
                <input type="hidden" class="invisible" hidden>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        </div>
        <%@ include file="footer.jsp" %>
    </body>

</html>
