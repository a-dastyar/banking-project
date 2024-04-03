
<div class="d-flex flex-column justify-content-center align-items-center ${error==null?'min-vh-100':'mt-5'}">
    <div class="container-sm shadow-lg p-3 mb-5 bg-body-tertiary rounded" style="max-width: 400px;">
        <h1 class="mr-3 pr-3 align-top border-right inline-block align-content-center">${code}</h1>
        <div class="inline-block align-middle">
            <h2 class="font-weight-normal lead" id="desc">${message}</h2>
            <a href="${pageContext.request.contextPath}" class="link-primary link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover">Home</a>
        </div>
    </div>
</div>