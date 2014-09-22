/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author lucas
 */
@Stateless
@LocalBean
public class UtilSession {

    public String getMd5HashFromString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            md.update(bytesOfMessage);
            bytesOfMessage = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytesOfMessage) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UtilSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UtilSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
