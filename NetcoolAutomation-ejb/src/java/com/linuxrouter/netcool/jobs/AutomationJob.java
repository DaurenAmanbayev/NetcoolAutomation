/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.configuration.AutomationConstants;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.log.AutomationLogAppender;
import com.linuxrouter.netcool.session.LoggerSocket;
import com.linuxrouter.netcool.session.PluginManager;
import com.linuxrouter.netcool.session.QueryUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
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
    protected Logger readerLogger = null;
    protected OmniClient omniClient = null;

    protected String groovyScript = "";

    protected String readerConnName = "";

    protected AutomationDao automationDao;

    protected QueryUtils queryUtils;

    protected LoggerSocket loggerSocket;
    protected AutomationLogAppender webLoger;

    protected PluginManager pluginManager;

    private void configureReaderLogger() {
        String pattern = "[%5p] %d{dd-MMM-yyyy HH:mm:ss} (Reader:" + this.policyName + ") - %m";
        webLoger = new AutomationLogAppender(loggerSocket);
        webLoger.setLayout(new PatternLayout(pattern));
        readerLogger = Logger.getLogger(AutomationJob.class);
        readerLogger.addAppender(webLoger);
    }

    private void removeWebLogger() {
        readerLogger.removeAppender(webLoger);
    }

    /**
     * Executes a Job Context...
     *
     * @param jec
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        this.policyName = jec.getJobDetail().getJobDataMap().getString(AutomationConstants.JOBNAME);
        this.omniClient = (OmniClient) jec.getJobDetail().getJobDataMap().get(AutomationConstants.OMNICLIENT);
        this.readerConnName = (String) jec.getJobDetail().getJobDataMap().get(AutomationConstants.READER_CONNECTION_NAME);
        this.omniBusConnection = omniClient.getPoolingConnectionByName(readerConnName);
        this.automationDao = (AutomationDao) jec.getJobDetail().getJobDataMap().get(AutomationConstants.AUTOMATIONDAO);
        this.queryUtils = (QueryUtils) jec.getJobDetail().getJobDataMap().get(AutomationConstants.QUERY_UTILS);
        this.loggerSocket = (LoggerSocket) jec.getJobDetail().getJobDataMap().get(AutomationConstants.WEBSOCKET_LOGGER);
        this.pluginManager = (PluginManager) jec.getJobDetail().getJobDataMap().get(AutomationConstants.PLUGIN_MANAGER);
        //this.connectionMap = (HashMap<String, Connection>) jec.getJobDetail().getJobDataMap().get(AutomationConstants.CONNECTION_HASH);
        //readerLogger = Logger.getLogger(this.policyName);
        configureReaderLogger();
        readerLogger.debug("Starting : " + this.policyName);
        Long startTime = System.currentTimeMillis();
        Connection con = null;
        try {
            con = omniBusConnection.getConnection();
        } catch (SQLException ex) {
            readerLogger.error("Generic SQL Exception trying to get connection", ex);
        }
        try {
            readerLogger.debug("Starting script context");
            executeContext(con);

        } catch (Exception ex) {
            readerLogger.error("Generic error in " + this.policyName, ex);
        }
        try {
            con.close();
        } catch (SQLException ex) {
            readerLogger.error("Fail to close omnibus connection", ex);
        }

        Long endTime = System.currentTimeMillis();
        readerLogger.debug("Done Reader:" + this.policyName + " Time Took: " + (endTime - startTime) + " ms");
        removeWebLogger();
    }

    /**
     * Has the implementation from the class
     */
    public abstract void executeContext(Connection con);

}
