<!DOCTYPE html>
<% import org.directcode.ci.web.WebUtil %>
<html>
<head>
    <title>Home | ${WebUtil.brand()}</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>
<body class="container">
<%= component("navigation", [active: "Home"]) %>

<%= component("job_table") %>
</body>
</html>