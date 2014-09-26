ConnectionInterface = function () {
    return{
        init: function () {
            ConnectionInterface.loadConnections()
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
                            var counter= x;
                            counter++;
                            html += '<tr>';
                            html += ' <td>' + counter + '</td>';
                            html += ' <td>' + connectionName + '</td>';
                            html += ' <td>' + connectionUrl + '</td>';
                            html += ' <td>' + connectionUsername + '</td>';
                            html += ' <td>' + enabledFlag + '</td>';
                            html += ' <td>' + readerCount + '</td>';
                            html += ' <td>' + policyCount + '</td>';
                            html += ' <td> <button type="button" class="btn btn-primary">Edit</button></td>';
                            html += '</tr>';
                        }
                        $('#connection-table tbody').html(html);
                    }
                }
            });
        }
    };
}();

ConnectionInterface.init();