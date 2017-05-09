package com.androidproject.owni.traveladventureapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidproject.owni.traveladventureapp.Database.DBRoute;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
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
            //View v = View.inflate(parent.getContext(), R.layout.route_item, null);
            return new RouteViewHolder(v, viewType,
                    (CardView) v.findViewById(R.id.route_card),
                    (TimelineView) v.findViewById(R.id.route_marker),
                    (TextView)v.findViewById(R.id.route_name),
                    (TextView) v.findViewById(R.id.route_date)
            );
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final DBRoute currentItem = dataSet.get(position);
            RouteViewHolder h = (RouteViewHolder)holder;
            h.routeName.setText(currentItem.getName());
            h.routeDate.setText(String.valueOf(currentItem.getTimestamp()));

            // operacje na obiektach Routes

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

        DBRoute routeItem = new DBRoute();
        routeItem.setName("Route");
        routeItem.setTimestamp(1);
        dataSet.add(routeItem);
        dataSet.add(routeItem);
        dataSet.add(routeItem);
        dataSet.add(routeItem);
        dataSet.add(routeItem);
        dataSet.add(routeItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        if(getIntent().hasExtra("TITLE")){
            setTitle(getIntent().getStringExtra("TITLE"));
        }

        routesItems = (RecyclerView)findViewById(R.id.routes_items);
        routesItems.setLayoutManager(new LinearLayoutManager(this));

        initializeDataSet();
        routesItems.setAdapter(routesItemsAdapter);
    }
}
