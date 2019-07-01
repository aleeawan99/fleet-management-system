package com.tekfocal.assetmanagementsystem.Models;

public class Package {

    private String title, route, status;

    public Package(String title, String route, String status) {
        this.title = title;
        this.route = route;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getRoute() {
        return route;
    }

    public String getStatus() {
        return status;
    }
}
