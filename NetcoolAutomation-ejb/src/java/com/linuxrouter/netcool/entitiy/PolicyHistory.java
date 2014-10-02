/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.entitiy;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lucas
 */
@Entity
@Table(name = "POLICY_HISTORY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PolicyHistory.findAll", query = "SELECT p FROM PolicyHistory p"),
    @NamedQuery(name = "PolicyHistory.findByExecutionDate", query = "SELECT p FROM PolicyHistory p WHERE p.executionDate = :executionDate"),
    @NamedQuery(name = "PolicyHistory.findByExecutionTime", query = "SELECT p FROM PolicyHistory p WHERE p.executionTime = :executionTime")})
public class PolicyHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;
    @Column(name = "EXECUTION_TIME")
    private Integer executionTime;
    @JoinColumn(name = "POLICY_NAME", referencedColumnName = "POLICY_NAME")
    @ManyToOne(optional = false)
    @Expose(serialize = false)
    private AutomationPolicies policyName;

    public PolicyHistory() {
    }

    public PolicyHistory(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    public AutomationPolicies getPolicyName() {
        return policyName;
    }

    public void setPolicyName(AutomationPolicies policyName) {
        this.policyName = policyName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (executionDate != null ? executionDate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PolicyHistory)) {
            return false;
        }
        PolicyHistory other = (PolicyHistory) object;
        if ((this.executionDate == null && other.executionDate != null) || (this.executionDate != null && !this.executionDate.equals(other.executionDate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.PolicyHistory[ executionDate=" + executionDate + " ]";
    }
    
}
