/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.dao;

import com.linuxrouter.netcool.entitiy.AutomationConnection;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.entitiy.AutomationReaderFilter;
import com.linuxrouter.netcool.entitiy.AutomationUsers;
import com.linuxrouter.netcool.entitiy.DbConnections;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */
@Stateless
@LocalBean
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
        List<AutomationReader> list = null;
        try {
            list = q.getResultList();
        } catch (Exception ex) {
        }
        return list;

    }

    public List<AutomationReader> getAllReaders() {
        Query q = em.createNamedQuery("AutomationReader.findAll");

        List<AutomationReader> list = null;
        try {
            list = q.getResultList();
        } catch (Exception ex) {
        }
        return list;

    }

    public List<AutomationConnection> getEnabledConections() {
        Query q = em.createNamedQuery("AutomationConnection.findByEnabled");
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
        AutomationPolicies pol = (AutomationPolicies) q.getSingleResult();
        return pol;
    }

    public AutomationReader getReaderByName(String readerName) {
        Query q = em.createNamedQuery("AutomationReader.findByReaderName");
        q.setParameter("readerName", readerName);
        AutomationReader reader = (AutomationReader) q.getSingleResult();
        return reader;
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

    public void saveConnection(AutomationConnection connection) {
        logger.debug("Before");
        em.merge(connection);
        logger.debug("Merged..");
    }

    public AutomationConnection getConnectionByName(String connectionName) {
        Query q = em.createNamedQuery("AutomationConnection.findByConnectionName");
        q.setParameter("connectionName", connectionName);
        AutomationConnection connection = (AutomationConnection) q.getSingleResult();
        return connection;
    }

    public List<AutomationReaderFilter> getAllFilters() {
        Query q = em.createNamedQuery("AutomationReaderFilter.findAll");

        List<AutomationReaderFilter> list = null;
        try {
            list = q.getResultList();
        } catch (Exception ex) {
        }
        return list;
    }

    public AutomationReaderFilter getFilterByName(String filterName) {
        Query q = em.createNamedQuery("AutomationReaderFilter.findByFilterName");
        q.setParameter("filterName", filterName);
        AutomationReaderFilter connection = (AutomationReaderFilter) q.getSingleResult();
        return connection;
    }

    public List<AutomationPolicies> getAllPolicies() {
        Query q = em.createNamedQuery("AutomationPolicies.findAll");
        List<AutomationPolicies> policies = q.getResultList();
        return policies;
    }

    public List<DbConnections> getAllDbConnections() {
        Query q = em.createNamedQuery("DbConnections.findAll");
        List<DbConnections> connections = q.getResultList();
        return connections;
    }

}
