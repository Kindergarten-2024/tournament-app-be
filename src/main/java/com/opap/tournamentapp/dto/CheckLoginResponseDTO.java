package com.opap.tournamentapp.dto;

import java.util.Map;

public class CheckLoginResponseDTO {
    private  boolean loggedIn;
    private Map<String,Object> user;

    public CheckLoginResponseDTO(boolean loggedIn, Map<String,Object> user) {
        this.loggedIn = loggedIn;
        this.user = user;
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Map<String,Object> getUser() {
        return user;
    }

    public void setUser(Map<String,Object> user) {
        this.user = user;
    }
}
