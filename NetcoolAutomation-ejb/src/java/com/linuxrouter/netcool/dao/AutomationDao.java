/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.dao;

import com.linuxrouter.netcool.entitiy.AutomationUsers;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author lucas
 */
@Stateless
@LocalBean
public class AutomationDao {

    @PersistenceContext(name = "NetcoolAutomation-ejbPU")
    private EntityManager em;

    public AutomationUsers getUserByLogin(String login) {
        return null;
    }
}
