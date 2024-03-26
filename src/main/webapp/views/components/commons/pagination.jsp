<%@ include file="/views/components/commons/imports.jsp" %>
<nav class="d-flex justify-content-center" aria-label="Page navigation pagination">
    <c:if test="${page.total()>page.size()}">
        <c:set var="start" value="${Math.max(page.page()-3,1)}"/>
        <c:set var="last" value="${Math.ceilDiv(page.total(),page.size())}"/>
        <c:set var="end" value="${Math.min(page.page()+3, last)}"/>
        <c:set var="prev" value="${Math.max(page.page()-1,start)}"/>
        <c:set var="next" value="${Math.min(page.page()+1,end)}"/>
        <ul class="pagination">
            <li class="page-item ${prev==page.page()?'disabled':''}"">
            <a class="page-link" href="${pageContext.request.contextPath}/${endpoint}?page=1&size=${page.size()}${typeParam}${additionalParam}" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
            </li>
            <c:forEach begin="${start}" end="${end}" varStatus="loop">
                <li class="page-item ${page.page()==loop.index?'active':''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/${endpoint}?page=${loop.index}&size=${page.size()}${typeParam}${additionalParam}" >${loop.index}</a>
                </li>
            </c:forEach>
            <li class="page-item ${next==page.page()?'disabled':''}"">
            <a class="page-link" href="${pageContext.request.contextPath}/${endpoint}?page=${last}&size=${page.size()}${typeParam}${additionalParam}" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
            </a>
            </li>
        </ul>
    </c:if>
</nav>