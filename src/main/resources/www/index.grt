<!DOCTYPE html>
<html>
<head>
    <title>Home | DirectBuild</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>
<body>
<%= component("navigation", [active: "Home"]) %>

<%= component("job_table") %>
</body>
</html>