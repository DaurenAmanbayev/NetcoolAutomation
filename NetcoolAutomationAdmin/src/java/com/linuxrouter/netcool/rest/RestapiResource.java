/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.rest;

import com.linuxrouter.netcool.response.BasicResponse;
import com.linuxrouter.netcool.session.AutomationSession;
import com.linuxrouter.netcool.session.GsonConverter;
import com.linuxrouter.netcool.session.UserSession;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 * REST Web Service
 *
 * @author lucas
 */
@Path("restapi")
@RequestScoped
public class RestapiResource {

    private final Logger logger = Logger.getLogger(RestapiResource.class);

    @EJB
    private GsonConverter converter;

    @EJB
    private AutomationSession automationSession;

    @EJB
    private UserSession userSession;

    @Context
    private UriInfo context;

    @Context
    private HttpServletRequest request;

    /**
     * Creates a new instance of RestapiResource
     */
    public RestapiResource() {
    }

    @POST
    @Produces("application/json")
    @Path("user/login")
    public String doLogin(@FormParam("user") String user, @FormParam("password") String password) {
        logger.debug("Auth User: " + user + " With Pass:[********]");
        BasicResponse s = userSession.authUser(user, password);
        if (s.getSuccess()) {
            request.getSession().setAttribute("AUTH", true);
            logger.debug("Session created...");
        }
        return converter.convert2Json(s);
    }

    @GET
    @Produces("application/json")
    @Path("reader/list")
    public String getAllReaders() {

        return converter.convert2Json(automationSession.getAllReaders());
    }

    @GET
    @Produces("application/json")
    @Path("reader/{name}")
    public String getReaderByName(@PathParam("name") String name) {
        return converter.convert2Json(automationSession.getReaderByName(name));
    }

    @GET
    @Produces("application/json")
    @Path("connection/list")
    public String getAllConnections() {
        return converter.convert2Json(automationSession.getAllConnections());
    }

    @GET
    @Produces("application/json")
    @Path("connection/{name}")
    public String getConnectionByName(@PathParam("name") String conName) {
        return converter.convert2Json(automationSession.getConnectionByName(conName));
    }

    @POST
    @Produces("application/json")
    @Path("connection/{name}/update")
    public String updateConnectionByName(@PathParam("name") String conName,
            @FormParam("user") String user, @FormParam("pass") String pass,
            @FormParam("url") String url, @FormParam("enabled") String enabled) {
        logger.debug("Got user:" + user);
        return converter.convert2Json(automationSession.updateConnectionByName(conName, user, pass, url, enabled));
    }

    @GET
    @Produces("application/json")
    @Path("connection/byreader/{name}")
    public String getConnectionByReaderName(@PathParam("name") String readerName) {
        return converter.convert2Json(automationSession.getConnectionByReaderName(readerName));
    }

    @POST
    @Produces("application/json")
    @Path("reader/{readerName}/update")
    public String updateReaderByName(@PathParam("readerName") String readerName,
            @FormParam("connectionName") String connectionName,
            @FormParam("cronString") String cronString,
            @FormParam("enabled") String enable) {
        return converter.convert2Json(automationSession.updateReaderByName(readerName, connectionName, cronString, enable));
    }

    @GET
    @Produces("application/json")
    @Path("filter/list")
    public String getAllFilters() {
        return converter.convert2Json(automationSession.getAllFilters());
    }

    @GET
    @Produces("application/json")
    @Path("filter/{name}")
    public String getFilterByBame(@PathParam("name") String filterName) {
        return converter.convert2Json(automationSession.getFilterByName(filterName));
    }

    @GET
    @Produces("application/json")
    @Path("reader/byfilter/{name}")
    public String getReaderByFilter(@PathParam("name") String filterName) {
        return converter.convert2Json(automationSession.getReaderByFilterName(filterName));
    }

    @POST
    @Produces("application/json")
    @Path("filter/{filterName}/update")
    public String updateFilterByName(@PathParam("filterName") String filterName,
            @FormParam("readerName") String readerName,
            @FormParam("fiterSql") String filterSql,
            @FormParam("enabled") String enable) {
        return converter.convert2Json(automationSession.updateFilterByName(filterName, readerName, filterSql, enable));
    }

    @GET
    @Produces("application/json")
    @Path("poilicy/{name}")
    public String getPolicyByName(@PathParam("name") String policeName) {
        return converter.convert2Json(automationSession.getPolicyByName(policeName));
    }

    @POST
    @Produces("application/json")
    @Path("poilicy/{name}/update")
    public String updatePolicyScript(@PathParam("name") String policeName, @FormParam("script") String script) {
        return converter.convert2Json(automationSession.updatePolicyScript(policeName, script));
    }

    @GET
    @Produces("application/json")
    @Path("policy/list")
    public String getAllPolicies() {
        return converter.convert2Json(automationSession.getallPolicies());
    }

    @POST
    @Produces("application/json")
    @Path("policy/{name}/reader/update")
    public String updatePolicyReaderAndStatus(@PathParam("name") String policeName,
            @FormParam("filter") String filter, @FormParam("enabled") String enable) {
        return converter.convert2Json(automationSession.setPolicyFilterAndStatusByName(policeName, filter, enable));
    }

    @GET
    @Produces("application/json")
    @Path("plugins/list")
    public String getAllPlugins() {
        return converter.convert2Json(automationSession.getAllplugins());
    }

    @GET
    @Produces("appplication/json")
    @Path("plugins/{name}")
    public String getPluginByName(@PathParam("name") String name) {
        return converter.convert2Json(automationSession.getPluginByName(name));
    }

}
