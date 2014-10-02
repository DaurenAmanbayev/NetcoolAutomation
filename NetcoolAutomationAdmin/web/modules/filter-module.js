FilterIinterface = function () {
    return{
        init: function () {
            FilterIinterface.loadFilter();
            FilterIinterface.populateCmbReaders();
            FilterIinterface.saveFilter();
        },
        populateCmbReaders: function () {
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
                            $("#reader-cmb").append($("<option></option>")
                                    .attr("value", reader.readerName)
                                    .text(reader.readerName));
                        }
                    }
                }
            });
        },
        loadFilter: function () {
            logger.debug("Loading Filter Lists");
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/filter/list",
                success: function (response) {
                    if (response.success) {
                        var html = '';
                        logger.debug("Reader Lists Size is: " + response.payLoad.length);
                        var html = '';
                        for (x in response.payLoad) {
                            var filter = response.payLoad[x];
                            var filterName = filter.filterName;
                            var filterSql = filter.filterSql;
                            var readerName = FilterIinterface.getReader(filterName).readerName;
                            var filterEnabled = filter.enabled;
                            var enabledFlag = "";
                            if (filterEnabled == "Y") {
                                enabledFlag = '<button type="button" class="btn btn-success btn-circle"><i class="fa fa-check"></i></button>';
                            } else {
                                enabledFlag = '<button type="button" class="btn btn-danger btn-circle "><i class="fa fa-times">';
                            }
                            var counter = x;
                            counter++;
                            html += '<tr>';
                            html += ' <td>' + counter + '</td>';
                            html += ' <td>' + filterName + '</td>';
                            html += ' <td>' + readerName + '</td>';
                            html += ' <td>' + filterSql + '</td>';
                            html += ' <td>' + enabledFlag + '</td>';
                            html += ' <td> <button type="button" class="btn btn-primary edit-connection" data-toggle="modal" data-target="#connection-detail" data-filter-name= "' + filterName + '">Edit</button></td>';
                            html += '</tr>';
                        }
                        $('#filter-table tbody').html(html);
                        FilterIinterface.clickEditButton();
                    }
                }
            });
        },
        getReader: function (readerName) {
            var data = jQuery.parseJSON($.ajax({
                type: "get",
                dataType: "json",
                async: false,
                url: "webresources/restapi/reader/byfilter/" + readerName,
                success: function (response) {
                    if (response.success) {


                    }
                }
            }).responseText);
            return data.payLoad;
        },
        clickEditButton: function () {
            $(".edit-connection").click(function () {
                logger.debug("Edit Connection Name: " + $(this).data('reader-name'));
                var filterName = $(this).data('filter-name');
                //Popula o form
                //webresources/restapi/connection/DEFAULT_OMNIBUS
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "webresources/restapi/filter/" + filterName,
                    success: function (response) {
                        if (response.success) {
                            logger.debug("Got Data ok xD");
                            var filter = response.payLoad;
                            $("#filter-name").val(filter.filterName);

                            $("#filter-sql").val(filter.filterSql);

                            var readerName = FilterIinterface.getReader(filterName).readerName;

                            //$("#connection-enabled").val(connection.enabled);
                            if (filter.enabled == "Y" || filter.enabled == "y") {
                                $("#filter-enabled").prop('checked', true);
                                $("#filter-enabled").prop('value', "Y");
                            } else {
                                $("#filter-enabled").prop('checked', false);
                                $("#filter-enabled").prop('value', "N");
                            }
                            $("#reader-cmb").val(readerName);

                        }
                    }});
            });
        },
        saveFilter: function () {
            $("#save-filter-data").click(function () {
                logger.debug("Saving Reader Data..." + $("#filter-name").val());
                var filterName = $("#filter-name").val();
                var readerName = $("#reader-cmb").val();
                var filterSql = $("#filter-sql").val();

                var readerEnabled = "N";
                if ($("#filter-enabled").prop('checked')) {
                    readerEnabled = "Y";
                }

                $.ajax({
                    type: "post",
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType: "json",
                    //webresources/restapi/reader/ALL_EVENTS
                    url: "webresources/restapi/filter/" + filterName + "/update",
                    data: {
                        readerName: readerName,
                        fiterSql: filterSql,                       
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

                logger.debug("Saving:::  " + readerName + " " + readerEnabled);
            });
        }
    };
}();
FilterIinterface.init();