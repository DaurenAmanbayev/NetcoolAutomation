/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import org.quartz.JobDataMap;
import static org.quartz.TriggerBuilder.*;

/**
 *
 * @author G0004218
 */
public class JobManager {

    public static ArrayList<JobHolder> jobs = new ArrayList<>();
    public static Scheduler scheduler;
    public static Logger logger = Logger.getLogger(JobManager.class);
    private static Boolean started = false;

    /**
     * Cria o scheduler..
     */
    private static void createScheduler() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            started = true;
        } catch (SchedulerException ex) {
            logger.error("Failed to create Scheduler..", ex);
        }
    }

    /**
     * Commita e agenda a job
     *
     * @param name
     * @param cronInterval
     * @param jobClass
     */
    public static JobDetail commitNewJob(String name, String cronInterval, Class jobClass, JobDataMap map) {
        JobHolder holder = new JobHolder();
        JobDetail job = newJob(jobClass)
                .withIdentity(name, "maingroup")
                .setJobData(map)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity(name + "trigger", "maingroup")
                .startNow()
                .withSchedule(cronSchedule(cronInterval))
                .build();

        holder.setJobName(name);
        holder.setJobs(job);
        holder.setTrigger(trigger);
        jobs.add(holder);
        try {
            scheduler.scheduleJob(holder.getJobs(), holder.getTrigger());
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            logger.debug("Commited: " + jobClass.getName() + " Next  execution at: " + df.format(holder.getTrigger().getNextFireTime()));
        } catch (SchedulerException ex) {
            logger.error("Fail to commit new Job...", ex);
        }
        return job;
    }

    /**
     * para o scheduler
     */
    public static void init() {
        if (!started) {
            createScheduler();
            logger.debug("JobManager Started...");
        } else {
            logger.debug("Already started...");
        }

    }

    /**
     * para o scheduler
     */
    public static void shutDown() {
        if (started) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException ex) {
                logger.error("Falha ao parar scheduler", ex);
            }
            logger.debug("Jobmanager Stopped...");
        }
    }
}
