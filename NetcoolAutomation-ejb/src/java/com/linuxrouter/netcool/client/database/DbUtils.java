/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.client.database;

import com.linuxrouter.database.Configuration;
import com.linuxrouter.database.query.QueryDb;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 *
 * @author Lucas
 */
public class DbUtils {
    
    private static HashMap<String, DBConnectionData> dsPool = new HashMap<String, DBConnectionData>();
    private static Integer maxAtcive = 10;
    private static Integer maxIdle = 5;
    private static Integer minIdIdle = 5;
    private static QueryDb queries = new QueryDb();
    private static PoolMonitor monitor = null;
    private static Configuration configuration;
    public static DbUtils instance = DbUtils.initDbPool();
    public static Logger logger = Logger.getLogger(DbUtils.class);
    
    public static DbUtils initDbPool() {
        
        if (instance == null) {
            instance = new DbUtils();
            autoConfigure();
        }
        
        return instance;
    }

    /**
     * Auto configura as conexões com o DB
     */
    public static void autoConfigure() {
        configureDbConnections("database");
    }

    /**
     * Path to xml for Database Configuration...
     *
     * @param path
     */
    public static void configureDbConnections(String path) {
        
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith("xml") || name.endsWith("XML")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File configDir = new File(path);
        if (configDir.exists()) {
            File[] files = configDir.listFiles(filter);
            for (File configFile : files) {
                try {
                    //File configFile = new File("db-pool.xml");
                    if (configFile != null) {
                        getLogger().debug("db-pool.xml Loaded");
                        JAXBContext context = JAXBContext.newInstance(Configuration.class);
                        configuration = (Configuration) context.createUnmarshaller().unmarshal(configFile);
                        getLogger().debug("Found: " + configuration.getDbPool().size() + " Connections to Setup");
                        for (Configuration.DbPool pool : configuration.getDbPool()) {
                            DbUtils.setConnection(pool);
                        }
                    }
                } catch (NullPointerException ex) {
                    getLogger().error("No pool Found");
                } catch (JAXBException ex) {
                    getLogger().error("Invalid XML File Format", ex);
                }
            }
        } else {
            getLogger().error("Config Dir[" + path + "] Not Found...");
        }
    }

