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
@Table(name = "AUTOMATION_POLICIES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutomationPolicies.findAll", query = "SELECT a FROM AutomationPolicies a"),
    @NamedQuery(name = "AutomationPolicies.findByPolicyName", query = "SELECT a FROM AutomationPolicies a WHERE a.policyName = :policyName"),
    @NamedQuery(name = "AutomationPolicies.findByEnabled", query = "SELECT a FROM AutomationPolicies a WHERE a.enabled = :enabled"),
    @NamedQuery(name = "AutomationPolicies.findByExecutionOrder", query = "SELECT a FROM AutomationPolicies a WHERE a.executionOrder = :executionOrder"),
    @NamedQuery(name = "AutomationPolicies.findByLogging", query = "SELECT a FROM AutomationPolicies a WHERE a.logging = :logging")})
public class AutomationPolicies implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "POLICY_NAME")
    private String policyName;
    @Size(max = 1)
    @Column(name = "ENABLED")
    private String enabled;
    @Lob
    @Size(max = 65535)
    @Column(name = "SCRIPT")
    private String script;
    @Column(name = "EXECUTION_ORDER")
    private Integer executionOrder;
    @Column(name = "LOGGING")
    private Character logging;
    @JoinColumn(name = "READER_NAME", referencedColumnName = "READER_NAME")
    @ManyToOne(optional = false)
    private AutomationReader readerName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyName")
    private List<PolicyHistory> policyHistoryList;

    public AutomationPolicies() {
    }

    public AutomationPolicies(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Integer getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Integer executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Character getLogging() {
        return logging;
    }

    public void setLogging(Character logging) {
        this.logging = logging;
    }

    public AutomationReader getReaderName() {
        return readerName;
    }

    public void setReaderName(AutomationReader readerName) {
        this.readerName = readerName;
    }

    @XmlTransient
    public List<PolicyHistory> getPolicyHistoryList() {
        return policyHistoryList;
    }

    public void setPolicyHistoryList(List<PolicyHistory> policyHistoryList) {
        this.policyHistoryList = policyHistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (policyName != null ? policyName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutomationPolicies)) {
            return false;
        }
        AutomationPolicies other = (AutomationPolicies) object;
        if ((this.policyName == null && other.policyName != null) || (this.policyName != null && !this.policyName.equals(other.policyName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.AutomationPolicies[ policyName=" + policyName + " ]";
    }
    
}
