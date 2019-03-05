package com.tingyuyeh.a268demo;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPS_Tracker extends Service implements LocationListener {
    private static final String DEBUG_TAG = "GPS_TRACKER";
    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for location status
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    // the minimum distance to change Updates in meters
    private static final long MIN_DIST_CHANGE_FOR_UPDATES = 1; // 10 meters

    // the minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 2; // 2 seconds

    // Declaring a location manager
    protected LocationManager locationManager;

    public GPS_Tracker(Context context) {
        mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            // initialize the location manager
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // get the GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get the network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // check if GPS or network providers are available
            if(!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
            }
            else {
                canGetLocation = true;
                // First get the location from the network provider
                if(isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DIST_CHANGE_FOR_UPDATES,
                                this);
                    }
                    catch (SecurityException e) {
                        e.printStackTrace();
                        Log.v(DEBUG_TAG, e.getMessage());
                    }
                    Log.d(DEBUG_TAG, "Network");
                    if(locationManager != null) {
                        try {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        catch (SecurityException e) {
                            e.printStackTrace();
                            Log.v(DEBUG_TAG, e.getMessage());
                        }
                        if(null != location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS is enabled get lat/long using GPS services
                if(isGPSEnabled) {
                    if(null == location) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DIST_CHANGE_FOR_UPDATES,
                                    this);
                        }
                        catch (SecurityException e) {
                            e.printStackTrace();
                            Log.v(DEBUG_TAG, e.getMessage());
                        }
                        Log.d(DEBUG_TAG, "GPS Enabled");
                        if(null != locationManager) {
                            try {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            }
                            catch (SecurityException e) {
                                Log.v(DEBUG_TAG, e.getMessage());
                                e.printStackTrace();
                            }
                            if(null != location) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                        else {
                            Log.v(DEBUG_TAG, "locationmanager is null");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.v(DEBUG_TAG, e.getMessage());
        }

        return location;
    }

    // stop using GPS listener
    // calling this function will stop using GPS in your app
    public void stopUsingGPS() {
        if(null != locationManager) {
            locationManager.removeUpdates(GPS_Tracker.this);
        }
    }

    // function to get latitude
    public double getLatitude() {
        if(null != location) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    // function to get longitude
    public double getLongitude() {
        if(null != location) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    // function to check if GPS/wifi is enabled
    public boolean canGetLocation() {
        return canGetLocation;
    }

    // function to show settings alert dialong
    // on pressing settings button will launch settings options
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // set the dialog title
        alertDialog.setTitle("GPS Settings");

        // set the dialog message
        alertDialog.setMessage("GPS is not enabled.  Do you want to go to the settings menu?");

        // on pressing the settings button
        alertDialog.setPositiveButton("Settings", ((DialogInterface dialog, int which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);})
        );

        // on pressing the cancel button
        alertDialog.setNegativeButton("Cancel", ((DialogInterface dialog, int which) -> {
            dialog.cancel(); })
        );

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
