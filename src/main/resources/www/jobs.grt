<!DOCTYPE html>
<html>
<head>
    <title>Jobs | DirectBuild</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>
<body class="container">
<%= component("navigation", [active: "Jobs"]) %>

<%= component("job_table") %>
</body>
</html>