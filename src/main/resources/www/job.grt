<!DOCTYPE html>
<%
import org.directcode.ci.web.WebUtil
import org.directcode.ci.jobs.JobStatus
%>

<%
def jobName = request.path - "/job/"
%>
<html>
<head>
    <title>${jobName} | ${WebUtil.brand()}</title>
    <%= component("jquery") %>
    <%= component("bootstrap") %>
</head>
<body class="container">
<%= component("navigation", [active: "Home"]) %>

<%= component('nav-tab-switch-script') %>

<ul class="nav nav-tabs" id="info-tabs">
    <li data-target="history" class="active"><a href="#">History</a></li>
    <li data-target="artifacts"><a href="#">Artifacts</a></li>
</ul>

<div id="content">
    <div id="history">
        <div id="job-history" class="list-group">
            <% def history = WebUtil.ci().getJobByName(jobName).history %>
            <% history.entries.each { entry -> %>
            <p class="list-group-item">
                ${entry.number} - ${JobStatus.parse(entry.status)} - ${entry.when}
            </p>
            <% } %>
        </div>
    </div>
    <div style="display: none;" id="artifacts">
        <div id="artifacts-list" class="list-group">

        </div>
    </div>
</div>

</body>
</html>