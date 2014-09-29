/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationConnection;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.response.BasicResponse;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */
@Stateless
@LocalBean
public class AutomationSession {

    private final Logger logger = Logger.getLogger(AutomationSession.class);

    @EJB
    private AutomationDao automationDao;

    @EJB
    private OmniClient omniClient;
    @EJB
    private UtilSession utilSession;

    public BasicResponse getAllReaders() {
        BasicResponse response = new BasicResponse();
        response.setSuccess(true);
        response.setPayLoad(automationDao.getAllReaders());
        return response;
    }

    /**
     * Update Policy list..
     *
     * @param readerName
     * @param policyName
     * @param script
     * @return
     */
    public BasicResponse updatePolicyScript(String readerName, String policyName, String script) {
        BasicResponse response = new BasicResponse();
        logger.debug("Getting policy: >" + policyName);
        try {
            AutomationPolicies pol = automationDao.getPolicyByName(policyName);
            response.setSuccess(true);
            pol.setScript(script);

        } catch (Exception ex) {
            response.setMsg(ex.getMessage());
        }
        return response;
    }

    /**
     * returns a list with all configured connections...
     *
     * @return
     */
    public BasicResponse getAllConnections() {
        BasicResponse response = new BasicResponse();
        response.setSuccess(true);
        response.setPayLoad(automationDao.getAllConnections());
        return response;
    }

    public BasicResponse getConnectionByName(String name) {
        BasicResponse response = new BasicResponse();
        response.setSuccess(true);
        response.setPayLoad(automationDao.getConnectionByName(name));
        return response;
    }

    public BasicResponse updateConnectionByName(String name, String user, String pass, String url, String enabled) {
        BasicResponse response = new BasicResponse();
        AutomationConnection connection = automationDao.getConnectionByName(name);
        connection.setEnabled(enabled);
        connection.setUsername(user);
        connection.setJdbcUrl(url);
        connection.setPassword(pass);
        response.setPayLoad(connection);
        try {
            logger.debug("Going to save...");
            automationDao.saveConnection(connection);
            omniClient.configureConnections();//critico
            response.setSuccess(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
}
