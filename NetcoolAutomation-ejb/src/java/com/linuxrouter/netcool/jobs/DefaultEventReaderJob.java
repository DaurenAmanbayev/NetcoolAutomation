/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import org.quartz.DisallowConcurrentExecution;

/**
 *
 * @author lucas
 */
@DisallowConcurrentExecution
public class DefaultEventReaderJob extends AutomationJob {

    @Override
    public void executeContext(Connection con) {
        // Execution Context
        String sql = "select * from alerts.status where Severity = 3";
        ArrayList<HashMap<String, Object>> events = this.omniClient.executeQuery(sql);
        Binding binding = new Binding();
        binding.setVariable("events", events);
        try {
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate("for (x in events){"
                    + " if (x.Node =~/lda/){println x.Identifier;} "
                    + "};println 'done....'");
            
        } catch (Exception ex) {
            logger.error("fail to execute script at job: " + this.jobName ,ex);
        }
    }
}
