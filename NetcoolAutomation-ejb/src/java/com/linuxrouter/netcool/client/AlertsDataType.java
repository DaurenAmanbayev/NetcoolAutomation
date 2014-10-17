/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.client;

import java.util.HashMap;

/**
 *
 * @author g0004218
 */
public class AlertsDataType {
    private final HashMap<String,Integer> fieldType = new HashMap<>();
    private String dbName = "";

    /**
     * @return the fieldType
     */
    public HashMap<String,Integer> getFieldType() {
        return fieldType;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
