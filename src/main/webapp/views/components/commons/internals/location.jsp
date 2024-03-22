<c:if test="${not empty paths}">
    <div class="container-sm pl-1 mt-3 rounded" style="background: #7d828447;">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb p-1 mb-2">
                <c:set var="url" value="${pageContext.request.contextPath}" />
                <li class="breadcrumb-item"><a href="${url}">Home</a></li>
                <c:forEach var="location" items="${paths}" varStatus="loop">
                    <li class="breadcrumb-item ${loop.last?'active':''}" aria-current="page">
                        <c:if test="${loop.last}">
                            ${location}
                        </c:if>
                        <c:if test="${not loop.last}">
                            <a href="${url}/${urls[loop.index]}">
                                ${location}
                            </a>
                        </c:if>
                    </li>
                </c:forEach>
            </ol>
        </nav>
    </div>
</c:if>