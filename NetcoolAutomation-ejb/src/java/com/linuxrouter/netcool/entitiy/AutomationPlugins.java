/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.entitiy;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G0004218
 */
@Entity
@Table(name = "AUTOMATION_PLUGINS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutomationPlugins.findAll", query = "SELECT a FROM AutomationPlugins a"),
    @NamedQuery(name = "AutomationPlugins.findByPluginName", query = "SELECT a FROM AutomationPlugins a WHERE a.pluginName = :pluginName"),
    @NamedQuery(name = "AutomationPlugins.findByEnabled", query = "SELECT a FROM AutomationPlugins a WHERE a.enabled = :enabled"),
    @NamedQuery(name = "AutomationPlugins.findByPluginClass", query = "SELECT a FROM AutomationPlugins a WHERE a.pluginClass = :pluginClass")})
public class AutomationPlugins implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "PLUGIN_NAME")
    private String pluginName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "ENABLED")
    private String enabled;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "PLUGIN_CLASS")
    private String pluginClass;

    public AutomationPlugins() {
    }

    public AutomationPlugins(String pluginName) {
        this.pluginName = pluginName;
    }

    public AutomationPlugins(String pluginName, String enabled, String pluginClass) {
        this.pluginName = pluginName;
        this.enabled = enabled;
        this.pluginClass = pluginClass;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pluginName != null ? pluginName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutomationPlugins)) {
            return false;
        }
        AutomationPlugins other = (AutomationPlugins) object;
        if ((this.pluginName == null && other.pluginName != null) || (this.pluginName != null && !this.pluginName.equals(other.pluginName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.AutomationPlugins[ pluginName=" + pluginName + " ]";
    }
    
}
