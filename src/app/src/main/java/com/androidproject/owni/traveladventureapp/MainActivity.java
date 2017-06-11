package com.androidproject.owni.traveladventureapp;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;

import com.androidproject.owni.traveladventureapp.database.DBLocation;
import com.androidproject.owni.traveladventureapp.database.DBPhoto;
import com.androidproject.owni.traveladventureapp.database.DBRoute;
import com.androidproject.owni.traveladventureapp.lib.DatabaseManager;
import com.androidproject.owni.traveladventureapp.lib.PhotosManager;
import com.google.android.gms.maps.MapFragment;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;


public class MainActivity extends FragmentActivity implements TravelMapFragment.OnFragmentInteractionListener {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mLastPhotoPath;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private DBRoute activeRoute;
    private TravelMapFragment mapFragment;
    public DBRoute getActiveRoute() {
        return activeRoute;
    }
    public void setActiveRoute(DBRoute activeRoute) {
        this.activeRoute = activeRoute;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askAboutLocalizationPermissions();

        ProbeLocalizationService.startServiceProbing(this);

        setContentView(R.layout.activity_main);

        mapFragment = new TravelMapFragment();
        //mapFragment.setArguments(args);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();

        DatabaseManager db = new DatabaseManager(getApplicationContext());
        final Realm realm = db.getRealmObject();

        RealmQuery<DBRoute> query = realm.where(DBRoute.class);
        query.equalTo("isRunning", Boolean.TRUE);
        activeRoute = query.findFirst();

        initNavigationView();

        if(getIntent().hasExtra("ROUTES_ID")) {
            Bundle args = new Bundle();
            args.putString("ROUTES_ID", getIntent().getExtras().getString("ROUTES_ID"));
            mapFragment.setArguments(args);
        }
        else if (activeRoute != null){
            Bundle args = new Bundle();
            args.putString("ROUTES_ID", activeRoute.getId());
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

        Menu nav_Menu = navigationView.getMenu();
        if(activeRoute == null) nav_Menu.findItem(R.id.current_travel).setEnabled(false);
        else nav_Menu.findItem(R.id.current_travel).setEnabled(true);
    }


    public void startNewTravelDialog() {
        DatabaseManager db = new DatabaseManager(getApplicationContext());
        final Realm realm = db.getRealmObject();

        if (activeRoute != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Do you want to stop current trip?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    realm.beginTransaction();
                    activeRoute.setIsRunning(Boolean.FALSE);
                    //realm.copyToRealmOrUpdate(dbRoute);
                    realm.commitTransaction();
                    activeRoute = null;
                    if(activeRoute == null) navigationView.getMenu().findItem(R.id.current_travel).setEnabled(false);
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
            public void onClick(DialogInterface dialog, int which) { }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(input.getText().toString().isEmpty()) {
                    input.setError("Enter route name");
                }
                else {
                    addNewTravel(input.getText().toString());
                    dialog.dismiss();
                }
            }
        });
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
        realm.insertOrUpdate(dbRoute);
        activeRoute = dbRoute;
        realm.commitTransaction();
        if(activeRoute != null) navigationView.getMenu().findItem(R.id.current_travel).setEnabled(true);
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

        if(activeRoute == null) return;
        Bundle args = new Bundle();
        args.putString("ROUTES_ID", activeRoute.getId());
        mapFragment.setArguments(args);
    }

    public void showTravelList() {
        Intent i = new Intent(MainActivity.this, RoutesActivity.class);
        i.putExtra("TITLE", "Routes");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void takePhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            PhotosManager photos = new PhotosManager(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            try {
                photoFile = photos.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        getApplicationContext(),
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mLastPhotoPath = photoFile.getAbsolutePath();
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            DatabaseManager db = new DatabaseManager(getApplicationContext());
            final Realm realm = db.getRealmObject();

            realm.beginTransaction();
            Long timestamp = System.currentTimeMillis() / 1000;
            DBPhoto dbPhoto = new DBPhoto();
            dbPhoto.setId(timestamp);
            dbPhoto.setTimestamp(timestamp);
            dbPhoto.setRoute(activeRoute);
            dbPhoto.setPath(mLastPhotoPath);
            RealmResults<DBLocation> routePoints = activeRoute.getRoute().sort("timestamp");
            DBLocation location = routePoints.last();
            dbPhoto.setLocation(location);
            realm.insertOrUpdate(dbPhoto);
            realm.commitTransaction();

            mapFragment.addPhotoMarker(dbPhoto);
        }
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
