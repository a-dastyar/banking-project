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
            <a class="btn btn-primary" href="add_bank_account">Go Add BankAccount</a>

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

            <button type="button" class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#get_bank_account_form"
                aria-expanded="false" aria-controls="get_bank_account_form">Get BankAccount</button>

            <form class="collapse" method="POST" action="bank_account" id="get_bank_account_form" >
                <div class="mb-3">
                    <label for="accountNumber" class="form-label">Account Number</label>
                    <input type="text" class="form-control" id="accountNumber" name="accountNumber">
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        </div>
        <%@ include file="footer.jsp" %>
    </body>

</html>
