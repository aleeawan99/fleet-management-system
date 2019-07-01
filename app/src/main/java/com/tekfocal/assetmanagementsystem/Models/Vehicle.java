package com.tekfocal.assetmanagementsystem.Models;


public class Vehicle {
    private String ownerName, model;
    private boolean status;

    public Vehicle(String ownerName, String model, boolean status) {
        this.ownerName = ownerName;
        this.model = model;
        this.status = status;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getModel() {
        return model;
    }

    public boolean isStatus() {
        return status;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
