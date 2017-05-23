package com.androidproject.owni.traveladventureapp;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.androidproject.owni.traveladventureapp.Database.DBLocation;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends FragmentActivity implements TravelMapFragment.OnFragmentInteractionListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askAboutLocalizationPermissions();

        ProbeLocalizationService.startServiceProbing(this);

        setContentView(R.layout.activity_main);

        TravelMapFragment mapFragment = new TravelMapFragment();
        //mapFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();

        initNavigationView();

        if(getIntent().hasExtra("ROUTES_ID")) {

            Bundle args = new Bundle();
            args.putString("ROUTES_ID", getIntent().getExtras().getString("ROUTES_ID"));
            mapFragment.setArguments(args);
        }

    }

    public void askAboutLocalizationPermissions() {

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, 23 );}
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, 23 ); }
    }

    public void initNavigationView(){

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                menuItem.setChecked(true);
                switch (id) {
                    case R.id.current_travel:
                        showCurrentTravel();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.travel_list:
                        showTravelList();
                        drawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });
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
        Intent i = new Intent(MainActivity.this, RoutesActivity.class);
        i.putExtra("TITLE", "Routes");
        startActivityForResult(i, 1);
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