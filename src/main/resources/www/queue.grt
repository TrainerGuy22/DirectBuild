<!DOCTYPE html>
<% import org.directcode.ci.web.WebUtil %>
<html>
<head>
    <title>Build Queue | ${WebUtil.brand()}</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>
<body class="container">
<%= component("navigation", [active: "Home"]) %>
<%= component("build_queue") %>
</body>
</html>