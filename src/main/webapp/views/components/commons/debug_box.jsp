<c:if test="${error!=null}">
    <div class="d-flex flex-column justify-content-center align-items-center">
        <div class="container-sm shadow-lg p-3 mb-5 bg-body-tertiary rounded" style="max-width: 800px;">
            <h4 class="mr-3 pr-3 align-top border-right inline-block align-content-center fs-5 mb-2 p-2" style="background-color: #a3a7ae54;">Debug mode enabled [to hide the debug box disable debug mode]</h4>
            <div class="inline-block align-middle overflow-y-scroll">
                <c:set var="newLine" value="\n"/>
                <pre class="font-weight-normal lead fs-6" id="desc">${error}</pre>
            </div>
        </div>
    </div>
</c:if>