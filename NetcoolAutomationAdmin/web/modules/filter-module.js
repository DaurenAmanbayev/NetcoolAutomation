FilterIinterface = function () {
    return{
        init: function () {
            FilterIinterface.loadFilter();
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
                    }
                }
            });
        },
        getReader: function (readerName) {
            var data = jQuery.parseJSON($.ajax({
                type: "get",
                dataType: "json",
                async: false,
                url: "webresources/restapi/reader/byfilter//" + readerName,
                success: function (response) {
                    if (response.success) {


                    }
                }
            }).responseText);
            return data.payLoad;
        }
    };
}();
FilterIinterface.init();