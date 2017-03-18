package com.active.since93.locationtracker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.active.since93.locationtracker.database.DatabaseHandler;
import com.active.since93.locationtracker.model.LocationData;
import com.active.since93.locationtracker.model.Place;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LOCATION_SERVICE";
    private static final int INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;
    private GoogleApiClient googleApiClient;

    private DatabaseHandler databaseHandler;
    private Context context;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        context = getApplicationContext();
        databaseHandler = DatabaseHandler.getInstance(context);

        initializeLocationManager();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void initializeLocationManager() {
        googleApiClient = new GoogleApiClient
                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL).setSmallestDisplacement(LOCATION_DISTANCE);

        LocationListener locationListener = new LocationListener();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient
                    , locationRequest
                    , locationListener);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class LocationListener implements com.google.android.gms.location.LocationListener {

        LocationListener() {}

        @Override
        public void onLocationChanged(Location newLocation) {
            Log.e(TAG, "onLocationChanged: " + newLocation);

            Place place = new Place();
            place.setLatitude(newLocation.getLatitude());
            place.setLongitude(newLocation.getLongitude());
            place.setName("");

            LocationData locationData = new LocationData();
            locationData.setPlace(place);
            locationData.setTime(System.currentTimeMillis());

            LocationData previousLocationData = databaseHandler.getLatestLocationData();
            if(previousLocationData != null) {
                Location previousLocation = new Location("");
                previousLocation.setLatitude(locationData.getPlace().getLatitude());
                previousLocation.setLongitude(locationData.getPlace().getLongitude());

                double lastPointDistance = newLocation.distanceTo(previousLocation);
                locationData.setTotalDistanceCovered(lastPointDistance);

                // If last point distance is greater than 10 meters
                Log.e(TAG, "Distance covered: " + lastPointDistance);
//                if(lastPointDistance > 10) {
                    databaseHandler.addNewLocationData(locationData);
//                }
            } else {
                locationData.setTotalDistanceCovered(0);
                databaseHandler.addNewLocationData(locationData);
            }
        }
    }
}
