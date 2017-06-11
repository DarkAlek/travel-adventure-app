package com.androidproject.owni.traveladventureapp;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;
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
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by Alek on 2017-06-11.
 */

@RunWith(AndroidJUnit4.class)
public class LocalizationServiceInstrumentedTest {

    @Rule
    public final ServiceTestRule ruleService = new ServiceTestRule().withTimeout(60L, TimeUnit.SECONDS);


    @Test
    public void ensureStartService() throws Exception {

        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), ProbeLocalizationService.class);
        ruleService.startService(serviceIntent);

    }

    @Test
    public void ensureBoundService() throws Exception {

        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), ProbeLocalizationService.class);
        IBinder binder = ruleService.bindService(serviceIntent);

    }

}
