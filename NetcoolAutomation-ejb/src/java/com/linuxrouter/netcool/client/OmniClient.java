package com.linuxrouter.netcool.client;

import com.linuxrouter.netcool.configuration.AutomationConstants;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationConnection;
import com.linuxrouter.netcool.entitiy.AutomationReader;
import com.linuxrouter.netcool.entitiy.AutomationReaderFilter;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import net.sourceforge.jtds.jdbc.Driver;

/**
 * Singleton class that represents the OmniBus DB Client. This is done in this
 * way so we can handle the db pool and better have control in it...
 *
 * @todo: Refatorar um dia
 * @author lucas
 */
@Singleton
@LocalBean
@Startup
public class OmniClient {

    private final HashMap<String, ObjectPool<PoolableConnection>> connectionPool = new HashMap<>();
    private final HashMap<String, PoolingDataSource<PoolableConnection>> poolingDataSource = new HashMap<>();
    private final HashMap<AutomationReader, Connection> activeReaderConnection = new HashMap<>();
    private final HashMap<String, AlertsDataType> dataFieldTypes = new HashMap<>();
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
    public Logger getMyLogger() {
        return logger;
    }

    private void getDataMap(String connectionName, PoolingDataSource<PoolableConnection> pds) {
        Connection omniBusConnection = null;
        try {
            omniBusConnection = omniBusConnection = pds.getConnection();
            String sql = "select * from catalog.columns where TableName = 'status' and DatabaseName = 'alerts'";
            Statement st = omniBusConnection.createStatement();
            logger.debug("Going go execute : " + sql);

            ResultSet rs = st.executeQuery(sql);
            logger.debug("Done");

            AlertsDataType types = new AlertsDataType();
            while (rs.next()) {
                // logger.debug("Fetching result...");               
                //types.getFieldType().put(rs.getString("ColumnName"), rs.getInt("DataType"));
                //logger.debug("Metadata DB:{" + connectionName + "}: [" + rs.getString("ColumnName") + "]:= " + rs.getInt("DataType"));
                String columnName = rs.getString("ColumnName");
                Integer colType = rs.getInt("DataType");
                types.getFieldType().put(columnName, colType);
                //logger.debug(columnName);
            }
            dataFieldTypes.put(connectionName, types);

        } catch (SQLException ex) {
            logger.error("Failed to get Status datamap.");
        } finally {
            try {
                omniBusConnection.close();
            } catch (SQLException ex) {
                logger.error("Main Failure closing db connection this is super bad.");
            } catch (Exception ex) {
                logger.error("Generic Exception");
            }
        }

    }

    private void setConnectionPools(String name, String url, String user, String pass) throws OmniException {
        logger.debug("Starting Netcool Automation Connection Pool");
        if (connectionPool.get(name) == null) {

            java.sql.Driver drv = null;

            if (url.contains("jtds")) {
                //jdbc:jtds:sybase://NCO:4100;TDS=5.0;charset=iso_1
                drv = new Driver();
            } else {
                drv = new com.sybase.jdbc3.jdbc.SybDriver();
            }
            try {
                DriverManager.registerDriver(drv);
            } catch (SQLException ex) {
                logger.error("Failed to create an instance of SybDriver...", ex);
            }

            Properties props = new Properties();
            props.put("REPEAT_READ", "false");
            props.put("USE_METADATA", "true");
            props.put("JCONNECT_VERSION", "6");
            props.put("CHARSET", "iso_1");
            props.put("APPLICATIONNAME", AutomationConstants.AUTOMATIONNAME);
            props.put("URL", url);
            props.put("Password", pass);
            props.put("User", user);

            //ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, user, pass);
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, props);

            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
            ObjectPool<PoolableConnection> cp = new GenericObjectPool<>(poolableConnectionFactory);
            poolableConnectionFactory.setPool(cp);
            PoolingDataSource<PoolableConnection> pds = new PoolingDataSource<>(cp);

            getDataMap(name, pds);

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
        logger.debug("Omnibus Connections Size: " + poolingDataSource.size());
        for (String key : poolingDataSource.keySet()) {
            logger.debug(" Connection Pool [" + key + "] Active: " + connectionPool.get(key).getNumActive() + " Idle: " + connectionPool.get(key).getNumIdle()
            );
        }
    }

    @PreDestroy
    public void shutDownConnectionPool() {
        logger.debug("Stopping Netcool Automation");

    }

