/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;
import org.apache.log4j.Logger;

/**
 * Com tempo fazer xD
 *
 * @author g0004218
 */
public class EntitySerializationExclusitionStrategy implements ExclusionStrategy {

    private final Logger logger = Logger.getLogger(EntitySerializationExclusitionStrategy.class);

    @Override
    public boolean shouldSkipField(FieldAttributes fa) {

        Expose expose = fa.getAnnotation(Expose.class);
        HideFromJson hide = fa.getAnnotation(HideFromJson.class);

        if (hide != null) {

            return true;
        }
        if (expose != null) {
            //  logger.debug("Skiping " + fa.getName() + " ::" + expose.serialize());
            return !expose.serialize();
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        return false;
    }

}
