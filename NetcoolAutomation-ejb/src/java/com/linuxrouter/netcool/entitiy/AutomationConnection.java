/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.entitiy;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author lucas
 */
@Entity
@Table(name = "AUTOMATION_CONNECTION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutomationConnection.findAll", query = "SELECT a FROM AutomationConnection a"),
    @NamedQuery(name = "AutomationConnection.findByConnectionName", query = "SELECT a FROM AutomationConnection a WHERE a.connectionName = :connectionName"),
    @NamedQuery(name = "AutomationConnection.findByUsername", query = "SELECT a FROM AutomationConnection a WHERE a.username = :username"),
    @NamedQuery(name = "AutomationConnection.findByPassword", query = "SELECT a FROM AutomationConnection a WHERE a.password = :password"),
    @NamedQuery(name = "AutomationConnection.findByJdbcUrl", query = "SELECT a FROM AutomationConnection a WHERE a.jdbcUrl = :jdbcUrl"),
    @NamedQuery(name = "AutomationConnection.findByEnabled", query = "SELECT a FROM AutomationConnection a WHERE a.enabled = :enabled")})
public class AutomationConnection implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "CONNECTION_NAME")
    private String connectionName;
    @Size(max = 50)
    @Column(name = "USERNAME")
    private String username;
    @Size(max = 255)
    @Column(name = "PASSWORD")
    private String password;
    @Size(max = 20)
    @Column(name = "JDBC_URL")
    private String jdbcUrl;
    @Column(name = "ENABLED")
    private String enabled;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "connectionName")
    private List<AutomationReader> automationReaderList;

    public AutomationConnection() {
    }

    public AutomationConnection(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @XmlTransient
    public List<AutomationReader> getAutomationReaderList() {
        return automationReaderList;
    }

    public void setAutomationReaderList(List<AutomationReader> automationReaderList) {
        this.automationReaderList = automationReaderList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (connectionName != null ? connectionName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutomationConnection)) {
            return false;
        }
        AutomationConnection other = (AutomationConnection) object;
        if ((this.connectionName == null && other.connectionName != null) || (this.connectionName != null && !this.connectionName.equals(other.connectionName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.AutomationConnection[ connectionName=" + connectionName + " ]";
    }
    
}
