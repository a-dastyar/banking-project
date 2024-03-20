<div class="col-3 m-1 p-1 rounded" style="background-color: #65666638;">
    <div class="d-flex align-items-start">
        <div class="nav flex-column nav-pills me-3 w-100" id="v-pills-tab" role="tablist" aria-orientation="vertical">
            <c:forEach var="item" items="${panelItems}" varStatus="loop">
                <c:set var="isActive" value="${activePanel==loop.index || ( activePanel==null && loop.index == 0 )}" />
                <button class="nav-link m-1 text-body-emphasis border border-primary btn-side ${isActive?'active':''}" id="v-pills-${loop.index}-tab" data-bs-toggle="pill" data-bs-target="#v-pills-${loop.index}" type="button" role="tab" aria-controls="v-pills-${loop.index}" aria-selected="true">${item}</button>
            </c:forEach>
        </div>
    </div>
</div>