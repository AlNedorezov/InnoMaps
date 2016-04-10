package com.innopolis.maps.innomaps.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by neewy on 10.04.16.
 */
public class PolylineWrapper {

    String floor;
    Polyline polyline;
    PolylineOptions polylineOptions;

    public PolylineWrapper(){}

    public PolylineWrapper(String floor, Polyline polyline, PolylineOptions polylineOptions) {
        this.floor = floor;
        this.polyline = polyline;
        this.polylineOptions = polylineOptions;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(GoogleMap map, PolylineOptions options) {
        this.polylineOptions = options;
        this.polyline = map.addPolyline(options);
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public void deleteFromMap() {
        polyline.remove();
    }

    public boolean isEmpty() {
        if (polyline == null) {
            return true;
        } else {
            return false;
        }
    }
}
