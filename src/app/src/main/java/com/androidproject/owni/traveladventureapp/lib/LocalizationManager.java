package com.androidproject.owni.traveladventureapp.lib;


import android.location.Location;
import android.util.Log;

public class LocalizationManager {

    public LocalizationManager(){
        //pass
    }

    public void location_changed_handler(Location location){
        // TODO
        // remove later debug logs

        Log.e("ProbeLocalization", "onLocationChanged loc_lat: " + String.valueOf(location.getLatitude()));
        Log.e("ProbeLocalization", "onLocationChanged loc_lon: " + String.valueOf(location.getLongitude()));
    }

    public void add_to_db(Location location){
        // TODO
        // add to database
    }
}