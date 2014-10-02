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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lucas
 */
@Entity
@Table(name = "READER_HISTORY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ReaderHistory.findAll", query = "SELECT r FROM ReaderHistory r"),
    @NamedQuery(name = "ReaderHistory.findByExecutionDate", query = "SELECT r FROM ReaderHistory r WHERE r.executionDate = :executionDate"),
    @NamedQuery(name = "ReaderHistory.findByEventCount", query = "SELECT r FROM ReaderHistory r WHERE r.eventCount = :eventCount"),
    @NamedQuery(name = "ReaderHistory.findByExecutionTime", query = "SELECT r FROM ReaderHistory r WHERE r.executionTime = :executionTime")})
public class ReaderHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;
    @Column(name = "EVENT_COUNT")
    private Integer eventCount;
    @Column(name = "EXECUTION_TIME")
    private Integer executionTime;
    @Lob
    @Size(max = 65535)
    @Column(name = "HIST_TEXT")
    private String histText;
    @JoinColumn(name = "READER_NAME", referencedColumnName = "READER_NAME")
    @ManyToOne(optional = false)
    @Expose(serialize = false)
    private AutomationReader readerName;

    public ReaderHistory() {
    }

    public ReaderHistory(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    public String getHistText() {
        return histText;
    }

    public void setHistText(String histText) {
        this.histText = histText;
    }

    public AutomationReader getReaderName() {
        return readerName;
    }

    public void setReaderName(AutomationReader readerName) {
        this.readerName = readerName;
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
        if (!(object instanceof ReaderHistory)) {
            return false;
        }
        ReaderHistory other = (ReaderHistory) object;
        if ((this.executionDate == null && other.executionDate != null) || (this.executionDate != null && !this.executionDate.equals(other.executionDate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.ReaderHistory[ executionDate=" + executionDate + " ]";
    }
    
}
