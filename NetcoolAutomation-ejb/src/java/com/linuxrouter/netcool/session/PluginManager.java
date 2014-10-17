/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.linuxrouter.netcool.dao.AutomationDao;
import com.linuxrouter.netcool.entitiy.AutomationPlugins;
import com.linuxrouter.netcool.plugin.AutomationPluginInterface;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

/**
 *
 * @author G0004218
 */
@Singleton
@LocalBean
@Startup
public class PluginManager {

    private HashMap<String, AutomationPluginInterface> plugins = new HashMap<>();
    private HashMap<String, Object> pluginsImpl = new HashMap<>();

    private final Logger logger = Logger.getLogger(PluginManager.class);

    @EJB
    private AutomationDao automationDao;

    public void registerEnabledPlugins() {
        List<AutomationPlugins> plug = automationDao.getEnabledPlugins();
        if (plug != null) {
            logger.debug("Got Plugins:" + plug.size());
            Long pluginStartTime = System.currentTimeMillis();
            for (AutomationPlugins plugin : plug) {
                try {
                    AutomationPluginInterface pluginImpl
                            = (AutomationPluginInterface) Class.forName(plugin.getPluginClass()).newInstance();
                    getPlugins().put(pluginImpl.getPluginAlias(), pluginImpl);
                    pluginsImpl.put(pluginImpl.getPluginAlias(),pluginImpl.getPluginImpl());
                            
                    logger.debug("Ok Registering new plugin with Alias: [" + pluginImpl.getPluginAlias() + "]");
                } catch (ClassNotFoundException pluginEx) {
                    logger.error("Failed to create plugin [Class Not Found]", pluginEx);
                } catch (InstantiationException ex) {
                    logger.error("Failed to create plugin [InstantiationException]", ex);
                } catch (IllegalAccessException ex) {
                    logger.error("Failed to create plugin [IllegalAccessException]", ex);
                }
            }
            Long pluginEndTime = System.currentTimeMillis();
            logger.debug("Plugin time was::: " + (pluginEndTime - pluginStartTime) + "[ms]");
        } else {
            logger.debug("No plugin Found");
        }

    }

    /**
     * @return the plugins
     */
    public HashMap<String, AutomationPluginInterface> getPlugins() {
        return plugins;
    }

    /**
     * @return the pluginsImpl
     */
    public HashMap<String, Object> getPluginsImpl() {
        return pluginsImpl;
    }

}
