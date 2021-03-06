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
import com.linuxrouter.netcool.entitiy.AutomationReaderFilter;
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
    @EJB
    private JobManagerBean jobManager;

    /**
     * List All readers...
     *
     * @return
     */
    public BasicResponse getAllReaders() {
        BasicResponse response = new BasicResponse();
        response.setSuccess(true);
        response.setPayLoad(automationDao.getAllReaders());
        return response;
    }
    
    public BasicResponse getReaderByName(String name) {
        BasicResponse response = new BasicResponse();
        response.setSuccess(true);
        response.setPayLoad(automationDao.getReaderByName(name));
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
    public BasicResponse updatePolicyScript(String policyName, String script) {
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

    /**
     * get a single connection by name
     *
     * @param name
     * @return
     */
    public BasicResponse getConnectionByName(String name) {
        BasicResponse response = new BasicResponse();
        response.setSuccess(true);
        response.setPayLoad(automationDao.getConnectionByName(name));
        return response;
    }

    /**
     * Updates a reader connection by name
     *
     * @param name
     * @param user
     * @param pass
     * @param url
     * @param enabled
     * @return
     */
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
            // if (!connection.getEnabled().equalsIgnoreCase("Y")) {//disabling...
            for (AutomationReader reader : connection.getAutomationReaderList()) {
                jobManager.updateReader(reader);
            }
           // }else{

            //}
            omniClient.configureConnections();//critico
            response.setSuccess(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
    
    public BasicResponse getConnectionByReaderName(String readerName) {
        BasicResponse response = new BasicResponse();
        AutomationReader reader = automationDao.getReaderByName(readerName);
        if (reader != null) {
            AutomationConnection connection = reader.getConnectionName();
            response.setSuccess(true);
            response.setPayLoad(connection);
        }
        return response;
    }
    
    public BasicResponse updateReaderByName(String readerName, String connectionName, String cronString, String enabled) {
        BasicResponse response = new BasicResponse();
        logger.debug("Getting connection: " + connectionName + " For Reader : " + readerName);
        AutomationConnection connection = automationDao.getConnectionByName(connectionName);
        AutomationReader reader = automationDao.getReaderByName(readerName);
        reader.setConnectionName(connection);
        reader.setCronInterval(cronString);
        reader.setEnabled(enabled);
        try {
            automationDao.saveReaderStatus(reader);
            response.setSuccess(true);
            jobManager.updateReader(reader);
        } catch (Exception ex) {
            response.setMsg("Failed to save connection");
        }
        
        return response;
        
    }
    
    public BasicResponse getAllFilters() {
        BasicResponse response = new BasicResponse();
        List<AutomationReaderFilter> list = null;
        list = automationDao.getAllFilters();
        if (list != null) {
            response.setPayLoad(list);
            response.setSuccess(true);
        }
        return response;
    }
    
    public BasicResponse getReaderByFilterName(String filterName) {
        BasicResponse response = new BasicResponse();
        AutomationReaderFilter filter = automationDao.getFilterByName(filterName);
        if (filter != null) {
            response.setSuccess(true);
            response.setPayLoad(filter.getReaderName());
        }
        return response;
    }
    
    public BasicResponse getFilterByName(String filterName) {
        BasicResponse response = new BasicResponse();
        AutomationReaderFilter filter = automationDao.getFilterByName(filterName);
        if (filter != null) {
            response.setSuccess(true);
            response.setPayLoad(filter);
        }
        return response;
    }
    
    public BasicResponse updateFilterByName(String filterName, String readerName, String filterSql, String enabled) {
        BasicResponse response = new BasicResponse();
        AutomationReaderFilter filter = automationDao.getFilterByName(filterName);
        AutomationReader reader = automationDao.getReaderByName(readerName);
        filter.setReaderName(reader);
        filter.setFilterSql(filterSql);
        filter.setEnabled(enabled);
        try {
            automationDao.saveFilterStatus(filter);
            response.setSuccess(true);
        } catch (Exception ex) {
        }
        return response;
    }
    
    public BasicResponse getPolicyByName(String policyName) {
        BasicResponse response = new BasicResponse();
        AutomationPolicies pol = automationDao.getPolicyByName(policyName);
        if (pol != null) {
            response.setPayLoad(pol);
            response.setSuccess(true);
        }
        return response;
    }
    
    public BasicResponse getallPolicies() {
        BasicResponse response = new BasicResponse();
        try {
            response.setPayLoad(automationDao.getAllPolicies());
            response.setSuccess(true);
        } catch (Exception ex) {
            
        }
        
        return response;
    }
    
    public BasicResponse setPolicyFilterAndStatusByName(String policyName, String filterName, String enabled) {
        BasicResponse response = new BasicResponse();
        logger.debug("Update:: " + policyName + " Ena:" + enabled + " Filter: " + filterName);
        try {
            AutomationPolicies policy = automationDao.getPolicyByName(policyName);
            policy.setEnabled(enabled);
            AutomationReaderFilter filter = automationDao.getFilterByName(filterName);
            policy.setFilterName(filter);
            automationDao.updatePolicy(policy);
            response.setSuccess(true);
            response.setPayLoad(policy);
            
        } catch (Exception ex) {
            
        }
        return response;
    }
}
