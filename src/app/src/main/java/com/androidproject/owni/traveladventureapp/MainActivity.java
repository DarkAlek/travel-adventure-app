package com.androidproject.owni.traveladventureapp;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.androidproject.owni.traveladventureapp.database.DBLocation;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends FragmentActivity implements TravelMapFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProbeLocalizationService.startServiceProbing(this);

        setContentView(R.layout.activity_main);

        TravelMapFragment mapFragment = new TravelMapFragment();
        //mapFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.current_travel:
                showCurrentTravel();
                return true;
            case R.id.travel_list:
                showTravelList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCurrentTravel() {
        TravelMapFragment mapFragment = new TravelMapFragment();
        //mapFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();
    }

    public void showTravelList() {
        // TODO
        // show travel list
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        // to do if needed interactions
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}