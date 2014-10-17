/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.client.database.DbUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */
@Singleton
@LocalBean
public class QueryUtils {

    private final Logger logger = Logger.getLogger(QueryUtils.class);
    private HashMap<String, Connection> connectionMap = new HashMap<>();

    
   
    
    public ArrayList<HashMap<String, Object>> executeQuery(String dbName, String sql) {
        Long start = System.currentTimeMillis();
        ArrayList<HashMap<String, Object>> result = new ArrayList<>();
        HashMap<Integer, String> colTypes = new HashMap<Integer, String>();
        HashMap<Integer, String> colNames = new HashMap<Integer, String>();
        try {
            //connection caching...
            Connection con = null;
            if (connectionMap.get(dbName) == null) {
                BasicDataSource ds = DbUtils.getSimpleDataSourceByName(dbName);
                con = ds.getConnection();
                connectionMap.put(dbName, con);
            } else {
                con = connectionMap.get(dbName);

            }

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                colTypes.put(i, metaData.getColumnTypeName(i));
                colNames.put(i, metaData.getColumnLabel(i));
            }
            while (rs.next()) {
                HashMap<String, Object> dado = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    dado.put(colNames.get(i), rs.getObject(i));

                }
                result.add(dado);
            }
            rs.close();
            st.close();
            //con.close();
            Long end = System.currentTimeMillis();
            //logger.debug("Query on external DB took: " + (end - start) + "ms");
        } catch (SQLException ex) {
            logger.error("Erro ao executar query:", ex);
        }
        return result;
    }

    public Boolean executeUpdate(String dbName, String sql) {
        Long start = System.currentTimeMillis();
        try {
            //connection caching...
            Connection con = null;
            if (connectionMap.get(dbName) == null) {
                BasicDataSource ds = DbUtils.getSimpleDataSourceByName(dbName);
                con = ds.getConnection();
                connectionMap.put(dbName, con);
            } else {
                con = connectionMap.get(dbName);

            }
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException ex) {
            logger.error("Erro ao executar query:", ex);
            return false;
        }
        Long end = System.currentTimeMillis();
        return true;
    }

}
