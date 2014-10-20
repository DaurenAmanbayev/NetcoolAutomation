/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.dao;

import com.linuxrouter.netcool.entitiy.AutomationConnection;
import com.linuxrouter.netcool.entitiy.AutomationPlugins;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.entitiy.AutomationReaderFilter;
import com.linuxrouter.netcool.entitiy.AutomationUsers;
import com.linuxrouter.netcool.entitiy.DbConnections;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */
@Singleton
@LocalBean
@Startup
public class AutomationDao {
    
    private final Logger logger = Logger.getLogger(AutomationDao.class);
    @PersistenceContext(name = "NetcoolAutomation-ejbPU")
    private EntityManager em;

    /**
     * Get a user instanca based on the login
     *
     * @param login
     * @return
     */
    public AutomationUsers getUserByLogin(String login) {
        Query q = em.createNamedQuery("AutomationUsers.findByLogin");
        q.setParameter("login", login);
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        try {
            AutomationUsers user = (AutomationUsers) q.getSingleResult();
            return user;
        } catch (Exception ex) {
            logger.debug("Login: [" + login + "] was not found in Database.");
            return null;
        }
    }

    /**
     * Persists a new user
     *
     * @param user
     */
    public void saveUser(AutomationUsers user) {
        em.persist(user);
    }
    
    public List<AutomationReader> getEnabledReaders() {
        Query q = em.createNamedQuery("AutomationReader.findByEnabled");
        q.setParameter("enabled", "Y");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<AutomationReader> list = null;
        try {
            list = q.getResultList();
        } catch (Exception ex) {
        }
        return list;
        
    }
    
    public List<AutomationReader> getAllReaders() {
        Query q = em.createNamedQuery("AutomationReader.findAll");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<AutomationReader> list = null;
        try {
            list = q.getResultList();
        } catch (Exception ex) {
        }
        return list;
        
    }
    
    public List<AutomationConnection> getEnabledConections() {
        Query q = em.createNamedQuery("AutomationConnection.findByEnabled");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter("enabled", "Y");
        try {
            List<AutomationConnection> list = q.getResultList();
            return list;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public List<AutomationConnection> getDisabledConnection() {
        Query q = em.createNamedQuery("AutomationConnection.findByEnabled");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter("enabled", "N");
        try {
            List<AutomationConnection> list = q.getResultList();
            return list;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public List<AutomationConnection> getAllConnections() {
        Query q = em.createNamedQuery("AutomationConnection.findAll");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        
        try {
            List<AutomationConnection> list = q.getResultList();
            return list;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public AutomationPolicies getPolicyByName(String name) {
        Query q = em.createNamedQuery("AutomationPolicies.findByPolicyName");
        q.setParameter("policyName", name);
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        try {
            AutomationPolicies pol = (AutomationPolicies) q.getSingleResult();
            return pol;
        } catch (Exception ex) {
            return null;
        }
        
    }
    
    public AutomationReader getReaderByName(String readerName) {
        Query q = em.createNamedQuery("AutomationReader.findByReaderName");
        q.setParameter("readerName", readerName);
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        try {
            AutomationReader reader = (AutomationReader) q.getSingleResult();
            return reader;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public void updatePolicy(AutomationPolicies pol) {
        em.merge(pol);
    }
    
    public void saveReaderStatus(AutomationReader reader) {
        em.merge(reader);
    }
    
    public void saveFilterStatus(AutomationReaderFilter filter) {        
        em.merge(filter);
    }
    
    public void detach(AutomationReaderFilter filter) {
        em.detach(filter);        
    }
    
    public void saveConnection(AutomationConnection connection) {
        logger.debug("Before");
        em.merge(connection);
        logger.debug("Merged..");
    }
    
    public void saveData(Object connection) {
        logger.debug("Before");
        em.persist(connection);
        logger.debug("Merged..");
    }
    
    public AutomationConnection getConnectionByName(String connectionName) {
        Query q = em.createNamedQuery("AutomationConnection.findByConnectionName");
        q.setParameter("connectionName", connectionName);
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        try {
            AutomationConnection connection = (AutomationConnection) q.getSingleResult();
            return connection;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public List<AutomationReaderFilter> getAllFilters() {
        Query q = em.createNamedQuery("AutomationReaderFilter.findAll");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<AutomationReaderFilter> list = null;
        try {
            list = q.getResultList();
        } catch (Exception ex) {
        }
        return list;
    }
    
    public AutomationReaderFilter getFilterByName(String filterName) {
        Query q = em.createNamedQuery("AutomationReaderFilter.findByFilterName");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter("filterName", filterName);
        try {
            AutomationReaderFilter connection = (AutomationReaderFilter) q.getSingleResult();
            return connection;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public List<AutomationPolicies> getAllPolicies() {
        Query q = em.createNamedQuery("AutomationPolicies.findAll");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<AutomationPolicies> policies = q.getResultList();
        return policies;
    }
    
    public List<DbConnections> getAllDbConnections() {
        Query q = em.createNamedQuery("DbConnections.findAll");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<DbConnections> connections = q.getResultList();
        return connections;
    }
    
    public List<AutomationPlugins> getEnabledPlugins() {
        Query q = em.createNamedQuery("AutomationPlugins.findByEnabled");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter("enabled", "Y");
        List<AutomationPlugins> plugins = q.getResultList();
        return plugins;
    }
    
    public List<AutomationPlugins> getAllPlugins() {
        Query q = em.createNamedQuery("AutomationPlugins.findAll");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        try {
            List<AutomationPlugins> plugins = q.getResultList();
            return plugins;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public AutomationPlugins getPluginByName(String pluginName) {
        Query q = em.createNamedQuery("AutomationPlugins.findByPluginName");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        try {
            q.setParameter("pluginName", pluginName);
            AutomationPlugins plugin = (AutomationPlugins) q.getSingleResult();
            
            return plugin;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}
