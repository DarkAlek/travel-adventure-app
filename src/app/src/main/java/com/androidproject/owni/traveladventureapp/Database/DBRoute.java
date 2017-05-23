package com.androidproject.owni.traveladventureapp.Database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alek on 2017-04-29.
 */

public class DBRoute extends RealmObject{

    @PrimaryKey
    private String id;

    private String name;

    private long timestamp;

    private RealmList<DBLocation> route;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<DBLocation> getRoute() {
        return route;
    }

    public void setRoute(RealmList<DBLocation> route) {
        this.route = route;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
