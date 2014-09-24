/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linuxrouter.netcool.json.EntitySerializationExclusitionStrategy;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.apache.log4j.Logger;

/**
 *
 * @author lucas
 */
@Stateless
@LocalBean
public class GsonConverter {

    private final Logger logger = Logger.getLogger(GsonConverter.class);

    /**
     *
     *
     * @param obj
     * @return
     */
    public String convert2Json(Object obj) {
        Gson g = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss")
                .setPrettyPrinting()
                //.excludeFieldsWithoutExposeAnnotation()
                .addSerializationExclusionStrategy(new EntitySerializationExclusitionStrategy())
                .create();
        String response = "";
        response = g.toJson(obj);
        return response;
    }
}
