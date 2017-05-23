package com.androidproject.owni.traveladventureapp.lib;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ownI_2 on 2017-05-23.
 */

public class DatabaseManager {
    private Realm realm;

    public DatabaseManager(Context context) {
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);

        realm = Realm.getDefaultInstance();
    }

    public Realm getRealmObject() {
        return realm;
    }

    public void close() {
        if (!realm.isClosed())
            realm.close();
    }
}
