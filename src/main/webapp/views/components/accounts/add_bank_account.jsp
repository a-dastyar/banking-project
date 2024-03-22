<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75 mt-3 p-3 border rounded  justify-content-center align-items-center">
    <form method="POST" action="${pageContext.request.contextPath}/bank-accounts"  class="needs-validation" id="add-bank-account-form" novalidate>
        <%@ include file="/views/components/accounts/common_form_fields.jsp" %>
        <div class="row justify-content-center align-items-center">
            <button type="submit" class="btn btn-primary col-sm-2 mt-2">Submit</button>
        </div>
    </form>
</div>