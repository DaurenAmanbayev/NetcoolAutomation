<%@include file="includes/auth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <%@include file="includes/default-header.jsp" %>
    <style type="text/css" media="screen">
        #editor {      
            height: 400px;
        }
    </style>
    <body>

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
                                Connection List
                            </div>
                            <!-- /.panel-heading -->
                            <div class="panel-body">
                                <div class="table-responsive">
                                    <table id="policy-table" class="table table-striped table-bordered table-hover">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Policy Name</th>
                                                <th>Filter</th>
                                                <th>Status</th>                                               
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>

                                        </tbody>
                                    </table>
                                </div>
                                <!-- /.table-responsive -->
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
        <script src="modules/policy-module.js"></script>
        <!-- Page-L<script src="modules/policy-editor.js"></script>evel Demo Scripts - Tables - Use for reference -->

    </body>

</html>
