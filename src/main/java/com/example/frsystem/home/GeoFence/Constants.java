package com.example.frsystem.home.GeoFence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public final class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "com.example.frsystem.home.GeoFence";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 500; // 1 mile = 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
     public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    static {
        // San Francisco International Airport.
        BAY_AREA_LANDMARKS.put("NRB", new LatLng(-1.304954, 36.815956));

        // Googleplex.
        BAY_AREA_LANDMARKS.put("RONGAI", new LatLng(-1.401014,36.755643));
    }
}
