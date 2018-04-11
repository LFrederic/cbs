package com.example.cbs.cbs.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

public class GPSLocalisationService extends Service {
    private static final String TAG = "GpsService";
    private static final int LOCATION_INTERVAL = 10;
    private static final float LOCATION_DISTANCE = 1f;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;
    private double testLatitude = 0;
    private double testLongitude = 0;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        String adresse = intent.getStringExtra("adresse");
        String sub = adresse.substring(10, adresse.length() - 1);
        String[] latlong = sub.split(",");
        testLatitude = Double.parseDouble(latlong[0]);
        testLongitude = Double.parseDouble(latlong[1]);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreateGPS");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(GPSLocalisationService.this, "Arrêt du service GPSLocalisationService", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onDestroyGPS");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        private LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            float[] distance = new float[1];
            Location.distanceBetween(latitude, longitude, testLatitude, testLongitude, distance);
            // La distance est donnée en mètres. distance[0]
            String res = Float.toString(distance[0]);

            //TODO TEST A MODIFIER UNE FOIS QUON AURA DE VRAIES VALEURS
            if (distance[0] < 10.0) {
                Toast.makeText(GPSLocalisationService.this, "Vous êtes arrivé à destination", Toast.LENGTH_SHORT).show();
                Log.e("GPSUpdate", "Je suis bien arrivé, je suis à :" + res + "mètres de chez moi");
                Intent intent = new Intent();
                intent.setAction("com.example.broadcast.GPS_NOTIFICATION");
                intent.putExtra("isArrived",true);
                sendBroadcast(intent);
            } else {
                Log.e("GPSUpdate", "Je suis pas encore arrivé, je suis à :" + res + "mètres de chez moi");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}