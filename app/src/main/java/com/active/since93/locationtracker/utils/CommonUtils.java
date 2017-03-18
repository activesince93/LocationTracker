package com.active.since93.locationtracker.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;

import com.active.since93.locationtracker.R;
import com.active.since93.locationtracker.constants.Constants;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by myzupp on 18-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class CommonUtils {
    /**
     * Create alert dialog
     * @param context context
     * @param message message
     * @return alert dialog
     */
    public static AlertDialog.Builder showErrorAlertDialog(Context context, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(Constants.APP_NAME)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.okay)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
    }

    /**
     * Get formatted date
     *
     * @param context context
     * @param smsTimeInMillis time in milliseconds
     * @return time in string for e.x. 07:20 PM, YESTERDAY etc.
     */
    public static String getTimeOnly(Context context, long smsTimeInMillis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMillis);

        final String timeFormatString = (!DateFormat.is24HourFormat(context))
                ? "hh:mm aa" : "HH:mm";
        Date date = new Date(smsTimeInMillis);
        SimpleDateFormat df = new SimpleDateFormat(timeFormatString, Locale.US);
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

    public static double distance(LatLng latLng1, LatLng latLng2) {
        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
