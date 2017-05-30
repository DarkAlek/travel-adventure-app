package com.androidproject.owni.traveladventureapp;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.MenuItem;

import com.androidproject.owni.traveladventureapp.database.DBLocation;
import com.androidproject.owni.traveladventureapp.database.DBRoute;
import com.androidproject.owni.traveladventureapp.lib.DatabaseManager;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import android.widget.EditText;

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
                    case R.id.start_travel:
                        startNewTravelDialog();
                        drawerLayout.closeDrawers();
                        return true;
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

    public void startNewTravelDialog() {
        DatabaseManager db = new DatabaseManager(getApplicationContext());
        final Realm realm = db.getRealmObject();

        realm.beginTransaction();
        RealmQuery<DBRoute> query = realm.where(DBRoute.class);
        query.equalTo("isRunning", Boolean.TRUE);
        final DBRoute dbRoute = query.findFirst();
        realm.commitTransaction();

        if (dbRoute != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Do You want to stop current trip?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    realm.beginTransaction();
                    dbRoute.setIsRunning(Boolean.FALSE);
                    //realm.copyToRealmOrUpdate(dbRoute);
                    realm.commitTransaction();

                    showAddNewTravelDialog();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
            showAddNewTravelDialog();

    }

    public void showAddNewTravelDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Insert name of new trip:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewTravel(input.getText().toString());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void addNewTravel(String travelName){
        DatabaseManager db = new DatabaseManager(MainActivity.this);
        Realm realm = db.getRealmObject();

        realm.beginTransaction();
        Long timestamp = System.currentTimeMillis()/1000;
        DBRoute dbRoute = new DBRoute();
        dbRoute.setId(timestamp.toString());
        dbRoute.setTimestamp(System.currentTimeMillis());
        dbRoute.setName(travelName);
        dbRoute.setIsRunning(Boolean.TRUE);
        realm.insert(dbRoute);
        realm.commitTransaction();

    }

    public void showCurrentTravel() {
        TravelMapFragment mapFragment = new TravelMapFragment();
        //mapFragment.setArguments(args);
        //android.app.FragmentTransaction fragmentTransaction =
        //        getFragmentManager().beginTransaction();
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