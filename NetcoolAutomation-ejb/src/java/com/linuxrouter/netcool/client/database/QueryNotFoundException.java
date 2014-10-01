/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.client.database;

/**
 *
 * @author G0004218
 */
public class QueryNotFoundException extends Exception {

    /**
     * Caso a query não esteja encontrada...
     *
     * @param msg
     */
    public QueryNotFoundException(String msg) {
        super(msg);
    }
}
