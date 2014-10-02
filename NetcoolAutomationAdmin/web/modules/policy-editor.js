PolicyEditor = function () {
    var editor = null;
    return{
        init: function () {
            PolicyEditor.populateFilterCmb();
            PolicyEditor.initEditor();
            PolicyEditor.loadPolicy();

            PolicyEditor.saveConnectionData();

        },
        saveConnectionData: function () {

            $("#save-policy-data").click(function () {
                logger.debug("Saving...");
                PolicyEditor.saveScript();
                var filterName = $("#filter-cmb").val();
                var policyEnabled = "N";
                if ($("#policy-enabled").prop('checked')) {
                    policyEnabled = "Y";
                }

                $.ajax({
                    type: "post",
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType: "json",
                    //webresources/restapi/reader/ALL_EVENTS
                    url: "webresources/restapi/policy/" + policyName + "/reader/update",
                    data: {
                        filter: filterName,
                        enabled: policyEnabled

                    },
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Connection Data Updated Success");

                        } else {
                            logger.error("Failed to update connection data");

                        }
                    }
                });


            });
        },
        populateFilterCmb: function () {
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/filter/list",
                success: function (response) {
                    if (response.success) {

                        logger.debug("Reader Lists Size is: " + response.payLoad.length);
                        for (x in response.payLoad) {
                            var filter = response.payLoad[x];
                            $("#filter-cmb").append($("<option></option>")
                                    .attr("value", filter.filterName)
                                    .text(filter.filterName));
                        }
                    }
                }
            });
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


                        //$("#connection-enabled").val(connection.enabled);
                        if (policy.enabled == "Y" || policy.enabled == "y") {
                            $("#policy-enabled").prop('checked', true);
                            $("#policy-enabled").prop('value', "Y");
                        } else {
                            $("#policy-enabled").prop('checked', false);
                            $("#policy-enabled").prop('value', "N");
                        }
                        $("#filter-cmb").val(policy.filterName.filterName);

                    }
                }
            });
        },
        saveScript: function () {
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
        }
    };
}();
PolicyEditor.init();