<%@ include file="/views/components/commons/imports.jsp" %>
<div class="container-sm w-75 mt-3 mb-3 p-3 border rounded">
    <form method="GET" action="${pageContext.request.contextPath}/${endpoint}" class="needs-validation" novalidate>
        <div class="row mb-3">
            <label for="sum_min" class="col-sm-auto col-form-label">Min balance:</label>
            <div class="col-sm">
                <input type="number" class="form-control" id="sum_min" name="sum_min" value="0" min="0" required>
                <div class="invalid-feedback">
                    Please enter an non-negative amount.
                </div>
            </div>
            <div class="col-sm-2">
                <button type="submit" class="btn btn-primary">Submit</button>
            </div>
        </div>
    </form>
</div>    
<c:if test="${min != null}">
    <table class="table border-dark">
        <thead>
            <tr>
                <th scope="col">Sum balance</th>
                <th scope="col">Higher than</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>${sum}</td>
                <td>${min}</td>
            </tr>
        </tbody>
    </table>
</c:if>