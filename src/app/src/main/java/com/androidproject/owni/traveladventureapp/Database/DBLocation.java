package com.androidproject.owni.traveladventureapp.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alek on 2017-04-29.
 */

public class DBLocation extends RealmObject{

    @PrimaryKey
    private long id;

    private double geoWidth;
    private double geoHeight;

    private long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getGeoWidth() {
        return geoWidth;
    }

    public void setGeoWidth(double geoWidth) {
        this.geoWidth = geoWidth;
    }

    public double getGeoHeight() {
        return geoHeight;
    }

    public void setGeoHeight(double geoHeight) {
        this.geoHeight = geoHeight;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
