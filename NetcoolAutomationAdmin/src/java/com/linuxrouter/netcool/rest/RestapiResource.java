/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.rest;

import com.linuxrouter.netcool.session.GsonConverter;
import com.linuxrouter.netcool.session.UserSession;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.log4j.Logger;

/**
 * REST Web Service
 *
 * @author lucas
 */
@Path("restapi")
public class RestapiResource {

    
    private final Logger logger = Logger.getLogger(RestapiResource.class);
    
    @EJB
    private GsonConverter converter;
    
    @EJB
    private UserSession userSession;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestapiResource
     */
    public RestapiResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.linuxrouter.netcool.rest.RestapiResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of RestapiResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    
    
    @POST
    @Produces("application/json")
    @Path("user/login")
    public String doLogin(@QueryParam("user") String user,@QueryParam("password") String password){
        logger.debug("Auth User: " +user +  " With Pass:[********]");
        return converter.convert2Json(userSession.authUser(user, password));
    }
    
}
