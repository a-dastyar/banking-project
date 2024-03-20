<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75">
    <form method="POST" action="${pageContext.request.contextPath}/saving-accounts">
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="account_number">Account number</label>
            <div class="col-sm-6" >
                <input  type="text" class="form-control" id="account_number" name="account_number">
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="username" >Holder username</label>
            <div class="col-sm-6" >
                <input type="text" class="form-control" id="username" name="username">
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="balance">Balance</label>
            <div class="col-sm-6" >
                <input type="number" class="form-control" id="balance" name="balance">
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="minimum_balance">Minimum balance</label>
            <div class="col-sm-6" >
                <input type="number" class="form-control" id="minimum_balance" name="minimum_balance">
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="interest_rate">Interest rate</label>
            <div class="col-sm-6" >
                <input type="number" class="form-control" id="interest_rate" name="interest_rate">
            </div>
        </div>
        <div class="mb-3 row">
            <label class="col-form-label col-sm-3" for="interest_period">Interest period</label>
            <div class="col-sm-6" >
                <select class="form-select" aria-label="Interest Period select" id="interest_period" name="interest_period">
                    <option value="YEARLY">YEARLY</option>
                    <option value="MONTHLY">MONTHLY</option>
                </select>
            </div>
        </div>
        
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>