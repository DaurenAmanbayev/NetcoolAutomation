<%@include file="includes/auth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <%@include file="includes/default-header.jsp" %>

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
                        <h1 class="page-header">Filter Setings</h1>
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
                                    <table id="filter-table" class="table table-striped table-bordered table-hover">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Filter Name</th>
                                                <th>Reader Name</th>

                                                <th>Filter SQL</th>
                                                <th>Enabled</th>
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

        <!-- Modal -->
        <div class="modal fade" id="connection-detail" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="myModalLabel">Edit Reader Settings</h4>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-lg-12">
                                <form role="form">
                                    <fieldset disabled>

                                        <div class="form-group">
                                            <label>Filter Name</label>
                                            <input id="filter-name" class="form-control" placeholder="">                                       
                                        </div>
                                    </fieldset>
                                    <div class="form-group">
                                        <label>Reader</label>
                                        <select id="reader-cmb" class="form-control">                                           
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label>Filer SQL</label>
                                        <input id="filter-sql" class="form-control" placeholder="">                                       
                                    </div>

                                    <div class="form-group">
                                        <label>Status</label>
                                        <div class="checkbox">
                                            <label>
                                                <input id="filter-enabled" type="checkbox" value="">Enabled
                                            </label>
                                        </div>                                        
                                    </div>
                                    <!-- 
                                     <button type="submit" class="btn btn-default">Submit Button</button>
                                     <button type="reset" class="btn btn-default">Reset Button</button> -->
                                </form>
                            </div>
                            <!-- /.col-lg-6 (nested) -->

                            <!-- /.col-lg-6 (nested) -->
                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        <button id="save-filter-data" type="button" class="btn btn-primary">Save changes</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>

        <%@include file="includes/default-botton.jsp" %>

        <!-- Page-Level Demo Scripts - Tables - Use for reference -->
        <!-- Custom Theme JavaScript -->
        <script src="modules/filter-module.js"></script> 

    </body>

</html>
