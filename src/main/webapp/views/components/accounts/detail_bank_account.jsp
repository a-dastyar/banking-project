<%@ include file="/views/components/imports.jsp" %>
<dl class="row col">
    <dt class="col-sm-3">Account number</dt>
    <dd class="col-sm-7">${account.getAccountNumber()}</dd>
    <dt class="col-sm-3">Owner</dt>
    <dd class="col-sm-7">${account.getAccountHolder().getUsername()}</dd>
    <dt class="col-sm-3">Current balance</dt>
    <dd class="col-sm-7">${account.getBalance()}</dd>
</dl>
<%@ include file="/views/components/accounts/list_transactions.jsp" %>