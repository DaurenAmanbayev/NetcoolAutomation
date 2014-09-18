/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 *
 * @author lucas
 */
public class DbPoolTest {

    public static void main(String[] args) {
        String host = "192.168.0.201";
        String port = "4100";
        String dbName = "alerts";
        String url = "jdbc:sybase:Tds:" + host + ":" + port + "/" + dbName;
        Driver drv = new com.sybase.jdbc3.jdbc.SybDriver();
        try {
            DriverManager.registerDriver(drv);
        } catch (SQLException ex) {
            Logger.getLogger(DbPoolTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, "root", "omni12@#");
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        PoolingDataSource<PoolableConnection> poolingDataSource = new PoolingDataSource<>(connectionPool);
        try {
            Connection con  = poolingDataSource.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from alerts.status");
            int x=0;
            while(rs.next()){
                //System.out.println(":::" + rs.getString(1));
                x++;
            }
            System.out.println("::::::" + x);
        } catch (SQLException ex) {
            Logger.getLogger(DbPoolTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
