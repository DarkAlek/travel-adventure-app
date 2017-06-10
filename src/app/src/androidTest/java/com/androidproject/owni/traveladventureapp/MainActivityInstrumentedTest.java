package com.androidproject.owni.traveladventureapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.androidproject.owni.traveladventureapp.database.DBRoute;
import com.androidproject.owni.traveladventureapp.lib.DatabaseManager;
import com.google.android.gms.maps.SupportMapFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.DatabaseMetaData;

import io.realm.Realm;
import io.realm.RealmQuery;

import static org.junit.Assert.*;

/**
 * Created by Alek on 2017-06-10.
 */

public class MainActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void ensureMapIsPresent() throws Exception {

        MainActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.map);
        assertNotNull(viewById);
        assertTrue(viewById instanceof FrameLayout);

    }

}
