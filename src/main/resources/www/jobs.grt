<!DOCTYPE html>
<% import org.directcode.ci.web.WebUtil %>
<html>
<head>
    <title>Jobs | ${WebUtil.brand()}</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>
<body class="container">
<%= component("navigation", [active: "Jobs"]) %>

<%= component("job_table") %>
</body>
</html>