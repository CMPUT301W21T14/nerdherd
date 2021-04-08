package com.example.nerdherd.Model;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class Region {
    private int range;
    private GeoPoint coordinates;
    private String description;
    private Location l;

    public Region() {

    }

    public Region(String description, GeoPoint coordinates, int range) {
        this.description = description;
        this.coordinates = coordinates;
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public String getDescription() {
        return description;
    }
}
