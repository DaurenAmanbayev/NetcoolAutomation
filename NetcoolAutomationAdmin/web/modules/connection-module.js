ConnectionInterface = function () {
    return{
        init: function () {
            ConnectionInterface.loadConnections();
            ConnectionInterface.refreshOnClose();
            ConnectionInterface.saveConnection();

        },
        loadConnections: function () {
            logger.debug("Reading connection list");
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/connection/list",
                success: function (response) {
                    if (response.success) {
                        logger.debug("Connections ok...");
                        var connections = response.payLoad;
                        var html = '';
                        for (x in connections) {

                            var connectionName = connections[x].connectionName;
                            var connectionUrl = connections[x].jdbcUrl;
                            var connectionEnabled = connections[x].enabled;
                            var connectionUsername = connections[x].username;
                            var readerCount = connections[x].automationReaderList.length;
                            var policyCount = 0;
                            for (y in connections[x].automationReaderList) {
                                var automationReaderFilterList = connections[x].automationReaderList[y].automationReaderFilterList;
                                for (z in automationReaderFilterList) {
                                    for (w in automationReaderFilterList[z].automationPoliciesList) {
                                        policyCount++;
                                    }
                                }
                            }
                            var enabledFlag = "";
                            if (connectionEnabled == "Y") {
                                enabledFlag = '<button type="button" class="btn btn-success btn-circle"><i class="fa fa-check"></i></button>';
                            } else {
                                enabledFlag = '<button type="button" class="btn btn-danger btn-circle "><i class="fa fa-times">';
                            }

                            logger.debug("Loading Connection: " + connections[x].connectionName + " Reader Count: " + readerCount + " Policy Count: " + policyCount);
                            var counter = x;
                            counter++;
                            html += '<tr>';
                            html += ' <td>' + counter + '</td>';
                            html += ' <td>' + connectionName + '</td>';
                            html += ' <td>' + connectionUrl + '</td>';
                            html += ' <td>' + connectionUsername + '</td>';
                            html += ' <td>' + enabledFlag + '</td>';
                            html += ' <td>' + readerCount + '</td>';
                          //  html += ' <td>' + policyCount + '</td>';
                            html += ' <td> <button type="button" class="btn btn-primary edit-connection" data-toggle="modal" data-target="#connection-detail" data-connection-name= "' + connectionName + '">Edit</button></td>';
                            html += '</tr>';
                        }
                        $('#connection-table tbody').html(html);
                        ConnectionInterface.clickEditButton();

                    }
                }
            });
        },
        clickEditButton: function () {
            $(".edit-connection").click(function () {
                logger.debug("Edit Connection Name: " + $(this).data('connection-name'));
                //Popula o form
                //webresources/restapi/connection/DEFAULT_OMNIBUS
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "webresources/restapi/connection/" + $(this).data('connection-name'),
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Got Data ok xD");
                            var connection = response.payLoad;
                            $("#connection-name").val(connection.connectionName);
                            $("#connection-user").val(connection.username);
                            $("#connection-pass").val(connection.password);
                            $("#connection-url").val(connection.jdbcUrl);
                            //$("#connection-enabled").val(connection.enabled);
                            if (connection.enabled == "Y" || connection.enabled == "y") {
                                $("#connection-enabled").prop('checked', true);
                                $("#connection-enabled").prop('value', "Y");
                            } else {
                                $("#connection-enabled").prop('checked', false);
                                $("#connection-enabled").prop('value', "N");
                            }

                        }
                    }});
            });
        },
        saveConnection: function () {
            $("#save-connection-data").click(function () {
                logger.debug("Saving Connection Data..." + $("#connection-user").val());
                var connectionName = $("#connection-name").val();
                var connectionUser = $("#connection-user").val();
                var connectionPass = $("#connection-pass").val();
                var connectionUrl = $("#connection-url").val();
                var connectionEnabled = "N";
                if ($("#connection-enabled").prop('checked')) {
                    connectionEnabled = "Y";
                }

                $.ajax({
                    type: "post",
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType: "json",
                    url: "webresources/restapi/connection/" + connectionName + "/update",
                    data: {
                        name: connectionName,
                        user: connectionUser,
                        pass: connectionPass,
                        url: connectionUrl,
                        enabled: connectionEnabled

                    },
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Connection Data Updated Success");

                        } else {
                            logger.error("Failed to update connection data");

                        }
                    }
                });

                logger.debug("Saving:::  " + connectionName + " " + connectionEnabled);
            });
        },
        refreshOnClose: function () {

            $('#connection-detail').on('hidden.bs.modal', function () {
                ConnectionInterface.loadConnections();
            });

        }
    };
}();

ConnectionInterface.init();