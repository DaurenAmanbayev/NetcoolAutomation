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
    @Path("connection/list")
    public String getAllConnections() {
        return converter.convert2Json(automationSession.getAllConnections());
    }
}
