/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.configuration.AutomationConstants;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.jobs.JobManager;
import com.linuxrouter.netcool.jobs.ScriptJob;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

/**
 * This holds Quartz Job Informations...
 *
 * @author lucas
 */
@Singleton
@LocalBean
@Startup
public class JobManagerBean {
    
    private final Logger logger = Logger.getLogger(JobManagerBean.class);
    @EJB
    private OmniClient omniclient;
    
    @EJB
    private AutomationDao automationDao;
    
    @PostConstruct
    private void startDefaultJob() {
        JobManager.init();
        startEnabledJobs();
        
    }
    
    @PreDestroy
    private void stopJobs() {
        JobManager.shutDown();
    } 
    
    public void startEnabledJobs() {
        logger.debug("Starting Enabled Jobs...");
        List<AutomationReader> readers = automationDao.getEnabledReaders();
        if (readers != null && readers.size() > 0) {
            for (AutomationReader reader : readers) {
                if (reader.getAutomationPoliciesList() != null && reader.getAutomationPoliciesList().size() > 0) {
                    logger.debug("Setting UP the Reader : [" + reader.getReaderName() + "] with :" + reader.getAutomationPoliciesList().size() + " Policy count..");
                    commitNewScriptJob(reader);
                } else {
                    logger.debug("The reader: " + reader.getReaderName() + " Is enabled but has no policy in it :(");
                }
            }
        }
        
    }
    
    public void commitNewScriptJob(AutomationReader reader) {        
        JobDataMap map = new JobDataMap();
        map.put(AutomationConstants.JOBNAME, reader.getReaderName());
        map.put(AutomationConstants.DBPOOL, omniclient.getPoolingConnectionByName(reader.getConnectionName().getConnectionName()));
        map.put(AutomationConstants.READER_CONNECTION_NAME, reader.getConnectionName().getConnectionName());
        
        map.put(AutomationConstants.OMNICLIENT, omniclient);
        map.put(AutomationConstants.SQL_TEXT, reader.getReaderSql());
        map.put(AutomationConstants.POLICIES, reader.getAutomationPoliciesList());        
        JobDetail det = JobManager.commitNewJob(reader.getReaderName(), reader.getCronInterval(), ScriptJob.class, map);        
        
    }
}
