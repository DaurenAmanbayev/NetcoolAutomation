ReaderInterface = function () {
    return{
        init: function () {
            ReaderInterface.loadConnections();
            ReaderInterface.populateCmbConnections();
            ReaderInterface.saveConnection();
            ReaderInterface.refreshOnClose();

        },
        populateCmbConnections: function () { 
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/connection/list",
                success: function (response) {
                    if (response.success) {
                        var html = '';
                        logger.debug("Reader Lists Size is: " + response.payLoad.length);
                        for (x in response.payLoad) {
                            var connection = response.payLoad[x];
                            $("#connection-cmb").append($("<option></option>")
                                    .attr("value", connection.connectionName)
                                    .text(connection.connectionName));
                        }
                    }
                }
            });
        },
        loadConnections: function () {
            logger.debug("Loading Reader Lists");
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/reader/list",
                success: function (response) {
                    if (response.success) {
                        var html = '';
                        logger.debug("Reader Lists Size is: " + response.payLoad.length);
                        for (x in response.payLoad) {
                            var reader = response.payLoad[x];
                            var readerName = reader.readerName;
                            var readerCron = reader.cronInterval;
                            var readerConnection = reader.cronInterval;
                            var readerEnabled = reader.enabled;
                            var filterCount = reader.automationReaderFilterList.length;


                            readerConnection = ReaderInterface.getReaderConnection(readerName).connectionName;

                            var enabledFlag = "";
                            if (readerEnabled == "Y") {
                                enabledFlag = '<button type="button" class="btn btn-success btn-circle"><i class="fa fa-check"></i></button>';
                            } else {
                                enabledFlag = '<button type="button" class="btn btn-danger btn-circle "><i class="fa fa-times">';
                            }
                            var counter = x;
                            counter++;
                            html += '<tr>';
                            html += ' <td>' + counter + '</td>';
                            html += ' <td>' + readerName + '</td>';
                            html += ' <td>' + readerConnection + '</td>';
                            html += ' <td>' + readerCron + '</td>';
                            html += ' <td>' + enabledFlag + '</td>';
                            html += ' <td>' + filterCount + '</td>';
                            html += ' <td> <button type="button" class="btn btn-primary edit-connection" data-toggle="modal" data-target="#connection-detail" data-reader-name= "' + readerName + '">Edit</button></td>';
                            html += '</tr>';
                        }
                        $('#reader-table tbody').html(html);
                        ReaderInterface.clickEditButton();
                    }
                }
            });
        },
        clickEditButton: function () {
            $(".edit-connection").click(function () {
                logger.debug("Edit Connection Name: " + $(this).data('reader-name'));
                var readerName = $(this).data('reader-name');
                //Popula o form
                //webresources/restapi/connection/DEFAULT_OMNIBUS
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "webresources/restapi/reader/ALL_EVENTS",
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Got Data ok xD");
                            var reader = response.payLoad;
                            $("#reader-name").val(reader.readerName);

                            $("#reader-cron").val(reader.cronInterval);

                            var connectionName = ReaderInterface.getReaderConnection(readerName).connectionName

                            //$("#connection-enabled").val(connection.enabled);
                            if (reader.enabled == "Y" || reader.enabled == "y") {
                                $("#reader-enabled").prop('checked', true);
                                $("#reader-enabled").prop('value', "Y");
                            } else {
                                $("#reader-enabled").prop('checked', false);
                                $("#reader-enabled").prop('value', "N");
                            }
                            $("#connection-cmb").val(connectionName);

                        }
                    }});
            });
        },
        getReaderConnection: function (readerName) {
            var data = jQuery.parseJSON($.ajax({
                type: "get",
                dataType: "json",
                async: false,
                url: "webresources/restapi/connection/byreader/" + readerName,
                success: function (response) {
                    if (response.success) {
                        var connection = response.payLoad;
                        //readerConnection = connection.connectionName;

                    }
                }
            }).responseText);
            return data.payLoad;
        }, 
        saveConnection: function () {
            $("#save-reader-data").click(function () {
                logger.debug("Saving Reader Data..." + $("#reader-name").val());
                var readerName = $("#reader-name").val();
                var connectionName = $("#connection-cmb").val();
                var cronString = $("#reader-cron").val();

                var readerEnabled = "N";
                if ($("#reader-enabled").prop('checked')) {
                    readerEnabled = "Y";
                }

                $.ajax({
                    type: "post",
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType: "json",
                    //webresources/restapi/reader/ALL_EVENTS
                    url: "webresources/restapi/reader/" + readerName + "/update",
                    data: {
                        readerName: readerName,
                        connectionName: connectionName,
                        cronString: cronString,
                        enabled: readerEnabled

                    },
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Connection Data Updated Success");

                        } else {
                            logger.error("Failed to update connection data");

                        }
                    }
                });

                logger.debug("Saving:::  " + connectionName + " " + readerEnabled);
            });
        },
        refreshOnClose: function () {

            $('#connection-detail').on('hidden.bs.modal', function () {
                ReaderInterface.loadConnections();
            });

        }
    };
}();

ReaderInterface.init();