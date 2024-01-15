package com.opap.tournamentapp.dto;

public class SharedData {
    private boolean sharedFlag;
    private static SharedData instance = new SharedData();

    private SharedData() {
        sharedFlag = false;
    }

    public static SharedData getInstance() {
        return instance;
    }

    public synchronized void makeTrue() {
        sharedFlag = true;
    }

    public synchronized void makeFalse() {
        sharedFlag = false;
    }

    public synchronized boolean isTrue() {
        return sharedFlag;
    }
}
