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

    public boolean LocationChangedHandler(Location location){
        // TODO
        // add logic to save to db only valuable locations

        return AddLocalization(location);
    }

    public boolean AddLocalization(Location location){
        // TODO
        // remove later debug logs
        Log.e("AddLocalization", "loc_lat: " + String.valueOf(location.getLatitude()));
        Log.e("AddLocalization", "loc_lon: " + String.valueOf(location.getLongitude()));

        realm.beginTransaction();
        Long timestamp = System.currentTimeMillis()/1000;
        DBLocation dbLocation = new DBLocation();
        dbLocation.setId(timestamp);
        dbLocation.setGeoWidth(location.getLongitude());
        dbLocation.setGeoHeight(location.getLatitude());
        dbLocation.setTimestamp(timestamp);
        realm.insert(dbLocation);
        realm.commitTransaction();

        return true;
    }

    public void CloseDatabase(){
        realm.close();
    }
}