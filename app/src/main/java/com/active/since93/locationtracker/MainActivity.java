package com.active.since93.locationtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.active.since93.locationtracker.constants.Constants;
import com.active.since93.locationtracker.database.DatabaseHandler;
import com.active.since93.locationtracker.model.LocationData;
import com.active.since93.locationtracker.service.LocationService;
import com.active.since93.locationtracker.utils.CommonUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private Context context;
    private DatabaseHandler databaseHandler;
    private List<LocationData> locationDataList = new ArrayList<>();
    private GoogleMap googleMap;
    private List<LatLng> latLngArrayList;
    private SharedPreferences userPreferences;
    private ImageView btnFocus;
    private int LOCATION_REQUEST_CODE = 1001;
    private LatLngBounds.Builder builder;
    private TextView textView;
    private Toolbar toolbar;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        userPreferences = getSharedPreferences(Constants.USER_DETAILS, MODE_PRIVATE);
        databaseHandler = DatabaseHandler.getInstance(context);

        setupToolbar();

        textView = (TextView) findViewById(R.id.textView);
        btnFocus = (ImageView) findViewById(R.id.btnFocus);
        btnFocus.setOnClickListener(this);
        initializeMap();

        locationDataList = databaseHandler.getAllLocationData();
        /*LocationData locationData = databaseHandler.getLatestLocationData();
        if (locationData != null) {
            String locationDetails = "Latitude: " + locationData.getPlace().getLatitude() + "\n"
                    + "Longitude: " + locationData.getPlace().getLongitude() + "\n"
                    + "Time: " + CommonUtils.getTimeOnly(context, locationData.getTime());
            textView.setText(locationDetails);
        }*/

        // Start location service
        startLocationService();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.location_tracker));
        toolbar.inflateMenu(R.menu.menu_main_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch (id) {
                    case R.id.action_logout:
                        SharedPreferences.Editor editor = userPreferences.edit();
                        editor.clear();
                        editor.apply();

                        intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.action_table:
                        intent = new Intent(context, TableActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    private void startLocationService() {
        Intent intent = new Intent(context, LocationService.class);
        startService(intent);
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        } else {
            googleMap.setMyLocationEnabled(true);
        }

        latLngArrayList = new ArrayList<>();
        builder = new LatLngBounds.Builder();
        for (LocationData locationData : locationDataList) {
            double lat = locationData.getPlace().getLatitude();
            double lon = locationData.getPlace().getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            latLngArrayList.add(latLng);
            builder.include(latLng);
        }

        googleMap.addPolyline(new PolylineOptions()
                .addAll(latLngArrayList)
                .width(5)
                .color(Color.RED));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                focusArea();
            }
        }, 1000);

        DecimalFormat df2 = new DecimalFormat("#.##");
        double totalDistance = calculateTravelledDistance(latLngArrayList);
        double totalDistanceKM = totalDistance / 1000;
        String string = "Total Distance: " + df2.format(totalDistanceKM) + " KM";
        textView.setText(string);
    }

    private double calculateTravelledDistance(List<LatLng> latLngArrayList) {
        double totalDistance = 0;
        for (int i = 0; i < latLngArrayList.size() - 1; i++) {
            Location location1 = new Location("");
            location1.setLatitude(latLngArrayList.get(i).latitude);
            location1.setLongitude(latLngArrayList.get(i).longitude);

            Location location2 = new Location("");
            location2.setLatitude(latLngArrayList.get(i + 1).latitude);
            location2.setLongitude(latLngArrayList.get(i + 1).longitude);

            totalDistance += location2.distanceTo(location1);
        }
        return totalDistance;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnFocus:
                focusArea();
                break;
        }
    }

    private void focusArea() {
        if(builder != null && latLngArrayList.size() > 0) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }
}
