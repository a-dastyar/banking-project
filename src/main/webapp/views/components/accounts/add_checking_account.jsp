<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75">
    <form method="POST" action="${pageContext.request.contextPath}/checking-accounts" class="needs-validation" novalidate>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="account_number" >Account number</label>
            <div class="col-sm-6 has-validation" >
                <input type="text" class="form-control" id="account_number" name="account_number" required>
                <div class="invalid-feedback">
                    Please enter an account number.
                </div>
            </div>
        </div>
        <div class="mb-3 row ">
            <label class="col-form-label col-sm-3" for="username" >Holder username</label>
            <div class="col-sm-6 has-validation" >
                <input type="text" class="form-control" id="username" name="username" required>
                <div class="invalid-feedback">
                    Please choose a username.
                </div>
            </div>      
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="balance">Balance</label>    
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="balance" name="balance" min="0" required>
                <div class="invalid-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="overdraft_limit">Overdraft limit</label>
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="overdraft_limit" name="overdraft_limit" min="0" value="0" required>
                <div class="invalid-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="debt">Debt</label>
            <div class="col-sm-6 has-validation" >
                <input type="number" class="form-control" id="debt" name="debt" min="0" value="0" required>
                <div class="invalid-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
        </div>
        
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>