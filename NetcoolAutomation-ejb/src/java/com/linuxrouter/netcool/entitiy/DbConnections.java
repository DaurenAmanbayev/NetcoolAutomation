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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lucas
 */
@Entity
@Table(name = "DB_CONNECTIONS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DbConnections.findAll", query = "SELECT d FROM DbConnections d"),
    @NamedQuery(name = "DbConnections.findByConnectionName", query = "SELECT d FROM DbConnections d WHERE d.connectionName = :connectionName"),
    @NamedQuery(name = "DbConnections.findByConnectionUrl", query = "SELECT d FROM DbConnections d WHERE d.connectionUrl = :connectionUrl"),
    @NamedQuery(name = "DbConnections.findByConnectionUser", query = "SELECT d FROM DbConnections d WHERE d.connectionUser = :connectionUser"),
    @NamedQuery(name = "DbConnections.findByConnectionPassword", query = "SELECT d FROM DbConnections d WHERE d.connectionPassword = :connectionPassword"),
    @NamedQuery(name = "DbConnections.findByConnectionEnabled", query = "SELECT d FROM DbConnections d WHERE d.connectionEnabled = :connectionEnabled")})
public class DbConnections implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "CONNECTION_NAME")
    private String connectionName;
    @Size(max = 255)
    @Column(name = "CONNECTION_URL")
    private String connectionUrl;
    @Size(max = 50)
    @Column(name = "CONNECTION_USER")
    private String connectionUser;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "CONNECTION_PASSWORD")
    private String connectionPassword;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "CONNECTION_ENABLED")
    private String connectionEnabled;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "CONNECTION_PROPERTIES")
    private String connectionProperties;

    public DbConnections() {
    }

    public DbConnections(String connectionName) {
        this.connectionName = connectionName;
    }

    public DbConnections(String connectionName, String connectionPassword, String connectionEnabled, String connectionProperties) {
        this.connectionName = connectionName;
        this.connectionPassword = connectionPassword;
        this.connectionEnabled = connectionEnabled;
        this.connectionProperties = connectionProperties;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getConnectionUser() {
        return connectionUser;
    }

    public void setConnectionUser(String connectionUser) {
        this.connectionUser = connectionUser;
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionEnabled() {
        return connectionEnabled;
    }

    public void setConnectionEnabled(String connectionEnabled) {
        this.connectionEnabled = connectionEnabled;
    }

    public String getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
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
        if (!(object instanceof DbConnections)) {
            return false;
        }
        DbConnections other = (DbConnections) object;
        if ((this.connectionName == null && other.connectionName != null) || (this.connectionName != null && !this.connectionName.equals(other.connectionName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.DbConnections[ connectionName=" + connectionName + " ]";
    }
    
}
