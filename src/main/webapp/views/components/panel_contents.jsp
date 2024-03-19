<div class="col m-1 p-1 rounded mh-100 mw-100 overflow-y-auto" style="background-color: #65666638;">
    <div class="tab-content w-100 mh-100 " id="v-pills-tabContent">
        <c:forEach var="pageURI" items="${panelContents}" varStatus="loop">
                <c:set var="isActive" value="${activePanel==loop.index || ( activePanel==null && loop.index == 0 )}" />
                <div class="container tab-pane rounded fade ${isActive?'active show':''}" id="v-pills-${loop.index}" role="tabpanel"
                    aria-labelledby="v-pills-${loop.index}-tab" tabindex="${loop.index}">
                    <jsp:include page="${pageURI}" />
                </div>
        </c:forEach>
    </div>
</div>