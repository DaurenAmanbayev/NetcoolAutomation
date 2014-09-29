ReaderInterface = function () {
    return{
        init: function () {
            ReaderInterface.loadConnections();


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
                            readerConnection = data.payLoad.connectionName;

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
                    }
                }
            });
        }
    };
}();

ReaderInterface.init();