    /**
     * Criado por causa do bug.... pool de conexão não estava retornando as
     * conexoes depois de fechadas
     *
     * @param name
     * @return
     */
    public static BasicDataSource getSimpleDataSourceByName(String name) {
        //getLogger().debug("Calling Simple Data Source Method...");
        Iterator it = dsPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            String connectionName = (String) pairs.getKey();
            if (connectionName.equals(name)) {
                DBConnectionData data = dsPool.get(connectionName);
                BasicDataSource ds = new BasicDataSource();
                ds.setDriverClassName(data.getPool().getDriverClassName());
                ds.setUsername(data.getPool().getDbUser());
                ds.setPassword(data.getPool().getDbPass());
                ds.setUrl(data.getPool().getJdbcUrl().trim());
                return ds;
            }
            
        }
        getLogger().error("Connection name: [" + name + "] not found..");
        return null;
    }

    /**
     * Apenas para estatiscas e debug.. Printa a quantidade de conexões sendo
     * utilizadas pelos processos. Não se aplica ao método
     * getSimpleDataSourceByName, pois este retorna um ds que não vem do pool.
     */
    public static void printPoolUsage() {
        Iterator it = dsPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            String connectionName = (String) pairs.getKey();
            DBConnectionData data = dsPool.get(connectionName);
            getLogger().debug("\tConnection: [" + connectionName + "]: Active:> " + data.getConnectionPool().getObjectPool().getNumActive() + " Idle:> " + data.getConnectionPool().getObjectPool().getNumIdle());
        }
    }
    
    public static void shutDownDbPool() {
        Iterator it = dsPool.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            String connectionName = (String) pairs.getKey();
            DBConnectionData data = dsPool.get(connectionName);
            data.getConnectionPool().objectPool.close();
            getLogger().debug("\tConnection: [" + connectionName + "]: Active:> " + data.getConnectionPool().getObjectPool().getNumActive() + " Idle:> " + data.getConnectionPool().getObjectPool().getNumIdle());
        }
        logger.debug("Db Pool ShutDown xD");
    }

    /**
     * Para Debug, deverá ser removido de produção. Cria um thread para ficar
     * exibindo estatisticas
     */
    public static void startMonitor() {
        monitor = new PoolMonitor();
        Thread t = new Thread(monitor);
        t.start();
    }

    /**
     * Carrega as queries...com base na especificação do xsd do projeto..
     */
    private static void loadQueries() {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith("xml") || name.endsWith("XML")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File configDir = new File("query");
        File[] files = configDir.listFiles(filter);
        for (File configFile : files) {
            try {
                //File configFile = new File("db-pool.xml");
                if (configFile != null) {
                    getLogger().debug("Query  Loaded");
                    JAXBContext context = JAXBContext.newInstance(QueryDb.class);
                    queries = (QueryDb) context.createUnmarshaller().unmarshal(configFile);
                    for (QueryDb.GvtQuery q : queries.getGvtQuery()) {
                        getLogger().debug("Loaded Query :[" + q.getName() + "]");
                    }
                }
            } catch (NullPointerException ex) {
                getLogger().error("No pool Found");
            } catch (JAXBException ex) {
                getLogger().error("Invalid XML File Format", ex);
            }
        }
    }

    /**
     * Loucura.. desnecessário deverá ser removido no futuro... ou não.. ainda
     * estou pensando.
     *
     * @param name
     * @return
     * @throws QueryNotFoundException
     */
    public static QueryDb.GvtQuery getQueryByName(String name) throws QueryNotFoundException {
        if (queries.getGvtQuery().size() < 1) {
            loadQueries();
        }
        for (QueryDb.GvtQuery q : queries.getGvtQuery()) {
            if (q.getName().equals(name)) {
                return q;
            }
        }
        throw new QueryNotFoundException("Query Bot Found");
    }

    /**
     * Este ficou mais util dado um nome retorna a query..>
     *
     * @param name
     * @return
     * @throws QueryNotFoundException
     */
    public static String getQuerySqlByName(String name) throws QueryNotFoundException {
        if (queries.getGvtQuery().size() < 1) {
            loadQueries();
        }
        for (QueryDb.GvtQuery q : queries.getGvtQuery()) {
            if (q.getName().equals(name)) {
                return q.getSql();
            }
        }
        throw new QueryNotFoundException("Query Bot Found");
    }

    /**
     * Configura uma nova conexão em nosso pool.
     *
     * @param name Nome único da conexão
     * @param url URL para acesso ao db, não precisa da classe ele se vira com
     * isso
     * @param user Usuário para acesso ao db.
     * @param pass Senha para acesso ao DB
     */
    public static void setConnection(Configuration.DbPool pool) {
        if (getDsPool().get(pool.getPoolName()) == null) {
            getLogger().debug("Creating pooling connection [" + pool.getPoolName() + "]");
            LinuxRouterPoolConnection ds = setupDataSource(pool.getJdbcUrl().trim(), pool.getDbUser(), pool.getDbPass());
            DBConnectionData data = new DBConnectionData();
            data.setConnectionPool(ds);
            data.setPool(pool);
            getDsPool().put(pool.getPoolName(), data);
        } else {
            getLogger().debug("Connection :" + pool.getPoolName() + " already created.");
            getLogger().debug("Validanting connection");
            String sql = "SELECT 1 FROM DUAL";
            getLogger().debug("Tudo ok com a conexão: " + pool.getPoolName());
            try {
                Statement st = getDsPool().get(pool.getPoolName()).getConnectionPool().getPoolingDataSource().getConnection().createStatement();
                st.execute(sql);
                st.close();
                getDsPool().get(pool.getPoolName()).getConnectionPool().getPoolingDataSource().getConnection().close();
                
            } catch (SQLException ex) {
                getLogger().error(ex); //Loga o erro
                getLogger().error("Connection: " + pool.getPoolName() + " is invalid.."
                        + "Fixing connection :)");
                getDsPool().remove(pool.getPoolName());
                try {
                    getLogger().debug("Waiting 5 secs..");
                    Thread.sleep(5 * 1000);
                    getLogger().debug("Creating pooling connection [" + pool.getPoolName() + "]");
                } catch (InterruptedException ex1) {
                    java.util.logging.Logger.getLogger(DbUtils.class.getName()).log(Level.SEVERE, null, ex1);
                }
                setConnection(pool);//Recria a conexão.

            }
        }
    }

    /**
     * Retorna um ds do pool Este método apresentou problemas com retorno, ainda
     * estou estudando ele, me parece segura utilizar com jdk6 mas na 7 está com
     * bug
     *
     * @param name
     * @return
     * @throws Exception
     * @deprecated
     */
    public static PoolingDataSource getConnection(String name) throws Exception {
        getLogger().debug("Get connectin: [" + name + "]");
        printPoolUsage();
        if (getDsPool().get(name) != null) {
            setConnection(dsPool.get(name).getPool());
            return getDsPool().get(name).getConnectionPool().getPoolingDataSource();
        } else {
            throw new Exception("Connection Name not Set.");
        }
    }

    /**
     * Método que configura o pool de conexões.. mas está com bug e deve ser
     * revisado..
     *
     * @param url
     * @param user
     * @param password
     * @return
     */
    private static LinuxRouterPoolConnection setupDataSource(String url, String user, String password) {
        LinuxRouterPoolConnection con = new LinuxRouterPoolConnection();

//        GenericObjectPool.Config config = new GenericObjectPool.Config();
//        config.maxActive = getMaxAtcive();
//        config.maxIdle = 2;
//        config.minIdle = 1;
//        config.maxWait = 5000;
        //ObjectPool connectionPool = new GenericObjectPool(null, config);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url.trim(), user, password);
        
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        PoolingDataSource<PoolableConnection> poolingDataSource = new PoolingDataSource<>(connectionPool);
        
        getLogger().debug("Connection created!");
        con.setObjectPool(connectionPool);
        con.setPoolingDataSource(poolingDataSource);
        return con;
        
    }
    
    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(DbUtils.class);
        }
        return logger;
    }

    /**
     * @return the dsPool
     */
    public static HashMap<String, DBConnectionData> getDsPool() {
        return dsPool;
    }

    /**
     * @param aDsPool the dsPool to set
     */
    public static void setDsPool(HashMap<String, DBConnectionData> aDsPool) {
        dsPool = aDsPool;
    }

    /**
     * @return the maxAtcive
     */
    public static Integer getMaxAtcive() {
        return maxAtcive;
    }

    /**
     * @param aMaxAtcive the maxAtcive to set
     */
    public static void setMaxAtcive(Integer aMaxAtcive) {
        maxAtcive = aMaxAtcive;
    }

    /**
     * @return the maxIdle
     */
    public static Integer getMaxIdle() {
        return maxIdle;
    }

    /**
     * @param aMaxIdle the maxIdle to set
     */
    public static void setMaxIdle(Integer aMaxIdle) {
        maxIdle = aMaxIdle;
    }

    /**
     * @return the minIdIdle
     */
    public static Integer getMinIdIdle() {
        return minIdIdle;
    }

    /**
     * @param aMinIdIdle the minIdIdle to set
     */
    public static void setMinIdIdle(Integer aMinIdIdle) {
        minIdIdle = aMinIdIdle;
    }

    /**
     * Classe interna que monitora o pool
     */
    private static class PoolMonitor implements Runnable {
        
        private final Boolean isRunning = true;
        private final Logger logger = Logger.getLogger("PoolMonitor");
        
        @Override
        public void run() {
            while (isRunning) {
                try {
                    logger.debug("Running Monitor...");
                    Iterator it = dsPool.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
                        String connectionName = (String) pairs.getKey();
                        DBConnectionData data = dsPool.get(connectionName);
                        logger.debug("Connection: [" + connectionName + "]: Active:> " + data.getConnectionPool().getObjectPool().getNumActive());
                    }
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException ex) {
                    
                }
            }
        }
        
    }
    
    public static class LinuxRouterPoolConnection {
        
        private PoolingDataSource poolingDataSource;
        private ObjectPool objectPool;

        /**
         * @return the poolingDataSource
         */
        public PoolingDataSource getPoolingDataSource() {
            return poolingDataSource;
        }

        /**
         * @param poolingDataSource the poolingDataSource to set
         */
        public void setPoolingDataSource(PoolingDataSource poolingDataSource) {
            this.poolingDataSource = poolingDataSource;
        }

        /**
         * @return the objectPool
         */
        public ObjectPool getObjectPool() {
            return objectPool;
        }

        /**
         * @param objectPool the objectPool to set
         */
        public void setObjectPool(ObjectPool objectPool) {
            this.objectPool = objectPool;
        }
    }
}
