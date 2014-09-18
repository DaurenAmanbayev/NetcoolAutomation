/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.configuration.AutomationConstants;
import com.linuxrouter.netcool.jobs.DefaultEventReaderJob;
import com.linuxrouter.netcool.jobs.JobManager;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
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

    @EJB
    private OmniClient omniclient;
    
    @PostConstruct
    private void startDefaultJob() {
        JobManager.init();
        JobDataMap map = new JobDataMap();
        map.put(AutomationConstants.JOBNAME, "Default");
        map.put(AutomationConstants.DBPOOL,omniclient.getOmniBusDataSource());
        map.put(AutomationConstants.OMNICLIENT,omniclient);
        JobDetail det = JobManager.commitNewJob("EventReader", "*/30 * * * * ?", DefaultEventReaderJob.class, map);
    }
    
    @PreDestroy
    private void stopJobs(){
        JobManager.shutDown();
    }
}
