package com.androidproject.owni.traveladventureapp;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    GoogleApiClient mGoogleApiClient = null;
    Location mLastLocation = null;
    LocationRequest mLocationRequest = null;
    GoogleMap mGoogleMap = null;
    //Boolean mRequestingLocationUpdates = true;

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e("TAG", "MAP READY HERE");
        mGoogleMap = map;

        if (mLastLocation != null) {
            map.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Marker"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10));
        }
    }

    protected void startLocationUpdates() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    23 );
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    23 );
        }

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        catch(SecurityException e)
        {
            Log.d("TAG", "startLocationUpdatesError: ");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("RAN", "onCreate: ");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d("TAG", "onConnected: " + mLastLocation.getLatitude());
                Log.d("TAG", "onConnected: " + mLastLocation.getLongitude());

                //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            } else {
                Log.d("TAG", "aa: ");
            }
        }
        catch (SecurityException e){
            Log.d("TAG", "aa: ");
        }

        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        Log.d("TAG", "onStart: ");

        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        Log.d("TAG", "onStop: ");

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.e("TAG", "onLocationChanged: ");

        Log.e("TAG", "onConnected NOTError: " + String.valueOf(mLastLocation.getLatitude()));
        Log.e("TAG", "onConnected NOTError: " + String.valueOf(mLastLocation.getLongitude()));

        if (mGoogleMap != null) {
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Marker"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
        }
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //updateUI();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("TAG", "onConnected: ");

        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        Log.d("TAG", "onConnected: " + result.toString());

        builder.setAlwaysShow(true);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        Log.d("TAG", "Satisfied: ");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        Log.d("TAG", "NotSatisfied: ");

                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

        startLocationUpdates();

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
        catch (SecurityException e)
        {
            Log.d("TAG", "onConnected Error: ");
        }

        if (mLastLocation != null) {
            Log.e("TAG", "onConnected NOTError: " + String.valueOf(mLastLocation.getLatitude()));
            Log.e("TAG", "onConnected NOTError: " + String.valueOf(mLastLocation.getLongitude()));

            if (mGoogleMap != null)
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Marker"));
        }

        Log.d("TAG", "onConnected: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
*/
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int time) {
        Log.d("TAG", "onConnectionSuspended:");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("TAG", "onConnectionSuspended:");
    }

}