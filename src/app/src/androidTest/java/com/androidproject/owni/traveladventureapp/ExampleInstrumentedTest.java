package com.androidproject.owni.traveladventureapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.androidproject.owni.traveladventureapp.database.DBRoute;
import com.androidproject.owni.traveladventureapp.lib.DatabaseManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.DatabaseMetaData;

import io.realm.Realm;
import io.realm.RealmQuery;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.androidproject.owni.traveladventureapp", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<>(MainActivity.class);
    @Test
    @UiThreadTest
    public void enterRouteName() throws Exception {

        MainActivity mainActivity = rule.getActivity();
        mainActivity.addNewTravel("TestRoute");
        DatabaseManager database = new DatabaseManager(InstrumentationRegistry.getTargetContext());
        Realm realm = database.getRealmObject();
        RealmQuery<DBRoute> query = realm.where(DBRoute.class);
        query.equalTo("name", "TestRoute");
        DBRoute route = query.findFirst();
        assertEquals(route.getName(), "TestRoute");

    }
}
