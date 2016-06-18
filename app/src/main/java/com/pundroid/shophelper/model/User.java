package com.pundroid.shophelper.model;

import java.util.Map;

/**
 * Created by pumba30 on 05.06.2016.
 */
public class User {
    private String email;
    private String name;
    private boolean hasLoginWithPassword;
    private Map<String, Object> timestampJoined;


    public User() {
    }

    public User(String email, String name, Map<String, Object> timestampJoined) {
        this.email = email;
        this.name = name;
        this.timestampJoined = timestampJoined;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }


    public Map<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean isHasLoginWithPassword() {
        return hasLoginWithPassword;
    }

    public void setHasLoginWithPassword(boolean hasLoginWithPassword) {
        this.hasLoginWithPassword = hasLoginWithPassword;
    }
}
