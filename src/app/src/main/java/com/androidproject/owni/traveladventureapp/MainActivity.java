package com.androidproject.owni.traveladventureapp;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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
    public  void onFragmentInteraction(Uri uri){
        // to do if needed interactions
    }
}