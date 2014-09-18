/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import java.util.ArrayList;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 *
 * @author G0004218
 */
public class JobHolder {

    private String jobName = "";
    private JobDetail jobs = null;
    private Trigger trigger = null;

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName the jobName to set
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return the jobs
     */
    public JobDetail getJobs() {
        return jobs;
    }

    /**
     * @param jobs the jobs to set
     */
    public void setJobs(JobDetail jobs) {
        this.jobs = jobs;
    }

    /**
     * @return the trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * @param trigger the trigger to set
     */
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }
}
