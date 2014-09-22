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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "AUTOMATION_READER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutomationReader.findAll", query = "SELECT a FROM AutomationReader a"),
    @NamedQuery(name = "AutomationReader.findByReaderName", query = "SELECT a FROM AutomationReader a WHERE a.readerName = :readerName"),
    @NamedQuery(name = "AutomationReader.findByCronInterval", query = "SELECT a FROM AutomationReader a WHERE a.cronInterval = :cronInterval"),
    @NamedQuery(name = "AutomationReader.findByEnabled", query = "SELECT a FROM AutomationReader a WHERE a.enabled = :enabled"),
    @NamedQuery(name = "AutomationReader.findByLogging", query = "SELECT a FROM AutomationReader a WHERE a.logging = :logging")})
public class AutomationReader implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "READER_NAME")
    private String readerName;
    @Size(max = 20)
    @Column(name = "CRON_INTERVAL")
    private String cronInterval;
    @Lob
    @Size(max = 65535)
    @Column(name = "READER_SQL")
    private String readerSql;
    @Column(name = "ENABLED")
    private Character enabled;
    @Column(name = "LOGGING")
    private Character logging;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "readerName")
    private List<AutomationPolicies> automationPoliciesList;
    @JoinColumn(name = "LOGIN", referencedColumnName = "LOGIN")
    @ManyToOne(optional = false)
    private AutomationUsers login;
    @JoinColumn(name = "CONNECTION_NAME", referencedColumnName = "CONNECTION_NAME")
    @ManyToOne(optional = false)
    private AutomationConnection connectionName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "readerName")
    private List<ReaderHistory> readerHistoryList;

    public AutomationReader() {
    }

    public AutomationReader(String readerName) {
        this.readerName = readerName;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getCronInterval() {
        return cronInterval;
    }

    public void setCronInterval(String cronInterval) {
        this.cronInterval = cronInterval;
    }

    public String getReaderSql() {
        return readerSql;
    }

    public void setReaderSql(String readerSql) {
        this.readerSql = readerSql;
    }

    public Character getEnabled() {
        return enabled;
    }

    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }

    public Character getLogging() {
        return logging;
    }

    public void setLogging(Character logging) {
        this.logging = logging;
    }

    @XmlTransient
    public List<AutomationPolicies> getAutomationPoliciesList() {
        return automationPoliciesList;
    }

    public void setAutomationPoliciesList(List<AutomationPolicies> automationPoliciesList) {
        this.automationPoliciesList = automationPoliciesList;
    }

    public AutomationUsers getLogin() {
        return login;
    }

    public void setLogin(AutomationUsers login) {
        this.login = login;
    }

    public AutomationConnection getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(AutomationConnection connectionName) {
        this.connectionName = connectionName;
    }

    @XmlTransient
    public List<ReaderHistory> getReaderHistoryList() {
        return readerHistoryList;
    }

    public void setReaderHistoryList(List<ReaderHistory> readerHistoryList) {
        this.readerHistoryList = readerHistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (readerName != null ? readerName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutomationReader)) {
            return false;
        }
        AutomationReader other = (AutomationReader) object;
        if ((this.readerName == null && other.readerName != null) || (this.readerName != null && !this.readerName.equals(other.readerName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.AutomationReader[ readerName=" + readerName + " ]";
    }
    
}
