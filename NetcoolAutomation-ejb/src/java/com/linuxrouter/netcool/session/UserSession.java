/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.response.BasicResponse;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 * Class that manager user specific atributes
 *
 * @author lucas
 */
@Stateless
@LocalBean
public class UserSession {

    @EJB
    private AutomationDao automationDao;
    
    public BasicResponse authUser(String login, String pass) {
        BasicResponse response = new BasicResponse();
        return response;
    }
}
