<%@ include file="/views/components/imports.jsp" %>
<div class="m-3">
    <form method="GET" action="${pageContext.request.contextPath}/${endPoint}">
        <div class="row mb-3">
            <label for="sum_min" class="col-sm-2 col-form-label">Min balance:</label>
            <div class="col-sm">
                <input type="text" class="form-control" id="sum_min" name="sum_min">
            </div>
            <div class="col">
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