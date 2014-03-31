var jobName = window.location.pathname.replace("/job/", "");

document.title = jobName + " - SimpleCI";
$(".job-name").html(jobName);

$(document).ready(function () {
    // Provide class='active' switching on the tabs.
    $(".nav-tabs li").each(function (index, tab) {
        tab = $(tab);
        console.log("Applying Active Switch");
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

    $.getJSON("/api/history/" + jobName, function (history) {
        var $history = $("#job-history");

        history.forEach(function (entry) {
            var status = entry["status"];
            var number = entry["number"].toString();
            var timestamp = entry["timeStamp"];
            var log = atob(entry["log"]);
            var buildTime = entry["buildTime"];

            $history.append("<p class=\"list-group-item\">"
                + number.bold() + ": "
                + ci.parseStatus(status)
                + " - "
                + timestamp
                + "</p>");
        });
    });
});