<div class="mb-3 row offset-sm-2">
    <label class="col-form-label col-md-4" for="username" >Holder username</label>
    <div class="col-sm-6 has-validation" >
        <input type="text" class="form-control" id="username" name="username" required>
        <div class="invalid-feedback">
            Please enter a username.
        </div>
    </div>      
</div>
<div class="mb-3 row offset-sm-2">
    <label class="col-form-label col-md-4" for="balance">Balance</label>    
    <div class="col-sm-6 has-validation" >
        <input type="number" class="form-control" id="balance" name="balance" min="0" required>
        <div class="invalid-feedback" id="balance-feedback">
            Please enter an non-negative amount.
        </div>
    </div>
</div>