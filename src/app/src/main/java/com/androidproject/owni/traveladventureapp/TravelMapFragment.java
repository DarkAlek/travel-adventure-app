package com.androidproject.owni.traveladventureapp;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidproject.owni.traveladventureapp.database.DBLocation;
import com.androidproject.owni.traveladventureapp.database.DBRoute;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class TravelMapFragment extends Fragment implements OnMapReadyCallback {

    OnFragmentInteractionListener mListener;
    GoogleMap mGoogleMap = null;
    Location mLastLocation = null;
    Messenger mService = null;
    Realm realm;
    boolean mIsBound;
    String routeID;
    double currentDistance = 0.0;
    private View rootView;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

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

                    if (mLastLocation == null) {
                        centerAtLocation(location);
                    }

                    if (mLastLocation != null) {
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(location.getLatitude(), location.getLongitude())));
                    }

                    addPointToMap(location);
                    mLastLocation = location;

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void loadCurrentRoute() {

        DBRoute route = realm.where(DBRoute.class).equalTo("id", routeID).findFirst();
        if(route == null){
            mGoogleMap.clear();
            return;
        }
        RealmResults<DBLocation> routePoints = route.getRoute().sort("timestamp");

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

            addPointToMap(aLocation);
            mLastLocation = aLocation;
        }

        loadInfoValues();
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
        distanceView.setText(Double.toString(currentDistance) + "km");
        distanceView.setText("KUTAS");
    }
}
