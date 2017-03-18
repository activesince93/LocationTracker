package com.active.since93.locationtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.active.since93.locationtracker.model.LocationData;
import com.active.since93.locationtracker.model.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by myzupp on 17-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static DatabaseHandler mDatabaseHandler;
    private static SQLiteDatabase mSQLiteDatabase;

    // database version
    private static final int DATABASE_VERSION = 1;
    // database name
    private static final String DATABASE_NAME = "mpaani_database";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get database instance
     * @param context context
     * @return database handler instance
     */
    public static DatabaseHandler getInstance(Context context) {
        if(mDatabaseHandler == null) {
            mDatabaseHandler = new DatabaseHandler(context);
        }
        return mDatabaseHandler;
    }

    /**
     * Open database
     * @return sqLiteDatabase object
     */
    private synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mSQLiteDatabase = this.getWritableDatabase();
        }
        return mSQLiteDatabase;
    }

    /**
     * Close database
     */
    private synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mSQLiteDatabase.close();
        }
    }

    /**
     * Check if database in use before deleting database
     * @return true if database in use
     */
    private boolean isDatabaseInUse() {
        return (mOpenCounter.intValue() != 0);
    }

    // TABLES
    private static final String TABLE_LOCATION_DATA = "table_location_data";

    // TABLE_LOCATION_DATA columns
    private static final String LOCATION_ID = "location_id";
    private static final String LOCATION_LATITUDE = "location_latitude";
    private static final String LOCATION_LONGITUDE = "location_longitude";
    private static final String LOCATION_NAME = "location_name";
    private static final String LOCATION_TIME = "location_time";
    private static final String LOCATION_TOTAL_DISTANCE_COVERED = "location_total_distance_covered";

    String CREATE_TABLE_LOCATION_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_DATA + "("
            + LOCATION_ID + " INTEGER PRIMARY KEY,"
            + LOCATION_LATITUDE + " TEXT,"
            + LOCATION_LONGITUDE + " TEXT,"
            + LOCATION_NAME + " TEXT,"
            + LOCATION_TIME + " INTEGER,"
            + LOCATION_TOTAL_DISTANCE_COVERED + " INTEGER"
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATION_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void addNewLocationData(LocationData locationData) {
        SQLiteDatabase db = openDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION_LATITUDE, String.valueOf(locationData.getPlace().getLatitude()));
        values.put(LOCATION_LONGITUDE, String.valueOf(locationData.getPlace().getLongitude()));
        values.put(LOCATION_NAME, locationData.getPlace().getName());
        values.put(LOCATION_TIME, locationData.getTime());
        values.put(LOCATION_TOTAL_DISTANCE_COVERED, locationData.getTotalDistanceCovered());
        db.insert(TABLE_LOCATION_DATA, null, values);
        closeDatabase();
    }

    public LocationData getLatestLocationData() {
        LocationData locationData = null;
        SQLiteDatabase db = openDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATION_DATA
                + " ORDER BY " + LOCATION_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            Place place = new Place();
            place.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_LATITUDE))));
            place.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_LONGITUDE))));
            place.setName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)));

            locationData = new LocationData();
            locationData.setPlace(place);
            locationData.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(LOCATION_TIME))));
            locationData.setTotalDistanceCovered(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_TOTAL_DISTANCE_COVERED))));
        }
        cursor.close();
        closeDatabase();
        return locationData;
    }

    public List<LocationData> getAllLocationData() {
        List<LocationData> locationDataList = new ArrayList<>();
        SQLiteDatabase db = openDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATION_DATA
                + " ORDER BY " + LOCATION_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                Place place = new Place();
                place.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_LATITUDE))));
                place.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_LONGITUDE))));
                place.setName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)));

                LocationData locationData = new LocationData();
                locationData.setPlace(place);
                locationData.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(LOCATION_TIME))));
                locationData.setTotalDistanceCovered(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_TOTAL_DISTANCE_COVERED))));

                locationDataList.add(locationData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return locationDataList;
    }

    public List<LocationData> getSelectedLocationData(long fromTime, long toTime) {
        List<LocationData> locationDataList = new ArrayList<>();
        SQLiteDatabase db = openDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATION_DATA
                + " WHERE " + LOCATION_TIME + ">=" + fromTime + " AND " + LOCATION_TIME + "<" + toTime
                + " ORDER BY " + LOCATION_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                Place place = new Place();
                place.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_LATITUDE))));
                place.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_LONGITUDE))));
                place.setName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)));

                LocationData locationData = new LocationData();
                locationData.setPlace(place);
                locationData.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(LOCATION_TIME))));
                locationData.setTotalDistanceCovered(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATION_TOTAL_DISTANCE_COVERED))));

                locationDataList.add(locationData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return locationDataList;
    }
}

