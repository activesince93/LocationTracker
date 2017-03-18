package com.active.since93.locationtracker.model;

import java.io.Serializable;

/**
 * Created by myzupp on 17-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class LocationData implements Serializable {
    long time;
    Place place;
    double totalDistanceCovered;

    public LocationData() {
    }

    public double getTotalDistanceCovered() {
        return totalDistanceCovered;
    }

    public void setTotalDistanceCovered(double totalDistanceCovered) {
        this.totalDistanceCovered = totalDistanceCovered;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
