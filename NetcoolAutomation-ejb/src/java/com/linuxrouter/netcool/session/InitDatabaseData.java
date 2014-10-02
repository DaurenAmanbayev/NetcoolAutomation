/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.database.Configuration;
import com.linuxrouter.netcool.client.database.DbUtils;
import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationUsers;
import com.linuxrouter.netcool.entitiy.DbConnections;
import java.math.BigInteger;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */
@Singleton
@LocalBean
@Startup
public class InitDatabaseData {

    private final Logger logger = Logger.getLogger(InitDatabaseData.class);
    @EJB
    private AutomationDao automationDao;

    @EJB
    private UtilSession utilSession;

    @PostConstruct
    private void initDbData() {
        AutomationUsers adminUser = automationDao.getUserByLogin("admin");
        if (adminUser == null) {
            logger.debug("Admin user does not exists... creating it...");
            adminUser = new AutomationUsers();
            adminUser.setEmail("admin@admin.com");
            adminUser.setLogin("admin");
            adminUser.setPassword(utilSession.getMd5HashFromString("admin"));
            adminUser.setEnabled("Y");
            adminUser.setName("Admin User");
            automationDao.saveUser(adminUser);

        } else {
            logger.debug("User admin already in database...");
        }
        init3dPartyDbConnections();
    }

    private void init3dPartyDbConnections() {
        logger.debug("Configuring Connection pools..");
        List<DbConnections> connections = automationDao.getAllDbConnections();
        for (DbConnections connection : connections) {
            //Dbp
            Configuration.DbPool pool = new Configuration.DbPool();
            pool.setPoolName(connection.getConnectionName());
            pool.setDbPass(connection.getConnectionPassword());
            pool.setDbUser(connection.getConnectionUser());
            pool.setJdbcUrl(connection.getConnectionUrl());
            pool.setPoolSize(new BigInteger("5"));
            DbUtils.setConnection(pool);
        }
    }

    @Schedule(minute = "*", hour = "*")
    private void printConnectionPoolUsage() {
        DbUtils.printPoolUsage();
    }
}
