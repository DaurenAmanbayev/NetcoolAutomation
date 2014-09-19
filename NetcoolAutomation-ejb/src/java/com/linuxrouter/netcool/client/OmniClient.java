package com.linuxrouter.netcool.client;

import groovy.util.ObservableMap;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * Singleton class that represents the OmniBus DB Client. This is done in this
 * way so we can handle the db pool and better have control in it...
 *
 * @author lucas
 */
@Singleton
@LocalBean
@Startup
public class OmniClient {
    
    private final String dbHost = "192.168.0.201";
    private final String dbUser = "root";
    private final String dbPass = "omni12@#";
    private final String dbName = "alerts";
    private final String dbPort = "4100";
    private ObjectPool<PoolableConnection> connectionPool = null;
    private PoolingDataSource<PoolableConnection> poolingDataSource = null;
    private final Logger logger = Logger.getLogger(OmniClient.class);
    
    @PostConstruct
    public void setupConnectionPool() {
        logger.debug("Starting Netcool Automation");
        Driver drv = new com.sybase.jdbc3.jdbc.SybDriver();
        try {
            DriverManager.registerDriver(drv);
        } catch (SQLException ex) {
            logger.error("Failed to create an instance of SybDriver...", ex);
        }
        String url = "jdbc:sybase:Tds:" + dbHost + ":" + dbPort + "/" + dbName;
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, dbUser, dbPass);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        poolingDataSource = new PoolingDataSource<>(connectionPool);
        
    }
    
    @Schedule(minute = "*", hour = "*")
    private void printConnectionPoolUsage() {
        logger.debug("OMNIBus Connection Active: " + connectionPool.getNumActive() + " Idle: " + connectionPool.getNumIdle());
    }
    
    @PreDestroy
    public void shutDownConnectionPool() {
        logger.debug("Stopping Netcool Automation");
        
    }
    
    public PoolingDataSource<PoolableConnection> getOmniBusDataSource() {
        return poolingDataSource;
    }
    
    public ArrayList<EventMap> executeQuery(String sql) {
        ArrayList<EventMap> list = new ArrayList<>();
        try {
            Connection omniBusConnection = poolingDataSource.getConnection();
            Statement st = omniBusConnection.createStatement();
            Long startTime = System.currentTimeMillis();
            ResultSet rs = st.executeQuery(sql);
            Integer resultCount = 0;
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                resultCount++;
                EventMap data = new EventMap();
                for (int x = 1; x < md.getColumnCount(); x++) {
                    data.put(md.getColumnName(x), rs.getString(x));
                    
                }
                data.addPropertyChangeListener(data);// adiciona depois para não zoar o processo..
                list.add(data);
            }
            
            rs.close();
            
            st.close();
            omniBusConnection.close();
            Long endTime = System.currentTimeMillis();
            logger.debug("Done Query Time Took: " + (endTime - startTime) + " ms");
            logger.debug("Query : " + sql);
            logger.debug("Result Count : " + resultCount);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(OmniClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    public void commitChangedEvents(HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents) {
        Long startTime = System.currentTimeMillis();
        Iterator it = changedEvents.entrySet().iterator();
        try {
            Connection con = poolingDataSource.getConnection();
            Statement st = con.createStatement();
            
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String serial = (String) pairs.getKey();
                ArrayList<HashMap<String, Object>> fieldsChanged = changedEvents.get(serial);
                //logger.debug("Changed Identifier found..:[" + identifier + "] Fields:Size: " + fieldsChanged.size());
                ArrayList<String> campoValor = new ArrayList<>();
                for (HashMap<String, Object> h : fieldsChanged) {
                    for (String key : h.keySet()) {
                        String field = key;
                        Object value = h.get(key);
                        if (value instanceof Integer) {
                            Integer intValue = (Integer) value;
                            campoValor.add(field + " = " + intValue + "");
                        } else {
                            campoValor.add(field + " = '" + value.toString() + "'");
                        }
                    }
                }
                
                String updateSql = "UPDATE alerts.status set " + StringUtils.join(campoValor, ", ") + " where Serial = " + serial + ";";
                //logger.debug("Query: " + updateSql);
                st.addBatch(updateSql);
            }
            st.executeBatch();
            st.close();
            con.close();
            Long enTime = System.currentTimeMillis();
            logger.debug("Commit Changed Events Took: " + (enTime - startTime) + " ms For: " +changedEvents.size()  + " Events" );
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(OmniClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
