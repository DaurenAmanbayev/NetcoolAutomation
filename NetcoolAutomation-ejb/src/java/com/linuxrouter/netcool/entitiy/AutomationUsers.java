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
@Table(name = "AUTOMATION_USERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutomationUsers.findAll", query = "SELECT a FROM AutomationUsers a"),
    @NamedQuery(name = "AutomationUsers.findByLogin", query = "SELECT a FROM AutomationUsers a WHERE a.login = :login"),
    @NamedQuery(name = "AutomationUsers.findByName", query = "SELECT a FROM AutomationUsers a WHERE a.name = :name"),
    @NamedQuery(name = "AutomationUsers.findByPassword", query = "SELECT a FROM AutomationUsers a WHERE a.password = :password"),
    @NamedQuery(name = "AutomationUsers.findByEmail", query = "SELECT a FROM AutomationUsers a WHERE a.email = :email"),
    @NamedQuery(name = "AutomationUsers.findByEnabled", query = "SELECT a FROM AutomationUsers a WHERE a.enabled = :enabled")})
public class AutomationUsers implements Serializable {
    @Column(name = "ENABLED")
    private String enabled;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "LOGIN")
    private String login;
    @Size(max = 255)
    @Column(name = "NAME")
    private String name;
    @Size(max = 255)
    @Column(name = "PASSWORD")
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "EMAIL")
    private String email;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "login")
    @Expose(serialize = false)
    private List<AutomationReader> automationReaderList;

    public AutomationUsers() {
    }

    public AutomationUsers(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        hash += (login != null ? login.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutomationUsers)) {
            return false;
        }
        AutomationUsers other = (AutomationUsers) object;
        if ((this.login == null && other.login != null) || (this.login != null && !this.login.equals(other.login))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.linuxrouter.netcool.entitiy.AutomationUsers[ login=" + login + " ]";
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
    
}
