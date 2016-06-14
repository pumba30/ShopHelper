package com.pundroid.shophelper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;
import com.pundroid.shophelper.utils.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 29.05.2016.
 */


public class ShoppingList implements Serializable {

    private String listName;
    private String owner;
    private Map<String, Object> timestampCreated;
    private Map<String, Object> dateLastChanged;


    public ShoppingList() {
    }

    public ShoppingList(String listName, String owner, Map<String,Object> timestampCreated) {
        this.listName = listName;
        this.owner = owner;
        this.timestampCreated = timestampCreated;
        Map<String, Object> dateLastChangedObj = new HashMap<>();
        dateLastChangedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;
    }

    public String getListName() {
        return listName;
    }


    public String getOwner() {
        return owner;
    }


    public Map<String, Object> getDateLastChanged() {
        return dateLastChanged;
    }

    public Map<String, Object> getTimestampCreated() {
        return timestampCreated;
    }


    @JsonIgnore
    public long getDateLastChangedLong() {
        if (dateLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED) == null) {
            return (long) dateLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
        }
        return (long) dateLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
