/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import com.linuxrouter.netcool.client.EventMap;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJB;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */

public class ScriptJob extends AutomationJob {

    private final Logger logger = Logger.getLogger(ScriptJob.class);

    

    @Override
    public void executeContext(Connection con) {
        // Execution Context
        
        AutomationReader reader = automationDao.getReaderByName(this.policyName);

        ArrayList<EventMap> events = this.omniClient.executeQuery(reader.getReaderSql(), this.readerConnName);

        HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents = new HashMap<>();
        Long beforeMap = System.currentTimeMillis();
        for (EventMap e : events) {
            e.setChangedMap(changedEvents);
        }
        Long afterMap = System.currentTimeMillis();
        logger.debug("Map Took: " + (afterMap - beforeMap) + " ms");
        Binding binding = new Binding();
        binding.setVariable("events", events);
        binding.setVariable("logger", logger);
        try {

            GroovyShell shell = new GroovyShell(binding);

            for (AutomationPolicies p : reader.getAutomationPoliciesList()) {

                if (p.getEnabled().equalsIgnoreCase("Y")) {
                    Long startTime = System.currentTimeMillis();
                    shell.evaluate(p.getScript());
                    Long endTime = System.currentTimeMillis();
                    logger.debug("Groovy Script[" + p.getPolicyName() + "] Execution Time: " + (endTime - startTime) + "ms");
                }
            }

        } catch (Exception ex) {
            logger.error("fail to execute script at job: " + this.readerConnName, ex);
        }

        //omniClient.commitChangedEvents(changedEvents, this.readerConnName);
    }

}
