/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

import groovy.lang.GroovyShell;
import java.sql.Connection;

/**
 *
 * @author lucas
 */
public class ScriptJob extends AutomationJob{

    @Override
    public void executeContext(Connection con) {
        GroovyShell shell = new GroovyShell();
        shell.evaluate("println 'Oi xD'");        
    }
    
}
