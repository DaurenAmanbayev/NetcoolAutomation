/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import com.linuxrouter.netcool.client.EventMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import org.quartz.DisallowConcurrentExecution;

/**
 * This is the default event reader.
 * @author lucas
 */
@DisallowConcurrentExecution
public class DefaultEventReaderJob extends AutomationJob {
    
    @Override
    public void executeContext(Connection con) {
        // Execution Context
        String sql = "select * from alerts.status where Severity > 2";
        ArrayList<EventMap> events = this.omniClient.executeQuery(sql);
        
        HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents = new HashMap<>();
        Long beforeMap = System.currentTimeMillis();        
        for (EventMap e : events) {
            e.setChangedMap(changedEvents);
        }
        Long afterMap = System.currentTimeMillis();
        logger.debug("Map Took: " +(afterMap-beforeMap) + " ms");
        Binding binding = new Binding();
        binding.setVariable("events", events);
        try {
            Long startTime = System.currentTimeMillis();
            GroovyShell shell = new GroovyShell(binding);            
            shell.evaluate("for (x in events){"
                    + " if (x.Summary =~/CTA/){"
                    + "   "
                    + "   x.Summary = 'Nishi + CTA'; "
                    + "   x.Severity = 5; "
                    + " } "
                    + "} ");
            Long endTime = System.currentTimeMillis();
            logger.debug("Groovy Script Execution Time: " + (endTime - startTime) + "ms");
        } catch (Exception ex) {
            logger.error("fail to execute script at job: " + this.jobName, ex);
        }
        
        omniClient.commitChangedEvents(changedEvents);
    }
}
