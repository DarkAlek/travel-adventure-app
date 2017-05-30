package com.androidproject.owni.traveladventureapp.lib;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidproject.owni.traveladventureapp.database.DBLocation;
import com.androidproject.owni.traveladventureapp.database.DBRoute;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LocalizationManager {

    private Realm realm;
    private Context mcontext;

    public LocalizationManager(Context context){
        mcontext = context;
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
        Log.e("Altitude", "altitude:" + Double.toString(location.getAltitude()));

        DBRoute dbRoute = realm.where(DBRoute.class).equalTo("isRunning", Boolean.TRUE).findFirst();

        if (dbRoute != null) {
            realm.beginTransaction();
            Long timestamp = System.currentTimeMillis() / 1000;
            DBLocation dbLocation = new DBLocation();
            dbLocation.setId(timestamp);
            dbLocation.setGeoWidth(location.getLatitude());
            dbLocation.setGeoHeight(location.getLongitude());
            dbLocation.setTimestamp(timestamp);
            dbRoute.getRoute().add(dbLocation);
            realm.insertOrUpdate(dbLocation);
            realm.insertOrUpdate(dbRoute);
            realm.commitTransaction();

            updateAltitude(dbLocation);
        }

        return true;
    }

    private double updateAltitude(final DBLocation location) {
        double result = Double.NaN;

        RequestQueue queue = Volley.newRequestQueue(mcontext);

        String url = "https://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(location.getGeoWidth())
                + "," + String.valueOf(location.getGeoHeight())
                + "&sensor=true"
                + "&key=AIzaSyDqTRHTjjU18oBC4AwP_wCWExUGqMwAzAg";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        double result = Double.NaN;

                        String tagOpen = "<elevation>";
                        String tagClose = "</elevation>";
                        if (response.indexOf(tagOpen) != -1) {
                            int start = response.indexOf(tagOpen) + tagOpen.length();
                            int end = response.indexOf(tagClose);
                            String value = response.substring(start, end);
                            result = Double.parseDouble(value);
                        }

                        Log.d("Altitude", "Response is: "+ Double.toString(result));
                        realm.beginTransaction();
                        location.setAltitude(result);
                        realm.insertOrUpdate(location);
                        realm.commitTransaction();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Altitude", "DOESNT WORK");
                    }
        });
        queue.add(stringRequest);

        return result;
    }

}
