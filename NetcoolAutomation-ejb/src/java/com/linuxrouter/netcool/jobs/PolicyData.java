/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.jobs;

/**
 *
 * @author lucas
 */
public class PolicyData {
    private String policyName ="";
    private String policyScript = "";

    /**
     * @return the policyName
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * @param policyName the policyName to set
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * @return the policyScript
     */
    public String getPolicyScript() {
        return policyScript;
    }

    /**
     * @param policyScript the policyScript to set
     */
    public void setPolicyScript(String policyScript) {
        this.policyScript = policyScript;
    }
}
