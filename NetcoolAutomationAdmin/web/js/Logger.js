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

