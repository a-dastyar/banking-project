<%@ include file="/views/components/commons/imports.jsp" %>
<dl class="row">
    <dt class="col-sm-3">Account number</dt>
    <dd class="col-sm-9">${account.getAccountNumber()}</dd>
    <dt class="col-sm-3">Owner</dt>
    <dd class="col-sm-9">${account.getAccountHolder().getUsername()}</dd>
    <dt class="col-sm-3">Current balance</dt>
    <dd class="col-sm-9">${account.getBalance()}</dd>
    <dt class="col-sm-3">Overdraft limit</dt>
    <dd class="col-sm-9">${account.getOverdraftLimit()}</dd>
    <dt class="col-sm-3">Debt</dt>
    <dd class="col-sm-9">${account.getDebt()}</dd>
</dl>
<%@ include file="/views/components/accounts/list_transactions.jsp" %>