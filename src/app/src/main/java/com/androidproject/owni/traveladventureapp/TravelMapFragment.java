package com.androidproject.owni.traveladventureapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidproject.owni.traveladventureapp.database.DBLocation;
import com.androidproject.owni.traveladventureapp.database.DBPhoto;
import com.androidproject.owni.traveladventureapp.database.DBRoute;

import com.androidproject.owni.traveladventureapp.lib.ImageIconFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class TravelMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    OnFragmentInteractionListener mListener;
    GoogleMap mGoogleMap = null;
    Location mLastLocation = null;
    Marker currentMarker = null;
    Messenger mService = null;
    Realm realm;
    boolean mIsBound;
    String routeID;
    double currentDistance = 0.0;
    double currentHighestAltitude = Double.MIN_VALUE;
    private View rootView;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    HashMap<Marker, String> mMarkers = new HashMap<Marker, String>();

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ProbeLocalizationService.MSG_LOCALIZATION:
                    // Updating map current location on message from background service
                    Location location = (Location) msg.obj;

                    DBRoute dbRoute = realm.where(DBRoute.class).equalTo("id", routeID).findFirst();

                    if (dbRoute != null && dbRoute.getIsRunning() == Boolean.FALSE)
                        break;

                    if (dbRoute.getRoute().where().equalTo("geoWidth", location.getLatitude()).equalTo("geoHeight", location.getLongitude()) == null)
                        break;

                    /*
                    if (mLastLocation == null) {
                        centerAtLocation(location);
                    }
                    */

                    /*
                    if (mLastLocation != null) {
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(location.getLatitude(), location.getLongitude())));
                    }
                    */

                    if(currentMarker != null) {
                        currentMarker.remove();
                    }
                    //addPointToMap(location);
                    currentMarker = addCurrentPositionToMap(location);
                    if(mLastLocation == null) return;
                    PolylineOptions line = new PolylineOptions();
                    line.color(Color.BLUE);
                    line.width(10);
                    line.visible(true);
                    line.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    line.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    mGoogleMap.addPolyline(line);
                    mLastLocation = location;

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void addPhotoMarker(DBPhoto dbPhoto) {
        // TODO
        // show photo on map, close to its location
        BitmapDescriptor icon = ImageIconFactory.createIconForPhoto(dbPhoto);

        LatLng myPoint = new LatLng(dbPhoto.getLocation().getGeoWidth(), dbPhoto.getLocation().getGeoHeight());
        //mGoogleMap.addCircle(new CircleOptions().center(myPoint).radius(3).fillColor(Color.RED).strokeColor(Color.RED));

        MarkerOptions marker = new MarkerOptions();
        marker.position(myPoint);
        marker.anchor((float)-0.5,(float)-0.5);
        marker.icon(icon);

        Marker marker_obj = mGoogleMap.addMarker(marker);
        mMarkers.put(marker_obj, dbPhoto.getPath());
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // handle photo click here
        if (mMarkers.containsKey(marker))
        {
            String path_to_image= mMarkers.get(marker);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File photoFile = new File(path_to_image);
            Uri photoUri = FileProvider.getUriForFile(
                    getContext().getApplicationContext(),
                    getContext().getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile
                    );

            intent.setDataAndType(photoUri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }

        return true;
    }

    void onCircleCLick(LatLng position) {
        /*
        LatLng center = circle.getCenter();
        double radius = circle.getRadius();
        float[] distance = new float[1];
        Location.distanceBetween(position.latitude, position.longitude, center.latitude, center.longitude, distance);
        boolean clicked = distance[0] < radius;
        */
    }

    private void loadCurrentRoute() {

        DBRoute route = realm.where(DBRoute.class).equalTo("id", routeID).findFirst();
        if(route == null){
            mGoogleMap.clear();
            return;
        }
        RealmResults<DBLocation> routePoints = route.getRoute().sort("timestamp");

        PolylineOptions line = new PolylineOptions();
        line.color(Color.BLUE);
        line.width(5);
        line.visible(true);

        for (int i = 0; i < routePoints.size(); i++) {
            DBLocation location = routePoints.get(i);

            Location aLocation = new Location("");
            aLocation.setLongitude(location.getGeoHeight());
            aLocation.setLatitude(location.getGeoWidth());

            float[] results = new float[1];

            if (mLastLocation != null) {
                Location.distanceBetween(aLocation.getLatitude(), aLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), results);
                currentDistance += results[0];
            }

            double altitude = location.getAltitude();

            if (altitude != Double.NaN)
                currentHighestAltitude = Math.max(altitude, currentHighestAltitude);

            line.add(new LatLng(location.getGeoWidth(), location.getGeoHeight()));
            //addPointToMap(aLocation);
            mLastLocation = aLocation;
        }

        loadInfoValues();
        mGoogleMap.addPolyline(line);

        RealmResults<DBPhoto> photos = realm.where(DBPhoto.class).equalTo("route.id", routeID).findAll();

        for(int i = 0; i < photos.size(); ++i){
            DBPhoto photo = photos.get(i);
            DBLocation location = photo.getLocation();

            mGoogleMap.addCircle(new CircleOptions().center(new LatLng(location.getGeoWidth(), location.getGeoHeight())).radius(3).fillColor(Color.RED).strokeColor(Color.RED).zIndex(99));
        }

        centerAtLocation(mLastLocation);
    }

    private void centerAtLocation(Location location) {
        if(location == null) return;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    private void addPointToMap(Location location) {
        mGoogleMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude())).radius(3).fillColor(Color.BLUE).strokeColor(Color.BLUE));
    }

    private Marker addCurrentPositionToMap(Location location) {
        //mGoogleMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude())).radius(3).fillColor(Color.BLUE).strokeColor(Color.BLUE));
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        return mGoogleMap.addMarker(new MarkerOptions()
                .title("Current position")
                .snippet("Your sync position during travel.")
                .position(currentPosition));
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null,
                        ProbeLocalizationService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    void doBindService() {
        getActivity().bindService(new Intent(getActivity().getApplicationContext(),
                ProbeLocalizationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            ProbeLocalizationService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }

    public TravelMapFragment() {
        // Required empty public constructor
    }

    public static TravelMapFragment newInstance() {
        TravelMapFragment fragment = new TravelMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getActivity().getApplicationContext());
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        realm = Realm.getDefaultInstance();
        Bundle args = getArguments();
        // showing chosen route
        if(args != null) {
            routeID = args.getString("ROUTES_ID");
        }

        doBindService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double clickAccuracy = 0.0002;
                double latMin = latLng.latitude - clickAccuracy;
                double latMax = latLng.latitude + clickAccuracy;
                double lonMin = latLng.longitude - clickAccuracy;
                double lonMax = latLng.longitude + clickAccuracy;

                RealmResults<DBPhoto> photos = realm.where(DBPhoto.class).equalTo("route.id", routeID).findAll();

                DBPhoto foundPhoto = null;
                for(int i = 0; i < photos.size(); ++i) {
                    DBLocation location = photos.get(i).getLocation();
                    if (location.getGeoWidth() > latMin && location.getGeoWidth() < latMax && location.getGeoHeight() > lonMin && location.getGeoHeight() < lonMax)
                    {
                        foundPhoto = photos.get(i);
                        break;
                    }
                }

                //DBPhoto photo = realm.where(DBPhoto.class).between("location.geoWidth", latMin, latMax).between("location.geoHeight", lonMin, lonMax).findFirst();
                if (foundPhoto != null)
                    addPhotoMarker(foundPhoto);

                // search for closest circle here
                /*
                LatLng center = circle.getCenter();
                double radius = circle.getRadius();
                float[] distance = new float[1];
                Location.distanceBetween(position.latitude, position.longitude, center.latitude, center.longitude, distance);
                boolean clicked = distance[0] < radius;
                */
                // end of searching
            }
        });

        if (mLastLocation != null) {
            addPointToMap(mLastLocation);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13));
        }

        //loadAllPointsToMap();
        if(routeID != null) {
            loadCurrentRoute();
            loadInfoValues();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment smf = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        smf.getMapAsync(this);

        loadInfoValues();
    }

    public void loadInfoValues(){
        TextView distanceView = (TextView) getView().findViewById(R.id.distance_textview);
        TextView date_start = (TextView) getView().findViewById(R.id.date_start_textview);
        TextView altitudeView = (TextView) getView().findViewById(R.id.highest_textview);
        TextView time_elapsed = (TextView) getView().findViewById(R.id.time_elapsed_textview);
        DBRoute dbRoute = realm.where(DBRoute.class).equalTo("id", routeID).findFirst();

        if (dbRoute == null) {
            distanceView.setText("-");
            date_start.setText("-");
            altitudeView.setText("-");
            time_elapsed.setText("-");

            return;
        }

        String distanceShowed = new DecimalFormat("#.#").format(currentDistance/1000);
        distanceView.setText(distanceShowed + "km");

        String highestShowed = new DecimalFormat("#.##").format(currentHighestAltitude);
        altitudeView.setText(highestShowed + "m");

        DateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        Date netDate = (new Date(dbRoute.getTimestamp()));

        // TODO
        if(!dbRoute.getRoute().sort("id").isEmpty()) {

            Date end_date = new Date(dbRoute.getRoute().sort("id").last().getTimestamp() * 1000);
            date_start.setText(sdf.format(netDate));

            long diff = (end_date.getTime() - netDate.getTime()) / 1000;
            long days = diff / (3600 * 24);
            long hours = (diff - days * 3600 * 24) / 3600;
            long mins = (diff - days * 24 * 3600 - hours * 3600) / 60;

            String time_elapsed_str = "";

            if (days > 0)
                time_elapsed_str += days + " days ";
            if (hours > 0)
                time_elapsed_str += hours + " h ";
            if (mins > 0)
                time_elapsed_str += mins + " m";

            if (time_elapsed_str.isEmpty())
                time_elapsed_str = "Just started";

            time_elapsed.setText(time_elapsed_str);
        }
    }
}
