package com.linuxrouter.netcool.client;

import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationConnection;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.entitiy.AutomationReaderFilter;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
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

    private final HashMap<String, ObjectPool<PoolableConnection>> connectionPool = new HashMap<>();
    private final HashMap<String, PoolingDataSource<PoolableConnection>> poolingDataSource = new HashMap<>();
    private final Logger logger = Logger.getLogger(OmniClient.class);

    @EJB
    private AutomationDao automationDao;

//    @PostConstruct
//    public void setupConnectionPool() {
//        logger.debug("Starting Netcool Automation");
//        Driver drv = new com.sybase.jdbc3.jdbc.SybDriver();
//        try {
//            DriverManager.registerDriver(drv);
//        } catch (SQLException ex) {
//            logger.error("Failed to create an instance of SybDriver...", ex);
//        }
//        String url = "jdbc:sybase:Tds:" + getDbHost() + ":" + getDbPort() + "/" + getDbName();
//        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, getDbUser(), getDbPass());
//        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
//        setConnectionPool(new GenericObjectPool<>(poolableConnectionFactory));
//        poolableConnectionFactory.setPool(getConnectionPool());
//        setPoolingDataSource(new PoolingDataSource<>(getConnectionPool()));
//
//    }
    private void setConnectionPools(String name, String url, String user, String pass) {
        logger.debug("Starting Netcool Automation Connection Pool");
        if (connectionPool.get(name) == null) {
            Driver drv = new com.sybase.jdbc3.jdbc.SybDriver();
            try {
                DriverManager.registerDriver(drv);
            } catch (SQLException ex) {
                logger.error("Failed to create an instance of SybDriver...", ex);
            }

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, user, pass);
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
            ObjectPool<PoolableConnection> cp = new GenericObjectPool<>(poolableConnectionFactory);
            poolableConnectionFactory.setPool(cp);
            PoolingDataSource<PoolableConnection> pds = new PoolingDataSource<>(cp);
            poolingDataSource.put(name, pds);
            connectionPool.put(name, cp);
            logger.debug("Configured: " + name + " At Omnit bus pool");
        } else {
            AutomationConnection con = automationDao.getConnectionByName(name);
            ObjectPool<PoolableConnection> pool = connectionPool.remove(name);
            PoolingDataSource<PoolableConnection> dst = poolingDataSource.remove(name);
            pool.close();
            if (con.getEnabled().equalsIgnoreCase("Y")) {
                setConnectionPools(name, url, user, pass);
                logger.debug("Reconfiguring...");
            } else {
                logger.debug("Connection Removed ..");
            }

        }
    }

    @Schedule(minute = "*", hour = "*")
    private void printConnectionPoolUsage() {
        for (String key : poolingDataSource.keySet()) {
            logger.debug(" Connection Pool [" + key + "] Active: " + connectionPool.get(key).getNumActive() + " Idle: " + connectionPool.get(key).getNumIdle()
            );
        }
    }

    @PreDestroy
    public void shutDownConnectionPool() {
        logger.debug("Stopping Netcool Automation");

    }

    public ArrayList<EventMap> executeQuery(String filter, String connName, AutomationReaderFilter readerFilter) {
        ArrayList<EventMap> list = new ArrayList<>();
        logger.debug("Executing Query on:" + connName);

        String sql = "select * from alerts.status where 1=1 and StateChange >  " + readerFilter.getStateChange() + " and " + filter + " order by StateChange ";
        logger.debug("SQL:::" + sql);
        try {

            Connection omniBusConnection = poolingDataSource.get(connName).getConnection();
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
            logger.error("Failed to execute sql", ex);
        }

        return list;
    }

    public void commitChangedEvents(HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents, String connName) {
        Long startTime = System.currentTimeMillis();
        Iterator it = changedEvents.entrySet().iterator();
        try {
            Connection con = poolingDataSource.get(connName).getConnection();
            Statement st = con.createStatement();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String serial = (String) pairs.getKey();
                ArrayList<HashMap<String, Object>> fieldsChanged = changedEvents.get(serial);
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
                serial= serial.replace("'", "''");
                String updateSql = "UPDATE alerts.status set " + StringUtils.join(campoValor, ", ") + " where Identifier = " + serial + ";";
                logger.debug("Query: " + updateSql);
                st.addBatch(updateSql);
            }
            st.executeBatch();
            st.close();
            con.close();
            Long enTime = System.currentTimeMillis();
            logger.debug("Commit Changed Events Took: " + (enTime - startTime) + " ms For: " + changedEvents.size() + " Events");
        } catch (SQLException ex) {
            logger.error("Failed to execute sql", ex);
        }
    }

    @PostConstruct
    public void configureConnections() {
        logger.debug("Configuring OMNI Bus Connection...");
        List<AutomationConnection> connections = automationDao.getEnabledConections();
        if (connections != null) {
            logger.debug("Enabled Connection Count: [" + connections.size() + "]");
            if (connections.size() > 0) {
                //lets configure the connections pools xD
                for (AutomationConnection con : connections) {
                    setConnectionPools(con.getConnectionName(), con.getJdbcUrl(), con.getUsername(), con.getPassword());
                }
            }
        } else {
            logger.debug("No Connection found...");
        }

        connections = automationDao.getDisabledConnection();
        if (connections != null) {
            if (connections.size() > 0) {
                //lets configure the connections pools xD
                for (AutomationConnection disCon : connections) {
                    if (connectionPool.get(disCon.getConnectionName()) != null) {
                        AutomationConnection con = automationDao.getConnectionByName(disCon.getConnectionName());
                        ObjectPool<PoolableConnection> pool = connectionPool.remove(disCon.getConnectionName());
                        PoolingDataSource<PoolableConnection> dst = poolingDataSource.remove(disCon.getConnectionName());
                        pool.close();
                        logger.debug("Removed Connection: " + disCon.getConnectionName());
                    }

                }
            }
        }
        //  

    }

    public PoolingDataSource<PoolableConnection> getPoolingConnectionByName(String name) {
        return this.poolingDataSource.get(name);
    }
}
