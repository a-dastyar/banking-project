<div class="row">
    <div class="col rounded m-3 p-3 border">
        <div id="bank_account_deposit" class="" data-bs-parent="#accordionExample">
            <div class="accordion-body">
                <form method="POST" action="${pageContext.request.contextPath}/${balanceEndpoint}" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="deposit-amount" class="form-label">Amount</label>
                        <div class="has-validation" >
                            <input type="number" class="form-control" id="deposit-amount" name="amount" min="${minDeposit}" required>
                            <div class="invalid-feedback" id="deposit-amount-feedback">
                                Minimum deposit amount is ${minDeposit}.
                            </div>
                        </div>
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
                <form method="POST" action="${pageContext.request.contextPath}/${balanceEndpoint}" class="needs-validation" id="withdraw-form" novalidate>
                    <div class="mb-3">
                        <label for="withdraw-amount" class="form-label">Amount</label>
                        <div class="has-validation" >
                            <input type="number" class="form-control" id="withdraw-amount" name="amount" min="${minWithdraw}" max="${maxWithdraw}" required>
                            <div class="invalid-feedback" id="withdraw-amount-feedback">
                                Allowed withdraw amount is range [${minWithdraw}, ${maxWithdraw}].
                            </div>
                        </div>
                        <input hidden class="form-control" id="account_number" name="account_number" value="${account.accountNumber}">
                        <input hidden class="form-control" id="type" name="type" value="WITHDRAW">
                    </div>
                    <button type="submit" class="btn btn-primary">Withdraw</button>
                </form>
            </div>
        </div>
    </div>
</div>