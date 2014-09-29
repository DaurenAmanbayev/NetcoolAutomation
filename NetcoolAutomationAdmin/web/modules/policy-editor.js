PolicyEditor = function () {
    var editor = null;
    return{
        init: function () {
            PolicyEditor.initEditor();
            PolicyEditor.loadPolicy();
            PolicyEditor.saveScript();
        },
        initEditor: function () {
            editor = ace.edit("editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/groovy");
        },
        loadPolicy: function () {
            logger.debug("Loading Policy: " + policyName);
            ///POWER_POLICY
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/poilicy/" + policyName,
                success: function (response) {
                    if (response.success) {
                        logger.debug("Connections ok...");
                        var policy = response.payLoad;
                        editor.setValue(policy.script);
                    }
                }
            });
        },
        saveScript: function () {
            $("#save-btn").click(function () {
                logger.debug("Saving Script " + policyName);
                $.ajax({
                    type: "post",
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType: "json",
                    //webresources/restapi/reader/ALL_EVENTS
                    url: "webresources/restapi/poilicy/" + policyName + "/update",
                    data: {
                        script: editor.getValue()

                    },
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Connection Data Updated Success");
                        }
                    }
                });
            });
        }
    };
}();
PolicyEditor.init();