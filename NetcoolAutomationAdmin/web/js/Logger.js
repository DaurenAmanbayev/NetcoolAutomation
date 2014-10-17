logger = function() {
    return{
        debug: function(msg) {
            console.debug(logger.getDate() + " - " + msg);
        },
        info: function(msg) {
            console.info(logger.getDate() + " - " + msg);
        },
        error: function(msg) {
            console.error(logger.getDate() + " - " + msg);
        },
        getDate: function() {
            var now = new Date();
            return now.format("dd/mm/yyyy HH:MM:ss.L");
        }
    };
}();

var loc = window.location, new_uri;
if (loc.protocol === "https:") {
    new_uri = "wss:";
} else {
    new_uri = "ws:";
}
new_uri += "//" + loc.host;
new_uri += "/NetcoolAutomationAdmin/loggerSocket";
var webLogger = new WebSocket(new_uri);

function logMsg(msg){
    console.debug(msg.data);
}
webLogger.onmessage = logMsg;

