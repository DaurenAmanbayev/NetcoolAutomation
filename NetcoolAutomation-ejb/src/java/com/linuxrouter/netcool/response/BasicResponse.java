/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.response;

/**
 *
 * @author lucas
 */
public class BasicResponse {

    private Boolean success = false;
    private String msg = "";
    private Object payLoad = null;

    /**
     * @return the success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the payLoad
     */
    public Object getPayLoad() {
        return payLoad;
    }

    /**
     * @param payLoad the payLoad to set
     */
    public void setPayLoad(Object payLoad) {
        this.payLoad = payLoad;
    }
}
