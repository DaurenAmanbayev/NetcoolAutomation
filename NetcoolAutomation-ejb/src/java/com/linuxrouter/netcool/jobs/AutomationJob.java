/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.configuration.AutomationConstants;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
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

    protected String jobName = "";
    protected PoolingDataSource<PoolableConnection> omniBusConnection = null;
    protected HashMap<String, Connection> connectionMap = null;
    protected Logger logger = null;
    protected OmniClient omniClient = null;

    /**
     * Executes a Job Context...
     *
     * @param jec
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        this.jobName = jec.getJobDetail().getJobDataMap().getString(AutomationConstants.JOBNAME);
        this.omniBusConnection = (PoolingDataSource<PoolableConnection>) jec.getJobDetail().getJobDataMap().get(AutomationConstants.DBPOOL);
        this.omniClient = (OmniClient) jec.getJobDetail().getJobDataMap().get(AutomationConstants.OMNICLIENT);
        //this.connectionMap = (HashMap<String, Connection>) jec.getJobDetail().getJobDataMap().get(AutomationConstants.CONNECTION_HASH);
        logger = Logger.getLogger(this.jobName);
        logger.debug("Starting : " + this.jobName);
        Long startTime = System.currentTimeMillis();
        Connection con = null;
        try {
            con = omniBusConnection.getConnection();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(AutomationJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            executeContext(con);

        } catch (Exception ex) {
            logger.error("Generic error in " + this.jobName, ex);
        }
        try {
            con.close();
        } catch (SQLException ex) {
            logger.error("Fail to close omnibus connection",ex);
        }

        Long endTime = System.currentTimeMillis();
        logger.debug("Done All Script:" + this.jobName + " Time Took: " + (endTime - startTime) + " ms");
    }

    /**
     * Has the implementation from the class
     */
    public abstract void executeContext(Connection con);

}
