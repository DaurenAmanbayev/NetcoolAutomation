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
import java.util.HashMap;
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
import org.quartz.JobKey;

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

    @EJB
    private QueryUtils queryUtils;
    
    private HashMap<String, JobKey> jobMap = new HashMap<>();

    @PostConstruct
    private void startDefaultJob() {
        JobManager.init();
        startEnabledJobs();

    }

    @PreDestroy
    private void stopJobs() {
        List<AutomationReader> readers = automationDao.getEnabledReaders();
        for (AutomationReader reader : readers) {
            logger.debug("Stopping Reader: " + reader.getReaderName());
            stopReader(reader);
        }
        JobManager.shutDown();
    }

    public void startEnabledJobs() {
        logger.debug("Starting Enabled Jobs...");
        List<AutomationReader> readers = automationDao.getEnabledReaders();
        if (readers != null && readers.size() > 0) {
            for (AutomationReader reader : readers) {
                if (reader.getAutomationReaderFilterList() != null && reader.getAutomationReaderFilterList().size() > 0) {
                    if (reader.getConnectionName().getEnabled().equalsIgnoreCase("Y")) {
                        logger.debug("Setting UP the Reader : [" + reader.getReaderName() + "] ");
                        commitNewScriptJob(reader);
                    }

                } else {
                    logger.debug("The reader: " + reader.getReaderName() + " Is enabled but has no policy in it :(");
                }
            }
        }

    }

    public void commitNewScriptJob(AutomationReader reader) {
        JobDataMap map = new JobDataMap();
        map.put(AutomationConstants.JOBNAME, reader.getReaderName());
        // map.put(AutomationConstants.DBPOOL, omniclient.getPoolingConnectionByName(reader.getConnectionName().getConnectionName()));
        map.put(AutomationConstants.READER_CONNECTION_NAME, reader.getConnectionName().getConnectionName());
        map.put(AutomationConstants.OMNICLIENT, omniclient);
        map.put(AutomationConstants.QUERY_UTILS, queryUtils);
        map.put(AutomationConstants.AUTOMATIONDAO, automationDao);
        JobDetail det = JobManager.commitNewJob(reader.getReaderName(), reader.getCronInterval(), ScriptJob.class, map);
        jobMap.put(reader.getReaderName(), det.getKey());
    }

    public void stopReader(AutomationReader reader) {
        if (jobMap.get(reader.getReaderName()) != null) {
            JobKey key = jobMap.get(reader.getReaderName());
            JobManager.stopJob(key);
        }
    }

    public void updateReader(AutomationReader reader) {

        this.stopReader(reader); // Remove
        if (reader.getEnabled().equalsIgnoreCase("Y")) {
            if (reader.getConnectionName().getEnabled().equalsIgnoreCase("Y")) {
                this.commitNewScriptJob(reader); //Addiciona}
            }
        } else {
            logger.debug("Stopping Reader ");
        }
    }
}
