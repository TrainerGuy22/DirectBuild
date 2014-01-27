<!DOCTYPE html>
<html>
<head>
    <title>SimpleCI</title>
    <link rel="stylesheet" href="/css/core.css">
    <link href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="matchStrap">

<nav class="navbar navbar-default navbar-fixed-top navbar-inverse" role="navigation">
    <div class="navbar-header"><a class="navbar-brand" href="#">SimpleCI</a></div>
    <ul class="nav navbar-nav" id="navigate">
        <li><a href="/">Home</a></li>
        <li><a href="/jobs">Jobs</a></li>
    </ul>
    <ul class="nav navbar-nav pull-right" id="navigate-right">
        <li class="active"><a href="#">Login</a></li>
    </ul>
</nav>
<div class="centered jumbotron">
    <div class="container">
        <br/>

        <h1>SimpleCI</h1>
    </div>
</div>
<table class="centered table table-bordered" border="1">
    <%
    import com.directmyfile.ci.jobs.Job

    ci.jobs.values().each { job ->
    println("<tr><td><a href=\"job/${job.name}\">${job.name}</a></td></tr>")
    }
    %>
</table>

<script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js"></script>
</body>
</html>