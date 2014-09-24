/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.entitiy;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name = "AUTOMATION_READER_FILTER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutomationReaderFilter.findAll", query = "SELECT a FROM AutomationReaderFilter a"),
    @NamedQuery(name = "AutomationReaderFilter.findByFilterName", query = "SELECT a FROM AutomationReaderFilter a WHERE a.filterName = :filterName"),
    @NamedQuery(name = "AutomationReaderFilter.findByFilterSql", query = "SELECT a FROM AutomationReaderFilter a WHERE a.filterSql = :filterSql"),
    @NamedQuery(name = "AutomationReaderFilter.findByEnabled", query = "SELECT a FROM AutomationReaderFilter a WHERE a.enabled = :enabled"),
    @NamedQuery(name = "AutomationReaderFilter.findByStateChange", query = "SELECT a FROM AutomationReaderFilter a WHERE a.stateChange = :stateChange")})
public class AutomationReaderFilter implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "FILTER_NAME")
    private String filterName;
    @Size(max = 255)
    @Column(name = "FILTER_SQL")
    private String filterSql;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ENABLED")
    private String enabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATE_CHANGE")
    private int stateChange;
    @JoinColumn(name = "READER_NAME", referencedColumnName = "READER_NAME")
    @ManyToOne(optional = false)
    @Expose(serialize = false)
    private AutomationReader readerName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "filterName")
    @OrderBy("executionOrder")    
    private List<AutomationPolicies> automationPoliciesList;

    public AutomationReaderFilter() {
    }

    public AutomationReaderFilter(String filterName) {
        this.filterName = filterName;
    }

    public AutomationReaderFilter(String filterName, String enabled, int stateChange) {
        this.filterName = filterName;
        this.enabled = enabled;
        this.stateChange = stateChange;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterSql() {
        return filterSql;
    }

    public void setFilterSql(String filterSql) {
        this.filterSql = filterSql;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public int getStateChange() {
        return stateChange;
    }

    public void setStateChange(int stateChange) {
        this.stateChange = stateChange;
    }

    public AutomationReader getReaderName() {
        return readerName;
    }

    public void setReaderName(AutomationReader readerName) {
        this.readerName = readerName;
    }

    @XmlTransient
    public List<AutomationPolicies> getAutomationPoliciesList() {
        return automationPoliciesList;
    }

    public void setAutomationPoliciesList(List<AutomationPolicies> automationPoliciesList) {
        this.automationPoliciesList = automationPoliciesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (filterName != null ? filterName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutomationReaderFilter)) {
            return false;
        }
        AutomationReaderFilter other = (AutomationReaderFilter) object;
        if ((this.filterName == null && other.filterName != null) || (this.filterName != null && !this.filterName.equals(other.filterName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.AutomationReaderFilter[ filterName=" + filterName + " ]";
    }
    
}
