/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import com.linuxrouter.netcool.client.EventMap;
import com.linuxrouter.netcool.entitiy.AutomationPolicies;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.entitiy.AutomationReaderFilter;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
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

        //for each filter in thre reader...validate is filter has filters...
        if (reader.getAutomationReaderFilterList() != null && reader.getAutomationReaderFilterList().size() > 0) {

            for (AutomationReaderFilter filter : reader.getAutomationReaderFilterList()) {

                if (filter.getEnabled().equalsIgnoreCase("Y")) {

                    ArrayList<EventMap> events = this.omniClient.executeQuery(filter.getFilterSql(), this.readerConnName, filter);
                    if (events != null && events.size() > 0) {
                        HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents = new HashMap<>();
                        Long beforeMap = System.currentTimeMillis();
                        for (EventMap e : events) {
                            e.setChangedMap(changedEvents);
                        }

                        Integer stateChanged = Integer.parseInt((String) events.get(events.size() - 1).get("StateChange"));
                        Long afterMap = System.currentTimeMillis();
                        logger.debug("Map Took: " + (afterMap - beforeMap) + " ms");
                        Binding binding = new Binding();
                        binding.setVariable("events", events);
                        binding.setVariable("logger", logger);
                        try {

                            GroovyShell shell = new GroovyShell(binding);

                            for (AutomationPolicies p : filter.getAutomationPoliciesList()) {
                                if (p.getEnabled().equalsIgnoreCase("Y")) {
                                    Long startTime = System.currentTimeMillis();
                                    shell.evaluate(p.getScript());
                                    Long endTime = System.currentTimeMillis();
                                    logger.debug("Groovy Script[" + p.getPolicyName() + "] Execution Time: " + (endTime - startTime) + "ms");
                                    logger.debug("Changed Events Size is: " + changedEvents.size());
                                }
                            }

                        } catch (Exception ex) {
                            logger.error("fail to execute script at job: " + this.readerConnName, ex);
                        }
                        if (changedEvents != null && changedEvents.size() > 0) {
                            omniClient.commitChangedEvents(changedEvents, this.readerConnName);
                        }
                        filter.setStateChange(stateChanged);
                        //automationDao.saveReaderStatus(reader);
                        automationDao.saveFilterStatus(filter);
                    } else {
                        logger.debug("No Event Found...");
                    }
                } else {
                    logger.debug("Filter is disabled..");
                }
            }
        } else {
            logger.debug("There is no filter in the reader...");
        }
    }

}
