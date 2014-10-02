/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.client.database;

import com.linuxrouter.database.Configuration;





/**
 *
 * @author G0004218
 */
public class DBConnectionData {

    private Configuration.DbPool pool;
    private DbUtils.LinuxRouterPoolConnection connectionPool;

    /**
     * @return the pool
     */
    public Configuration.DbPool getPool() {
        return pool;
    }

    /**
     * @param pool the pool to set
     */
    public void setPool(Configuration.DbPool pool) {
        this.pool = pool;
    }

    /**
     * @return the connectionPool
     */
    public DbUtils.LinuxRouterPoolConnection getConnectionPool() {
        return connectionPool;
    }

    /**
     * @param connectionPool the connectionPool to set
     */
    public void setConnectionPool(DbUtils.LinuxRouterPoolConnection connectionPool) {
        this.connectionPool = connectionPool;
    }


}
