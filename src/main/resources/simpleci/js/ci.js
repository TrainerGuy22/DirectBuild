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

ci.jobs = function (callback) {
    ci.fetch("/jobs", callback);
};