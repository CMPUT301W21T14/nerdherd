package com.example.nerdherd;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.security.Provider;
import java.util.List;
import java.util.Map;

public class GPSTracker extends Provider.Service implements LocationListener {

    private Context mContext;
    boolean isGPSTrackingEnabled = false;

    double latitude;
    double longitude;

    /**
     * Construct a new service.
     *
     * @param provider   the provider that offers this service
     * @param type       the type of this service
     * @param algorithm  the algorithm name
     * @param className  the name of the class implementing this service
     * @param aliases    List of aliases or null if algorithm has no aliases
     * @param attributes Map of attributes or null if this implementation
     *                   has no attributes
     * @throws NullPointerException if provider, type, algorithm, or
     *                              className is null
     */
    public GPSTracker(Provider provider, String type, String algorithm, String className, List<String> aliases, Map<String, String> attributes) {
        super(provider, type, algorithm, className, aliases, attributes);
    }

    public GPSTracker(Context context) {
        super(null, null, null, null, null, null);
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
