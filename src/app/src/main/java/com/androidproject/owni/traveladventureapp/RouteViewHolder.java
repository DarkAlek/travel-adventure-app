package com.androidproject.owni.traveladventureapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

/**
 * Created by Alek on 2017-05-09.
 */

public class RouteViewHolder extends RecyclerView.ViewHolder {

    CardView routeCard;
    TimelineView routeMarker;
    TextView routeName;
    TextView routeDate;
    ImageView routeDelete;


    public RouteViewHolder(View itemView, int viewType, CardView routeCard, TimelineView routeMarker, TextView routeName, TextView routeDate, ImageView routeDelete) {
        super(itemView);
        this.routeCard = routeCard;
        this.routeMarker = routeMarker;
        this.routeMarker.initLine(viewType);
        this.routeName = routeName;
        this.routeDate = routeDate;
        this.routeDelete = routeDelete;
    }
}
