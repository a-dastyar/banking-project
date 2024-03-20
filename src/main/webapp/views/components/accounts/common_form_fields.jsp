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
            Please enter a username.
        </div>
    </div>      
</div>
<div class="mb-3 row">
    <label class="col-form-label col-sm-3" for="balance">Balance</label>    
    <div class="col-sm-6 has-validation" >
        <input type="number" class="form-control" id="balance" name="balance" min="0" required>
        <div class="invalid-feedback" id="balance-feedback">
            Please enter an non-negative amount.
        </div>
    </div>
</div>