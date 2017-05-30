package com.androidproject.owni.traveladventureapp.lib;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.androidproject.owni.traveladventureapp.database.DBLocation;
import com.androidproject.owni.traveladventureapp.database.DBRoute;

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

        DBRoute dbRoute = realm.where(DBRoute.class).equalTo("isRunning", Boolean.TRUE).findFirst();

        realm.beginTransaction();
        Long timestamp = System.currentTimeMillis()/1000;
        DBLocation dbLocation = new DBLocation();
        dbLocation.setId(timestamp);
        dbLocation.setGeoWidth(location.getLatitude());
        dbLocation.setGeoHeight(location.getLongitude());
        dbLocation.setTimestamp(timestamp);
        dbRoute.getRoute().add(dbLocation);
        realm.insertOrUpdate(dbLocation);
        realm.insertOrUpdate(dbRoute);
        realm.commitTransaction();

        return true;
    }
}