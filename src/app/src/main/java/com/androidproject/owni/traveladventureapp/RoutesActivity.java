package com.androidproject.owni.traveladventureapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidproject.owni.traveladventureapp.database.DBRoute;
import com.androidproject.owni.traveladventureapp.database.DBLocation;

import com.github.vipulasri.timelineview.TimelineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class RoutesActivity extends AppCompatActivity {

    private RecyclerView routesItems;

    private Realm realm;

    private List<DBRoute> dataSet;

    private RecyclerView.Adapter routesItemsAdapter = new RecyclerView.Adapter() {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.route_item, parent, false);

            return new RouteViewHolder(v, viewType,
                    (CardView) v.findViewById(R.id.route_card),
                    (TimelineView) v.findViewById(R.id.route_marker),
                    (TextView)v.findViewById(R.id.route_name),
                    (TextView) v.findViewById(R.id.route_date),
                    (ImageView) v.findViewById(R.id.route_delete)
            );
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final DBRoute currentItem = dataSet.get(position);
            RouteViewHolder h = (RouteViewHolder)holder;
            h.routeName.setText(currentItem.getName());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            Date resultDate = new Date(currentItem.getTimestamp());
            h.routeDate.setText(sdf.format(resultDate));
            h.routeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // show route
                    Intent intent = new Intent(RoutesActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("ROUTES_ID", currentItem.getId());
                    startActivity(intent);
                }
            });
            if(currentItem.getIsRunning() == false) {
                h.routeDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(RoutesActivity.this);
                        builder.setMessage("Are you sure to delete?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                realm.beginTransaction();
                                currentItem.deleteFromRealm();
                                realm.commitTransaction();
                                initializeDataSet();
                                routesItemsAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
            else {
                h.routeDelete.setColorFilter(Color.WHITE);
                h.routeMarker.setMarker(getResources().getDrawable(R.drawable.marker_active));
            }
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position,getItemCount());
        }
    };

    private void initializeDataSet() {

        dataSet = new ArrayList<>();
        RealmResults<DBRoute>  routeResults = realm.where(DBRoute.class).findAllSorted("timestamp", Sort.DESCENDING);
        for(DBRoute item:routeResults) dataSet.add(item);
    }

    // to mock objects
    private void mockRoutes() {

        DBRoute routeItem = new DBRoute();
        RealmList<DBLocation> locationsList= new RealmList<DBLocation>();
        locationsList.addAll(realm.where(DBLocation.class).findAllSorted("timestamp", Sort.DESCENDING).subList(0,3));

        routeItem.setName("My Route");
        routeItem.setId(UUID.randomUUID().toString());
        routeItem.setTimestamp(System.currentTimeMillis());
        routeItem.setRoute(locationsList);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(routeItem);
        realm.commitTransaction();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        if(getIntent().hasExtra("TITLE")){
            setTitle(getIntent().getStringExtra("TITLE"));
        }

        realm = Realm.getDefaultInstance();


        routesItems = (RecyclerView)findViewById(R.id.routes_items);
        routesItems.setLayoutManager(new LinearLayoutManager(this));

        //mockRoutes();
        initializeDataSet();
        routesItems.setAdapter(routesItemsAdapter);
    }
}
