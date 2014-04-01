var jobName = window.location.pathname.replace("/job/", "");

document.title = jobName + " - SimpleCI";
$(".job-name").prepend(jobName);

ci.jobStatus(jobName, function (status) {
    $(".job-status").append(ci.parseStatus(status));
});

$(document).ready(function () {
    // Provide class='active' switching on the tabs.
    $(".nav-tabs li").each(function (index, tab) {
        tab = $(tab);
        $(tab).click(function () {
            $(".nav-tabs li").each(function (i, t) {
                t = $(t);
                t.removeClass("active");
                $("#" + t.attr("data-target")).hide();
            });
            tab.addClass("active");
            var target = tab.attr("data-target");
            $("#" + target).show();
        });
    });

    $.getJSON("/api/history/" + jobName, function (jobHistory) {
        var $history = $("#job-history");
        var $artifacts = $("#artifacts-list");

        var latest = 0;

        var lastBuild = null;

        jobHistory.forEach(function (entry) {
            var status = entry["status"];
            var number = entry["number"];
            if (number > latest) {
                latest = number;
                lastBuild = entry;
            }
            var timestamp = entry["when"];
            var log = atob(entry["log"]);
            var buildTime = entry["buildTime"];

            $history.append("<p class=\"list-group-item\">"
                + number.toString().bold() + ": "
                + ci.parseStatus(status)
                + " - "
                + timestamp
                + "</p>");
        });

        lastBuild["artifacts"].forEach(function (artifact) {
            var name = artifact["name"];
            $artifacts.append('<p class="list-group-item">' +
                '<a href="' +
                ci.artifactUrl(jobName, latest, name) +
                '">' +
                name +
                '<a/>' +
                '</p>');
        });
    });
});