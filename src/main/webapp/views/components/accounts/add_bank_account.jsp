<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75">
    <form method="POST" action="${pageContext.request.contextPath}/bank-accounts">
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
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>