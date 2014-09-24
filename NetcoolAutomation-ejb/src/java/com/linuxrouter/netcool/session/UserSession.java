/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import com.linuxrouter.netcool.entitiy.AutomationUsers;
import com.linuxrouter.netcool.response.BasicResponse;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.jws.WebService;
import org.apache.log4j.Logger;

/**
 * Class that manager user specific atributes
 *
 * @author lucas
 */
@Stateless
@LocalBean

public class UserSession {
    
    private final Logger logger = Logger.getLogger(UserSession.class);
    @EJB
    private AutomationDao automationDao;
    
    @EJB
    private UtilSession utilSession;
    
    public BasicResponse authUser(String login, String pass) {
        BasicResponse response = new BasicResponse();
        try {
            AutomationUsers user = automationDao.getUserByLogin(login);
            if (user.getPassword().equals(utilSession.getMd5HashFromString(pass))) {
                response.setSuccess(true);
                response.setMsg("Auth ok!");
            } else {
                response.setSuccess(false);
                response.setMsg("Invalid Password.");
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMsg("User not found...");
        }
        
        return response;
    }
 
    
}
