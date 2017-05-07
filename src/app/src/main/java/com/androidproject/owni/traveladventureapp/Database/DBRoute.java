package com.androidproject.owni.traveladventureapp.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alek on 2017-04-29.
 */

public class DBRoute extends RealmObject{

    @PrimaryKey
    private long id;

    private String name;
    private RealmList<DBLocation> route;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}