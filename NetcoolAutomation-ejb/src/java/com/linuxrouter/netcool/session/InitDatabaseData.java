/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationUsers;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
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

        }else{
            logger.debug("User admin already in database..."); 
        }
    }
}
