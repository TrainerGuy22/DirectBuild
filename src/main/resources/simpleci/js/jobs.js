var jobList = $("#jobList");

ci.jobs(function (jobs) {
    jobs.forEach(function (job) {
        jobList.append('<tr id="job-' + job.name + '"><td>' + '<a href="' + "/job/" + job.name + '">' + job.name + '</a></td></tr>');
        $("#job-" + job.name).append("<td>" + ci.parseStatus(job.status) + "</td>").addClass(ci.getStatusClass(job.status));
    });
});