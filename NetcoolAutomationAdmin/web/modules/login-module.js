LoginInterface = function () {


    return{
        init: function () {
            logger.debug("Starting Login Interface");
            LoginInterface.bindActions();
        },
        bindActions: function () {
            $("#login-btn").click(function () {
                //do login
                LoginInterface.submitLogin();    
            });
            
            $("#login-form").submit(function(){
                LoginInterface.submitLogin();    
            });

        },
        submitLogin: function () {
            $.ajax({
                type: "post",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                dataType: "json",
                url: "webresources/restapi/user/login",
                data: {
                    user: $("#user").val(),
                    password: $("#pass").val()
                },
                success: function (response) {
                    if (response.success) {
                        logger.debug("Login ok xD");
                        window.location.href = "index.jsp";
                    }   
                }
            });
        }
    };
}();

LoginInterface.init();