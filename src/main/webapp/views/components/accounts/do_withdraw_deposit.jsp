<div class="row">
    <div class="col rounded m-3 p-3 border">
        <div id="bank_account_deposit" class="" data-bs-parent="#accordionExample">
            <div class="accordion-body">
                <form method="POST" action="${pageContext.request.contextPath}/${endPoint}/balance">
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
                <form method="POST" action="${pageContext.request.contextPath}/${endPoint}/balance">
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