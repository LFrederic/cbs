package com.example.cbs.cbs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cbs.cbs.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParametreDefautActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int RESULT_PICK_CONTACT = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final float zoomLevel = 16.0f;
    //Variable globales à mettre dans les SharedPreferences
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_param);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initDefaultVar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                case PLACE_PICKER_REQUEST:
                    locationPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        initMapLocation();
    }

    public void launchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    public void launchPlacePicker(View view) {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(ParametreDefautActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void locationPicked(Intent data) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final TextView displayDefaultAddr = findViewById(R.id.defaultAddrDisplay);
        String addr = "";
        LatLng actualLatLng;

        Place place = PlacePicker.getPlace(this, data);
        mMap.clear();
        addr = (String) place.getAddress();
        actualLatLng = place.getLatLng();
        mMap.addMarker(new MarkerOptions()
                .position(actualLatLng)
                .title("Actual Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLatLng, zoomLevel));

        SharedPreferences.Editor editor = prefs.edit();
        String defaultAddr = "Adresse actuelle par défaut :  + " + addr;
        displayDefaultAddr.setText(defaultAddr);
        editor.putString("defaultAdresse", addr);
        editor.putString("defaultLatLng", actualLatLng.toString());
        editor.apply();
    }

    private void contactPicked(Intent data) {
        String phoneNumber = "";
        String name = "";
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final TextView displayDefaultNumero = findViewById(R.id.defaultNumDisplay);
        SharedPreferences.Editor editor = prefs.edit();

        Uri uri = data.getData();
        if (uri != null) {
            Cursor cursor;
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phoneNumber = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);
                cursor.close();
            }
        }

        String defaultPerson = name + " " + phoneNumber;
        displayDefaultNumero.setText(defaultPerson);
        editor.putString("defaultNum", phoneNumber);
        editor.putString("defaultName", name);
        editor.apply();
    }

    private void initMapLocation() {
        String actualLatLng2 = PreferenceManager.getDefaultSharedPreferences(this).getString("defaultLatLng", "");
        if (!"".equals(actualLatLng2)) {
            mMap.clear();
            String sub = actualLatLng2.substring(10, actualLatLng2.length() - 1);
            String[] latlong = sub.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng location = new LatLng(latitude, longitude);

            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Default Place"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
        }

    }

    private void initDefaultVar() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final TextView displayDefaultNumero = findViewById(R.id.defaultNumDisplay);
        final TextView displayDefaultAddr = findViewById(R.id.defaultAddrDisplay);
        String defaultPerson = prefs.getString("defaultNum", "") + " " + prefs.getString("defaultName", "");
        displayDefaultNumero.setText(defaultPerson);
        String defaultAddr = "Adresse actuelle par défaut :  + " + prefs.getString("defaultAdresse", "");
        displayDefaultAddr.setText(defaultAddr);
    }
}