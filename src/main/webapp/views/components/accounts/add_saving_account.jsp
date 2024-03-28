<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75 mt-3 p-3 border rounded">
    <form method="POST" action="${pageContext.request.contextPath}/saving-accounts" class="needs-validation add-account-form" id="add-saving-account-form" novalidate>
        <%@ include file="/views/components/accounts/common_form_fields.jsp" %>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-md-4" for="minimum_balance">Minimum balance</label>
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="minimum_balance" name="minimum_balance" value="0" min="0" required>
                <div class="invalid-feedback" id="minimum_balance-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
        </div>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-md-4" for="interest_rate">Interest rate</label>
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="interest_rate" name="interest_rate" value="0" min="0" required>
                <div class="invalid-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
        </div>
        <div class="mb-3 row offset-sm-2">
            <label class="col-form-label col-md-4" for="interest_period">Interest period</label>
            <div class="col-sm-6 has-validation" >
                <select class="form-select" aria-label="Interest Period select" id="interest_period" name="interest_period">
                    <option value="YEARLY">YEARLY</option>
                    <option value="MONTHLY">MONTHLY</option>
                </select>
            </div>
        </div>
        <div class="mb-3 row justify-content-center align-items-center">
            <div class="collapse col-auto col-offset-3 row alert alert-danger p-2 m-2" id="invalid" style="width: fit-content;"  role="alert">
                <div id="invalid-message"></div>
            </div>
        </div>
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>