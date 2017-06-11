package com.androidproject.owni.traveladventureapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.androidproject.owni.traveladventureapp.database.DBRoute;
import com.androidproject.owni.traveladventureapp.lib.DatabaseManager;

import org.junit.Before;
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

@RunWith(AndroidJUnit4.class)
public class RoutesActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<RoutesActivity> ruleRA  = new  ActivityTestRule<RoutesActivity>(RoutesActivity.class)
    {
        @Override
        protected Intent getActivityIntent() {
            InstrumentationRegistry.getTargetContext();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.putExtra("TITLE", "Test");
            return intent;
        }
        @Override
        protected void beforeActivityLaunched() {
            Realm.init(InstrumentationRegistry.getTargetContext());
        }
    };

    @Test
    public void ensureListIsPresent() throws Exception {

    RoutesActivity activity = ruleRA.getActivity();
    View viewById = activity.findViewById(R.id.routes_items);
    assertNotNull(viewById);
    assertTrue(viewById instanceof RecyclerView);
    RecyclerView recycler = (RecyclerView) viewById;
    RecyclerView.Adapter adapter = recycler.getAdapter();
    assertNotNull(adapter);
    assertTrue(adapter.getItemCount() >= 0);
}

    @Test
    public void ensureIntentDataIsDisplayed()throws Exception {

        RoutesActivity activity = ruleRA.getActivity();
        assertEquals(activity.getTitle().toString(), "Test");
    }

}
