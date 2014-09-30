<%@include file="includes/auth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <%@include file="includes/default-header.jsp" %>
    <style type="text/css" media="screen">
        #editor {      
            height: 350px;
        }
    </style>
    <body>
        <script>
            var policyName = "<%=request.getParameter("name")%>";
        </script>
        <div id="wrapper">

            <!-- Navigation -->
            <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="index.html">Netcool Automation</a>
                </div>
                <!-- /.navbar-header -->
                <%@include file="includes/drop-down.jsp" %>
                <!-- /.navbar-top-links -->

                <%@include file="includes/navbar.jsp" %>
                <!-- /.navbar-static-side -->
            </nav>

            <div id="page-wrapper">
                <div class="row">
                    <div class="col-lg-12">
                        <!-- /<h1 class="page-header">Netcool Automation Policy Editor - POC</h1> -->
                        <h1 class="page-header"> </h1>
                    </div>
                    <!-- /.col-lg-12 -->
                </div>
                <!-- /.row -->
                <div class="row">
                    <div class="col-lg-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                Script Editor
                            </div>
                            <div class="panel-heading">
                                <!-- <button id="syntax-check-btn" type="button" class="btn btn-default btn-circle"><i class="fa fa-check"></i></button> -->
                                <button id="save-btn" type="button" class="btn btn-primary btn-circle"><i class="fa fa-save"></i></button>

                            </div>
                            <!-- /.panel-heading -->
                            <div class="panel-body">
                                <div class="table-responsive">
                                    <div id="editor"></div>  
                                </div>
                                <!-- /.table-responsive -->

                            </div>
                            <!-- /.panel-body -->
                        </div>
                        <!-- /.panel -->
                    </div>
                    <!-- /.col-lg-12 -->
                </div>
                <div class="row">
                    <div class="col-lg-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                Script Editor
                            </div>

                            <!-- /.panel-heading -->
                            <div class="panel-body">
                                <div class="row">
                                    <div class="col-lg-4">
                                        <div class="form-group">
                                            <label>Filter</label>
                                            <select id="filter-cmb" class="form-control">
                                               
                                            </select>
                                        </div></div>
                                    <div class="col-lg-4">

                                        <div class="form-group">
                                            <label>Status</label>
                                            <div id="policy-enable-div" class="checkbox">
                                                <label>
                                                    <input id="policy-enabled" type="checkbox" value="">Enabled
                                                </label>
                                            </div>                                        
                                        </div>

                                    </div>
                                    <div class="col-lg-4">

                                        <div class="form-group" style=" padding-top: 20px;">                                       
                                            <button id="save-policy-data" type="button" class="btn btn-primary">Save changes</button>
                                        </div>
                                    </div>
                                </div>


                            </div>
                            <!-- /.panel-body -->
                        </div>
                        <!-- /.panel -->
                    </div>
                    <!-- /.col-lg-12 -->
                </div>
                <!-- /.row -->
            </div>
            <!-- /#page-wrapper -->

        </div>
        <!-- /#wrapper -->
        <%@include file="includes/default-botton.jsp" %>

        <!-- Page-Level Demo Scripts - Tables - Use for reference -->
        <script src="plugins/ace/js-min/ace.js" type="text/javascript" charset="utf-8"></script>
        <script src="modules/policy-editor.js"></script>
    </body>

</html>
