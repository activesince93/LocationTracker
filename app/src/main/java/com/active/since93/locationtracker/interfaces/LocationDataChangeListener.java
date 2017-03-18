package com.active.since93.locationtracker.interfaces;

import com.active.since93.locationtracker.model.LocationData;

/**
 * Created by myzupp on 18-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public interface LocationDataChangeListener {
    void onLocationDataChanged(LocationData locationData);
}