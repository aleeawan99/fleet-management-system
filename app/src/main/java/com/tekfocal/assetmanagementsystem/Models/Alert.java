package com.tekfocal.assetmanagementsystem.Models;

public class Alert {
    private String event, time;

    public Alert(String event, String time) {
        this.event = event;
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public String getTime() {
        return time;
    }
}
