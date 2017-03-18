package com.active.since93.locationtracker.model;

/**
 * Created by myzupp on 17-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class Place {
    String name;
    double latitude;
    double longitude;

    public Place() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
