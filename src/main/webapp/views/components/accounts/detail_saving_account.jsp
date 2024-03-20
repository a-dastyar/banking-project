<%@ include file="/views/components/commons/imports.jsp" %>
<dl class="row">
    <dt class="col-sm-3">Account number</dt>
    <dd class="col-sm-9">${account.getAccountNumber()}</dd>
    <dt class="col-sm-3">Owner</dt>
    <dd class="col-sm-9">${account.getAccountHolder().getUsername()}</dd>
    <dt class="col-sm-3">Current balance</dt>
    <dd class="col-sm-9">${account.getBalance()}</dd>
    <dt class="col-sm-3">Minimum balance</dt>
    <dd class="col-sm-9">${account.getMinimumBalance()}</dd>
    <dt class="col-sm-3">Interest rate</dt>
    <dd class="col-sm-9">${account.getInterestRate()}</dd>
    <dt class="col-sm-3">Interest period</dt>
    <dd class="col-sm-9">${account.getInterestPeriod()}</dd>
</dl>
<%@ include file="/views/components/accounts/list_transactions.jsp" %>