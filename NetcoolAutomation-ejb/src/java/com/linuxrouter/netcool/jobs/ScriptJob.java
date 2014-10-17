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
import com.linuxrouter.netcool.log.AutomationLogAppender;
import com.linuxrouter.netcool.plugin.AutomationPluginInterface;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.codehaus.groovy.control.CompilationFailedException;
import org.quartz.DisallowConcurrentExecution;

/**
 *
 * @author lucas
 */
@DisallowConcurrentExecution
public class ScriptJob extends AutomationJob {

    private final Logger logger = Logger.getLogger(ScriptJob.class);

    private void configurePolicyLogger(String policy) {
        //[%5p] %d{dd-MMM-yyyy HH:mm:ss} 

    }

    @Override
    public void executeContext(Connection con) {
        // Execution Context
        AutomationLogAppender myScriptLogger = new AutomationLogAppender(loggerSocket);
        myScriptLogger.setLayout(new PatternLayout("[%5p] %d{dd-MMM-yyyy HH:mm:ss} (ScriptContext:" + this.policyName + ") - %m"));
        logger.addAppender(myScriptLogger);
        Long mainStart = System.currentTimeMillis();
        AutomationReader reader = automationDao.getReaderByName(this.policyName);

        //for each filter in thre reader...validate is filter has filters...
        if (reader.getAutomationReaderFilterList() != null && reader.getAutomationReaderFilterList().size() > 0) {

            for (AutomationReaderFilter filter : reader.getAutomationReaderFilterList()) {

                if (filter.getEnabled().equalsIgnoreCase("Y")) {
                    Boolean persistState = false;
                    if (filter.getPersistState().equalsIgnoreCase("Y")) {
                        persistState = true;
                    }
                    ArrayList<EventMap> events = this.omniClient.executeQuery(filter.getFilterSql(), this.readerConnName, filter, persistState);
                    if (events != null && events.size() > 0) {
                        HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents = new HashMap<>();
                        Long beforeMap = System.currentTimeMillis();
                        for (EventMap e : events) {
                            e.setChangedMap(changedEvents);
                        }

                        Integer stateChanged = Integer.parseInt((String) events.get(events.size() - 1).get("StateChange"));
                        Long afterMap = System.currentTimeMillis();

                        try {

                            for (AutomationPolicies p : filter.getAutomationPoliciesList()) {

                                Logger policyLogger = Logger.getLogger(AutomationPolicies.class);

                                String pattern = "[%5p] %d{dd-MMM-yyyy HH:mm:ss} (Reader:" + this.policyName + " Policy:" + p.getPolicyName() + ") - %m";
                                AutomationLogAppender appender = new AutomationLogAppender(loggerSocket);
                                appender.setLayout(new PatternLayout(pattern));
                                policyLogger.addAppender(appender);
                                Binding binding = new Binding();
                                
                                binding.setVariable("q", queryUtils);
                                binding.setVariable("events", events);
                                binding.setVariable("logger", policyLogger);
                                binding.setVariable("filterName", filter.getFilterName());
                                binding.setVariable("omniSql", omniClient);

                                
                                binding.setVariable("plugins", pluginManager.getPluginsImpl());
//                                if (plugins != null) {
//                                    
//                                    logger.debug("Got Plugins:" + plugins.size());
//                                    Long pluginStartTime = System.currentTimeMillis();
////                                    Iterator it = plugins.entrySet().iterator();
////
////                                    while (it.hasNext()) {
////                                        try {
////                                            Map.Entry pairs = (Map.Entry) it.next();
////                                            AutomationPluginInterface plugin = plugins.get(pairs.getKey());
////
////                                            binding.setVariable(plugin.getPluginAlias(), plugin.getPluginImpl());
////                                            logger.debug("Ok Registering new plugin with Alias: [" + plugin.getPluginAlias() + "]");
////                                        } catch (Exception pluginEx) {
////                                            logger.error("Failed to create plugin");
////                                        }
////                                    }
//                                    Long pluginEndTime = System.currentTimeMillis();
//                                    logger.debug("Plugin time was::: " + (pluginEndTime - pluginStartTime) + "[ms]");
//                                } else {
//                                    logger.debug("No plugin Found");
//                                }

                                GroovyShell shell = new GroovyShell(binding);
                                if (p.getEnabled().equalsIgnoreCase("Y")) {
                                    Long startTime = System.currentTimeMillis();
                                    try {
                                        shell.evaluate(p.getScript());
                                    } catch (CompilationFailedException ex) {
                                        //Compilation exeption :/
                                        logger.error("Fail to compile groovy script :/ ", ex);
                                    }
                                    Long endTime = System.currentTimeMillis();
                                    //logger.debug("Groovy Script[" + p.getPolicyName() + "] Execution Time: " + (endTime - startTime) + "ms");
                                    // logger.debug("Changed Events Size is: " + changedEvents.size());
                                }
                                policyLogger.removeAppender(appender);
                            }

                        } catch (Exception ex) {
                            logger.error("fail to execute script at job: " + this.readerConnName, ex);
                        }
                        if (changedEvents != null && changedEvents.size() > 0) {
                            omniClient.commitChangedEvents(changedEvents, reader);
                        }

                        filter.setStateChange(stateChanged);
                        //automationDao.saveReaderStatus(reader);

                        Long mainEnd = System.currentTimeMillis();
                        //logger.debug("Delta Execution:" + (mainEnd - mainStart) + " ms");
                        automationDao.saveFilterStatus(filter);
                    } else {
                        //automationDao.saveReaderStatus(reader);
                        Long mainEnd = System.currentTimeMillis();
                        logger.debug("Delta Execution:" + (mainEnd - mainStart) + " ms");
                        logger.debug("No Event Found...");
                    }
                    logger.debug("Done Filter: " + filter.getFilterName());
                } else {
                    logger.debug("Filter is disabled..");
                }
            }
        } else {
            logger.debug("There is no filter in the reader...");
        }
        logger.removeAppender(myScriptLogger);
    }

}
