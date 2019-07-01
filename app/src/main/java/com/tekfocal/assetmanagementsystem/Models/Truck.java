package com.tekfocal.assetmanagementsystem.Models;

import com.google.android.gms.maps.model.LatLng;

public class Truck {
    private String name;
    private double lat,lon;
    private Boolean state=false;
    private String geofence="";
    private LatLng poistion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getGeofence() {
        return geofence;
    }

    public void setGeofence(String geofence) {
        this.geofence = geofence;
    }

    public LatLng getPoistion() {
        return poistion;
    }

    public void setPoistion(LatLng poistion) {
        this.poistion = poistion;
    }
}
