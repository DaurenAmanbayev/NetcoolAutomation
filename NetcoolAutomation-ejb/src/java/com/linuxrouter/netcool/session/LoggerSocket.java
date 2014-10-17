/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.client.OmniClient;
import com.linuxrouter.netcool.client.database.DbUtils;
import com.linuxrouter.netcool.log.AutomationLogAppender;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author lucas
 */
@Singleton
@LocalBean
@Startup
@ServerEndpoint("/loggerSocket")
public class LoggerSocket {

    private final Logger logger = Logger.getLogger(LoggerSocket.class);
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @EJB
    private OmniClient omni;

    @EJB
    private GsonConverter converter;
    
    @OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }

    /**
     * Envia mensagem xD
     *
     * @param msg
     */
    @OnMessage
    public void onMessage(String msg) {
        for (Session peer : peers) {
            try {
                peer.getBasicRemote().sendText(msg);
            } catch (IOException ex) {
                logger.error("Failed to write to logger :/");
            }
        }
    }

    /**
     * Register the logger to the websocket xD
     */
    @PostConstruct
    private void registerLoger() {
        String pattern = "[%5p] %d{dd-MMM-yyyy HH:mm:ss} (DB:Omni) - %m";
        AutomationLogAppender appender = new AutomationLogAppender(this);
        appender.setLayout(new PatternLayout(pattern));
        omni.getMyLogger().addAppender(appender);
        
        
        pattern = "[%5p] %d{dd-MMM-yyyy HH:mm:ss} (DbUtils:Omni) - %m";
        AutomationLogAppender dbUtilsAppender = new AutomationLogAppender(this);
        dbUtilsAppender.setLayout(new PatternLayout(pattern));
        
        DbUtils.getLogger().addAppender(dbUtilsAppender);
    }

}
