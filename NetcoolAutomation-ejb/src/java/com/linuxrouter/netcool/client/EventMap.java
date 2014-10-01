/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linuxrouter.netcool.client;

import groovy.util.ObservableMap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lucas
 */
public class EventMap extends ObservableMap implements PropertyChangeListener {

    private HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents;

    public EventMap() {
        super();
    }

    public EventMap(Map delegate) {
        super(delegate);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("Marking Event Changed for Identifier: " + this.get("Identifier"));
        HashMap<String, Object> data = new HashMap<>();
        data.put(evt.getPropertyName(), evt.getNewValue());
        if (changedEvents.get(this.get("Serial")) != null) {
            changedEvents.get(this.get("Serial")).add(data);
        } else {
            ArrayList<HashMap<String, Object>> lists = new ArrayList<>();
            lists.add(data);
            changedEvents.put(this.get("Serial").toString(), lists);
        }
    }

    public void setChangedMap(HashMap<String, ArrayList<HashMap<String, Object>>> map) {
        this.changedEvents = map;
    }
}
