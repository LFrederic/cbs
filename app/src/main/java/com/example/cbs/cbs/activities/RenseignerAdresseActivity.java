package com.example.cbs.cbs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;

public class RenseignerAdresseActivity extends FragmentActivity implements OnMapReadyCallback {

    private final Integer PLACE_PICKER_REQUEST = 1;
    private GoogleMap mMap;
    private CharSequence actualPlace = "";
    private LatLng actualLatLng;
    private float zoomLevel = 16.0f; //This goes up to 21
    private ArrayList<String> phoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renseigner_adresse);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initActualParams();
    }

    public void validerAdresse(View view) {
        Intent intent = new Intent(RenseignerAdresseActivity.this, RenseignerHeureArriveeActivity.class);
        intent.putStringArrayListExtra("phoneNumbers",phoneNumbers);
        intent.putExtra("adresse",actualLatLng.toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                actualPlace = place.getAddress();
                actualLatLng = place.getLatLng();
                updatePlace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(actualLatLng)
                .title("Actual Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLatLng, zoomLevel));
    }

    public void modifierAdresse(View view) {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(RenseignerAdresseActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void updatePlace() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(actualLatLng)
                .title("Actual Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLatLng, zoomLevel));

        TextView defaultAddr = findViewById(R.id.defaultAdresse);
        defaultAddr.setText(actualPlace);
    }

    private void initActualParams() {
        Intent intent = getIntent();
        phoneNumbers = intent.getStringArrayListExtra("phoneNumbers");
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getString("defaultAdresse", "").equals("")) {
            String defaultAdr = prefs.getString("defaultAdresse", "");
            TextView defaultAddr = findViewById(R.id.defaultAdresse);
            defaultAddr.setText(defaultAdr);
        }
        if (!prefs.getString("defaultLatLng", "").equals("")) {
            String defaultAdr = prefs.getString("defaultLatLng", "");
            String sub = defaultAdr.substring(10, defaultAdr.length() - 1);
            String[] latlong = sub.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            actualLatLng = new LatLng(latitude, longitude);
        } else {
            actualLatLng = new LatLng(44.836151, -0.580816);
        }

    }

}