    /**
     * Executa uma query no omnibus
     *
     * @param sql
     * @param filterName
     * @return
     */
    public ArrayList<EventMap> runOmniSql(String sql, String filterName) {
        logger.debug("Running User SQL:::" + sql);
        Connection omniBusConnection = null;
        AutomationReaderFilter readerFilter = automationDao.getFilterByName(filterName);
        ArrayList<EventMap> list = new ArrayList<>();
        String connName = readerFilter.getReaderName().getConnectionName().getConnectionName();
        try {
            if (activeReaderConnection.get(readerFilter.getReaderName()) == null) {
                //é null
                omniBusConnection = poolingDataSource.get(connName).getConnection();
                activeReaderConnection.put(readerFilter.getReaderName(), omniBusConnection);
                logger.debug("Connection Created for User Query!");
            } else {
                omniBusConnection = activeReaderConnection.get(readerFilter.getReaderName());
            }
        } catch (Exception ex) {
            logger.error("General SQL Error in SQL:", ex);
        }

        AlertsDataType types = null;
        if (dataFieldTypes.get(connName) != null) {
            types = dataFieldTypes.get(connName);
            logger.debug("Found Types..");
        }

        if (omniBusConnection == null) {
            logger.debug("The connection is for somehow Null :(");
        }
        try {
            Statement st = omniBusConnection.createStatement();
            Long startTime = System.currentTimeMillis();
            ResultSet rs = st.executeQuery(sql);
            Integer resultCount = 0;
            ResultSetMetaData md = rs.getMetaData();
            logger.debug("Collumn count for user is: " + md.getColumnCount());
            while (rs.next()) {
                resultCount++;
                EventMap data = new EventMap();
                /**
                 * Trata os tipos de campo...
                 */
                for (int x = 1; x <= md.getColumnCount(); x++) {
                    data.put(md.getColumnName(x), rs.getString(x));
                }
                data.addPropertyChangeListener(data);// adiciona depois para não zoar o processo..
                list.add(data);
            }

            rs.close();

            st.close();
            //omniBusConnection.close();
            Long endTime = System.currentTimeMillis();
//            logger.debug("Done Query Time Took: " + (endTime - startTime) + " ms");
//            logger.debug("Query : " + sql);
            logger.debug("Result Count(user) : " + resultCount);
        } catch (SQLException ex) {
            try {
                activeReaderConnection.remove(readerFilter.getReaderName());
                omniBusConnection.close();
                logger.debug("Connection was closed due to errors...");

            } catch (SQLException ex1) {
                logger.error("Main Failure error1");
            }
            logger.error("Failed to execute sql", ex);
        }
        return list;
    }

    public void executeUpdate(String sql, String filterName) {
        logger.debug("Running User SQL for Update:::" + sql);
        Connection omniBusConnection = null;
        AutomationReaderFilter readerFilter = automationDao.getFilterByName(filterName);

        String connName = readerFilter.getReaderName().getConnectionName().getConnectionName();
        try {
            if (activeReaderConnection.get(readerFilter.getReaderName()) == null) {
                //é null
                omniBusConnection = poolingDataSource.get(connName).getConnection();
                activeReaderConnection.put(readerFilter.getReaderName(), omniBusConnection);
                logger.debug("Connection Created for User Query!");
            } else {
                omniBusConnection = activeReaderConnection.get(readerFilter.getReaderName());
            }
        } catch (Exception ex) {
            logger.error("General SQL Error in SQL:", ex);
        }

        AlertsDataType types = null;
        if (dataFieldTypes.get(connName) != null) {
            types = dataFieldTypes.get(connName);
        }

        if (omniBusConnection == null) {
            logger.debug("The connection is for somehow Null :(");
        }
        try {
            Statement st = omniBusConnection.createStatement();
            Long startTime = System.currentTimeMillis();
            st.executeUpdate(sql);
            st.close();
            logger.debug("update Feito xD");
        } catch (SQLException ex) {
            try {
                activeReaderConnection.remove(readerFilter.getReaderName());
                omniBusConnection.close();
                logger.debug("Connection was closed due to errors...");

            } catch (SQLException ex1) {
                logger.error("Main Failure error1");
            }
            logger.error("Failed to execute sql", ex);
        }

    }

