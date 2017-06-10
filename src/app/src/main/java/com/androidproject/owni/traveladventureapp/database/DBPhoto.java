package com.androidproject.owni.traveladventureapp.database;

/**
 * Created by ownI_2 on 2017-06-10.
 */

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DBPhoto extends RealmObject {

    @PrimaryKey
    private long id;

    private DBLocation location;
    private DBRoute route;
    private String path;
    private long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public DBRoute getRoute() { return this.route; }

    public void setRoute(DBRoute route) { this.route = route; }

    public DBLocation getLocation() { return this.location; }

    public void setLocation(DBLocation location) { this.location = location; }
}
