package com.androidproject.owni.traveladventureapp.lib;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.androidproject.owni.traveladventureapp.database.DBLocation;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LocalizationManager {

    private Realm realm;

    public LocalizationManager(Context context){
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);

        realm = Realm.getDefaultInstance();
    }

    public void LocationChangedHandler(Location location){
        // TODO
        // add logic to save to db only valuable locations

        AddLocalization(location);
    }

    public void AddLocalization(Location location){
        // TODO
        // remove later debug logs

        Log.e("AddLocalization", "loc_lat: " + String.valueOf(location.getLatitude()));
        Log.e("AddLocalization", "loc_lon: " + String.valueOf(location.getLongitude()));

        // TODO
        // add location to database

        /*
        realm.beginTransaction();
        Log.e("AddLocalization: ", "beginTransaction");

        Long timestamp = System.currentTimeMillis()/1000;
        DBLocation dbLocation = realm.createObject(DBLocation.class);
        Log.e("AddLocalization: ", "createObject");
        dbLocation.setGeoWidth(location.getLongitude());
        dbLocation.setGeoHeight(location.getLatitude());
        dbLocation.setTimestamp(timestamp);
        Log.e("AddLocalization: ", "setProperties");

        realm.commitTransaction();
        Log.e("AddLocalization", "transactionEnd: " + String.valueOf(location.getLongitude()));
        */

        // TODO
        // send notification to TravelMapFragment about current position
    }

    public void CloseDatabase(){
        realm.close();
    }
}