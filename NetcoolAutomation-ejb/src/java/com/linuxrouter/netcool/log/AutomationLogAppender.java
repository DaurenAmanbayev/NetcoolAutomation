/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.log;

import com.linuxrouter.netcool.session.LoggerSocket;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author lucas
 */
public class AutomationLogAppender extends AppenderSkeleton {

    private LoggerSocket logger;

    public AutomationLogAppender(LoggerSocket logger) {
        this.logger = logger;
    }

    @Override
    protected void append(LoggingEvent event) {

        //logger.onMessage(this.layout.format(event));
    }

    @Override
    public void close() {
        //faz algo..que eu ainda não sei oque é....
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
