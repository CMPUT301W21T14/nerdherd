package com.example.nerdherd;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.security.Provider;

public class GPSTracker extends Provider.Service implements LocationListener {

    private final Context mContext;
    boolean isGPSTrackingEnabled = false;

    double latitude;
    double longitude;


    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }



    public void getLocation() {

    }

    public boolean getIsGPSTrackingEnabled() {
        return this.isGPSTrackingEnabled;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
