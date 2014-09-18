/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.client;

import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;

/**
 *
 * @author lucas
 */
public class OmniBusConnectionPool {

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
