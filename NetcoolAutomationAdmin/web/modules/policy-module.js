PolicyListInterface = function () {
    return {
        init: function () {
            logger.debug("Starting Policy Interface");
            PolicyListInterface.loadPolicyList();
            logger.debug("loadgin cmb");

        },
        loadPolicyList: function () {
            $.ajax({
                type: "get",
                dataType: "json",
                url: "webresources/restapi/policy/list",
                success: function (response) {
                    if (response.success) {
                        logger.debug("Connections ok...");
                        var policies = response.payLoad;
                        var html = '';
                        for (x in policies) {
                            var policyName =    policies[x].policyName;
                            var filterName =    policies[x].filterName.filterName;
                            var policyEnabled = policies[x].enabled;
                            var policyOrder =   policies[x].executionOrder;
                            var enabledFlag = "";
                            if (policyEnabled == "Y") {
                                enabledFlag = '<button type="button" class="btn btn-success btn-circle"><i class="fa fa-check"></i></button>';
                            } else {
                                enabledFlag = '<button type="button" class="btn btn-danger btn-circle "><i class="fa fa-times">';
                            }
                            var counter = x;
                            counter++;
                            html += '<tr>';
                            html += ' <td>' + counter + '</td>';
                            html += ' <td>' + policyName + '</td>';
                            html += ' <td>' + filterName + '</td>';
                            html += ' <td>' + enabledFlag + '</td>';
                            html += ' <td>' + policyOrder + '</td>';
                            html += ' <td> <button type="button" class="btn btn-primary edit-connection" data-toggle="modal" data-target="#connection-detail" data-policy-name= "' + policyName + '">Edit</button></td>';
                            html += '</tr>';
                        }
                        $('#policy-table tbody').html(html);
                        $(".edit-connection").click(function () {
                            logger.debug("Edit Policy Name: " + $(this).data('policy-name'));
                            window.location.href = "policy-editor.jsp?name=" + $(this).data('policy-name');
                        });
                    }
                }
            });

        }
    };
}();
PolicyListInterface.init();