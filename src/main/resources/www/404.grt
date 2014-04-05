<!DOCTYPE html>
<html>

<head>
    <title>404: Not Found</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>

<body class="container" style="text-align: center;">
<div class="jumbotron">
    <h1>Not Found</h1>

    <p>We could not find ${request.path} on this server.</p>

    <p><a href="/" class="btn btn-primary btn-lg" role="button">DirectBuild Home</a></p>
</div>
</body>

</html>