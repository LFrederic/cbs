package com.example.cbs.cbs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSLocalisationService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    //TODO Il faudra peut-être revoir ces valeurs, j'en ai mis 2 au pif mais je crois que c'est très peu
    //et que du coup dans la vraie vie ça va spam les appels services. Je vous aime
    private static final int LOCATION_INTERVAL = 10;
    private static final float LOCATION_DISTANCE = 10f;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
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
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
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


            //TODO A SUPPRIMER CE SONT DES DONNEES DE TEST
            double testLatitude = -15.0;
            double testLongitude = 52.0;


            float[] distance = new float[1];
            Location.distanceBetween(latitude, longitude, testLatitude, testLongitude, distance);
            // La distance est donnée en mètres. distance[0]
            String res = Float.toString(distance[0]);

            //TODO TEST A MODIFIER UNE FOIS QUON AURA DE VRAIES VALEURS
            if (distance[0] < 500.0) {
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