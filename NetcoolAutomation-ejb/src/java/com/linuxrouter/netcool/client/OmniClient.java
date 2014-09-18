package com.linuxrouter.netcool.client;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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

    public ArrayList<HashMap<String, Object>> executeQuery(String sql) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        try {
            Connection omniBusConnection = poolingDataSource.getConnection();
            Statement st = omniBusConnection.createStatement();
            Long startTime = System.currentTimeMillis();
            ResultSet rs = st.executeQuery(sql);
            Integer resultCount = 0;
            ResultSetMetaData md = rs.getMetaData();
//            for (int x = 1; x < md.getColumnCount(); x++) {
//                logger.debug("ColName: " + md.getColumnName(x) + " Type: " + md.getColumnType(x));
//            }
            while (rs.next()) {
                resultCount++;
                HashMap<String, Object> data = new HashMap<>();
                for (int x = 1; x < md.getColumnCount(); x++) {
                    data.put(md.getColumnName(x), rs.getString(x));

                }
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
           
}
