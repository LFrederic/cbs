package com.example.cbs.cbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class RenseignerAdresseActivity extends FragmentActivity implements OnMapReadyCallback {

    private final Integer PLACE_PICKER_REQUEST = 1;
    private GoogleMap mMap;
    private CharSequence actualPlace = "";
    private LatLng actualLatLng;
    private float zoomLevel = 16.0f; //This goes up to 21

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renseigner_adresse);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO set le actualLatLng par l'adresse par défaut quand on l'aura si elle existe, sinon on met BordeauuuuuuxZooGangCity
        actualLatLng = new LatLng(44.836151, -0.580816);
        TextView defaultAddr = findViewById(R.id.defaultAdresser);
        defaultAddr.setText(actualPlace);

        Button btnModifierAdresse = findViewById(R.id.btnModifierAdresse);
        btnModifierAdresse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO De même ici, on mettra a l'adresse par défaut, sinon DOBOR MA GUEULE LE 443 KEBAB
                LatLng BORDEAUX = new LatLng(-15.627306350103277, 52.24363300949335);
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();


                //POur une raison osbcure, ça ne marche pas.
                double radius = 100;
                LatLng southwest = SphericalUtil.computeOffset(BORDEAUX, radius * Math.sqrt(2.0), 225);
                LatLng northeast = SphericalUtil.computeOffset(BORDEAUX, radius * Math.sqrt(2.0), 45);
                intentBuilder.setLatLngBounds(new LatLngBounds(southwest, northeast));


                try {
                    startActivityForResult(intentBuilder.build(RenseignerAdresseActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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

    private void updatePlace() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(actualLatLng)
                .title("Actual Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLatLng, zoomLevel));

        TextView defaultAddr = findViewById(R.id.defaultAdresser);
        defaultAddr.setText(actualPlace);
    }

}
