PluginsInterface = function () {
    return{
        init: function () {
            logger.debug("Starting plugin interface...");
            PluginsInterface.loadPluginListTable();

            PluginsInterface.addlisteners();
        },
        addlisteners: function () {
            $("#add-plugin-btn").click(function () {
                $('#plugin-name').removeAttr('disabled');
                $("#plugin-name").val("");
                $("#plugin-class").val("");

                $("#plugin-enabled").prop('checked', false);
                $("#plugin-enabled").prop('value', "N");
                logger.debug("Done...");
            });
        },
        loadPluginListTable: function () {
            logger.debug("Loading Plugin list");
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/plugins/list",
                success: function (response) {
                    if (response.success) {
                        var html = '';
                        logger.debug("Reader Lists Size is: " + response.payLoad.length);
                        var html = '';
                        for (x in response.payLoad) {
                            var plugin = response.payLoad[x];
                            var pluginName = plugin.pluginName;
                            var pluginDescription = "";
                            var pluginClass = plugin.pluginClass;
                            var filterEnabled = plugin.enabled;

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
                            html += ' <td>' + pluginName + '</td>';
                            html += ' <td>' + pluginDescription + '</td>';
                            html += ' <td>' + pluginClass + '</td>';
                            html += ' <td>' + enabledFlag + '</td>';
                            html += ' <td> <button type="button" class="btn btn-primary edit-connection" data-toggle="modal" data-target="#connection-detail" data-plugin-name= "' + pluginName + '">Edit</button></td>';
                            html += '</tr>';
                        }
                        $('#plugin-table tbody').html(html);
                        PluginsInterface.clickEditBtn();
                    }
                }
            });
        },
        clickEditBtn: function () {
            $(".edit-connection").click(function () {
                var pluginName = $(this).data('plugin-name');
                logger.debug("Edit Plugin name: " + pluginName);
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "webresources/restapi/plugins/" + pluginName,
                    success: function (response) {
                        if (response.success) {
                            // pega os dados do plugin xD
                            var plugin = response.payLoad;
                            $("#plugin-class").val(plugin.pluginClass);
                            $("#plugin-name").val(pluginName);
                            if (plugin.enabled == "Y" || plugin.enabled == "y") {
                                $("#plugin-enabled").prop('checked', true);
                                $("#plugin-enabled").prop('value', "Y");
                            } else {
                                $("#plugin-enabled").prop('checked', false);
                                $("#plugin-enabled").prop('value', "N");
                            }
                        }
                    }});
            });
        }

    };
}();

PluginsInterface.init();