    /**
     * Executes a Query in the Omnibus object server.
     *
     * @param filter
     * @param connName
     * @param readerFilter
     * @param hasState
     * @return
     */
    public ArrayList<EventMap> executeQuery(String filter, String connName, AutomationReaderFilter readerFilter, Boolean hasState) {
        ArrayList<EventMap> list = new ArrayList<>();
        //logger.debug("Executing Query on:" + connName);

        String sql = "select * from alerts.status where 1=1";
        if (hasState) {
            sql += " and StateChange >  " + readerFilter.getStateChange() + " and " + filter + " order by StateChange ";
        } else {
            sql += " and " + filter + " order by StateChange ";
        }
        //logger.debug("SQL:::" + sql);Connection omniBusConnection = 
        Connection omniBusConnection = null;

        try {
            if (activeReaderConnection.get(readerFilter.getReaderName()) == null) {
                //é null
                omniBusConnection = poolingDataSource.get(connName).getConnection();
                activeReaderConnection.put(readerFilter.getReaderName(), omniBusConnection);
                logger.debug("Connection Created for Query!");
            } else {
                omniBusConnection = activeReaderConnection.get(readerFilter.getReaderName());
            }
            AlertsDataType types = null;
            if (dataFieldTypes.get(connName) != null) {
                types = dataFieldTypes.get(connName);
                logger.debug("Found Types..");
            }

            if (omniBusConnection == null) {
                logger.debug("The connection is for somehow Null :(");
            }

            Statement st = omniBusConnection.createStatement();
            Long startTime = System.currentTimeMillis();
            ResultSet rs = st.executeQuery(sql);
            Integer resultCount = 0;
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                resultCount++;
                EventMap data = new EventMap();
                /**
                 * Trata os tipos de campo...
                 */
                for (int x = 1; x <= md.getColumnCount(); x++) {
                    if (types == null) {
                        data.put(md.getColumnName(x), rs.getString(x));
                    } else {
                        if (types.getFieldType().get(md.getColumnName(x)) != null) {
                            if (types.getFieldType().get(md.getColumnName(x)) == 2) {
                                data.put(md.getColumnName(x), rs.getString(x));
                            } else {
                                data.put(md.getColumnName(x), rs.getInt(x));
                            }
                        } else {
                            data.put(md.getColumnName(x), rs.getString(x));
                        }
                    }

                }
                data.addPropertyChangeListener(data);// adiciona depois para não zoar o processo..
                list.add(data);
            }
            rs.close();

            st.close();
            //omniBusConnection.close();
            Long endTime = System.currentTimeMillis();
//            logger.debug("Done Query Time Took: " + (endTime - startTime) + " ms");
//            logger.debug("Query : " + sql);
            logger.debug("Result Count : [" + resultCount + "] For Query: " + sql);
        } catch (SQLException ex) {
            try {
                activeReaderConnection.remove(readerFilter.getReaderName());
                omniBusConnection.close();
                logger.debug("Connection was closed due to errors...");

            } catch (SQLException ex1) {
                logger.error("Main Failure error1");
            }
            logger.error("Failed to execute sql", ex);
//            try {
//                activeReaderConnection.remove(readerFilter.getReaderName());
//                omniBusConnection.close();
//            } catch (Exception ex1) {
//                logger.error("Main Failure error2");
//            }
        } finally {

        }

        return list;
    }

    /**
     * Save data to omnibus
     *
     * @param changedEvents
     * @param reader
     */
    public void commitChangedEvents(HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents, AutomationReader reader) {
        Long startTime = System.currentTimeMillis();
        Iterator it = changedEvents.entrySet().iterator();
        Connection omniBusConnection = null;
        try {
            if (activeReaderConnection.get(reader) == null) {
                //é null
                omniBusConnection = poolingDataSource.get(reader.getConnectionName().getConnectionName()).getConnection();
                activeReaderConnection.put(reader, omniBusConnection);
                logger.debug("Connection Created for Update!");
            } else {
                omniBusConnection = activeReaderConnection.get(reader);
            }
            Statement st = omniBusConnection.createStatement();
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
                serial = serial.replace("'", "''");
                //logger.debug("Identifier Is: [" + serial + "]");
                String updateSql = "UPDATE alerts.status set " + StringUtils.join(campoValor, ", ") + " where Serial = " + serial + ";";
                // logger.debug("Query: " + updateSql);
                st.addBatch(updateSql);
            }
            st.executeBatch();
            st.close();
            //omniBusConnection.close();
            Long enTime = System.currentTimeMillis();
            logger.debug("Commit Changed Events Took: " + (enTime - startTime) + " ms For: " + changedEvents.size() + " Events");
        } catch (SQLException ex) {
            logger.error("Failed to execute sql2", ex);
            try {
                activeReaderConnection.remove(reader);
                omniBusConnection.close();
            } catch (Exception ex1) {
                logger.error("Main Failure error2");
            }
        } finally {

        }
    }

    @PostConstruct
    public void configureConnections() {
//        AutomationLogAppender appender = new AutomationLogAppender(loggerSocket);
//        String pattern = "[%5p] %d{dd-MMM-yyyy HH:mm:ss} (DB:Omni) - %m%n";
//        appender.setLayout(new PatternLayout(pattern));
        // logger.addAppender(appender);
        logger.debug("Configuring OMNI Bus Connection...");
        List<AutomationConnection> connections = automationDao.getEnabledConections();
        if (connections != null) {
            logger.debug("Enabled Connection Count: [" + connections.size() + "]");
            if (connections.size() > 0) {
                //lets configure the connections pools xD
                for (AutomationConnection con : connections) {
                    try {
                        setConnectionPools(con.getConnectionName(), con.getJdbcUrl(), con.getUsername(), con.getPassword());
                    } catch (OmniException ex) {
                        logger.error("Failed to configure Connection Pooling", ex);

                        con.setEnabled("E");
                        automationDao.saveConnection(con);

                    }
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
                        for (AutomationReader r : disCon.getAutomationReaderList()) {
                            if (activeReaderConnection.get(r) != null) {
                                Connection connection = activeReaderConnection.get(r);
                                try {
                                    connection.close();
                                } catch (SQLException ex) {
                                    logger.error("Failed to close connection pool :/ this is very bad", ex);
                                }
                            } else {
                                logger.debug("Reader doesnt have a connection associated with.");
                            }
                        }
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
