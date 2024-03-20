<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75 mt-3 p-3 border rounded">
    <form method="POST" action="${pageContext.request.contextPath}/checking-accounts" class="needs-validation" id="add-checking-account-form" novalidate>
        <%@ include file="/views/components/accounts/common_form_fields.jsp" %>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="overdraft_limit">Overdraft limit</label>
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="overdraft_limit" name="overdraft_limit" min="0" value="0" required>
                <div class="invalid-feedback" id="overdraft_limit-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="debt">Debt</label>
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="debt" name="debt" min="0" value="0" required>
                <div class="invalid-feedback" id="debt-feedback">
                    Please enter an non-negative amount.
                </div>
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