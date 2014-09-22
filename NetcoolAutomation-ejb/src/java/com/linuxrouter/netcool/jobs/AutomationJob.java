/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.configuration.AutomationConstants;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author lucas
 */
@DisallowConcurrentExecution
public abstract class AutomationJob implements Job {

    protected String policyName = "";
    protected PoolingDataSource<PoolableConnection> omniBusConnection = null;
    protected HashMap<String, Connection> connectionMap = null;
    protected Logger logger = null;
    protected OmniClient omniClient = null;
    protected String sqlReader = "";
    protected String groovyScript = "";
    protected List<AutomationPolicies> policies = new ArrayList<>();
    protected String readerConnName = "";

    /**
     * Executes a Job Context...
     *
     * @param jec
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        this.policyName = jec.getJobDetail().getJobDataMap().getString(AutomationConstants.JOBNAME);
        this.omniBusConnection = (PoolingDataSource<PoolableConnection>) jec.getJobDetail().getJobDataMap().get(AutomationConstants.DBPOOL);
        this.omniClient = (OmniClient) jec.getJobDetail().getJobDataMap().get(AutomationConstants.OMNICLIENT);
        this.sqlReader = (String) jec.getJobDetail().getJobDataMap().get(AutomationConstants.SQL_TEXT);
        this.readerConnName = (String) jec.getJobDetail().getJobDataMap().get(AutomationConstants.READER_CONNECTION_NAME);
        this.policies = (List<AutomationPolicies>) jec.getJobDetail().getJobDataMap().get(AutomationConstants.POLICIES);
        //this.connectionMap = (HashMap<String, Connection>) jec.getJobDetail().getJobDataMap().get(AutomationConstants.CONNECTION_HASH);
        logger = Logger.getLogger(this.policyName);
        logger.debug("Starting : " + this.policyName);
        Long startTime = System.currentTimeMillis();
        Connection con = null;
        try {
            con = omniBusConnection.getConnection();
        } catch (SQLException ex) {
            logger.error("Generic SQL Exception trying to get connection", ex);
        }
        try {

            executeContext(con);

        } catch (Exception ex) {
            logger.error("Generic error in " + this.policyName, ex);
        }
        try {
            con.close();
        } catch (SQLException ex) {
            logger.error("Fail to close omnibus connection", ex);
        }

        Long endTime = System.currentTimeMillis();
        logger.debug("Done All Script:" + this.policyName + " Time Took: " + (endTime - startTime) + " ms");
    }

    /**
     * Has the implementation from the class
     */
    public abstract void executeContext(Connection con);

}
