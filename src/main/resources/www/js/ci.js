var ci = {
    options: {}
};

ci.options.apiEndpoint = "/api";

ci.fetch = function (path, callback) {
    var endpoint = ci.options.apiEndpoint + path;
    $.getJSON(endpoint, callback);
};

ci.getStatusClass = function (id) {
    switch (id) {
        case 0:
            return "success";
        case 1:
            return "danger";
        default:
            return "";
    }
};

ci.parseStatus = function (id) {
    switch (id) {
        case 0:
            return "Success";
        case 1:
            return "Failure";
        case 2:
            return "Not Started";
        case 3:
            return "Running";
        case 4:
            return "Waiting";
        default:
            return "Unknown";
    }
};

ci.artifactUrl = function (jobName, buildNumber, name) {
    return "/artifacts/" + jobName + "/" + buildNumber + "/" + name;
};

ci.jobs = function (callback) {
    ci.fetch("/api/jobs.json", callback);
};

ci.jobStatus = function (name, callback) {
    ci.jobs(function (jobs) {
        jobs.forEach(function (job) {
            if (job["name"] == name) {
                callback(job["status"]);
            }
        });
    });
};