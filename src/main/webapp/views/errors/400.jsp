<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/views/components/commons/meta.jsp" %> 
    <title>400 | Bad Request</title>
</head>
<body>
    <div class="d-flex flex-column min-vh-100 justify-content-center align-items-center">
        <div class="container-sm shadow-lg p-3 mb-5 bg-body-tertiary rounded" style="max-width: 400px;">
            <h1 class="mr-3 pr-3 align-top border-right inline-block align-content-center">400</h1>
            <div class="inline-block align-middle">
                <h2 class="font-weight-normal lead" id="desc">Invalid request.</h2>
                <a href="${pageContext.request.contextPath}" class="link-primary link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover">Home</a>
            </div>
        </div>
    </div>
</body>
</html>