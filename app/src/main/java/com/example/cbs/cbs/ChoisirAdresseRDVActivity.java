package com.example.cbs.cbs;

import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChoisirAdresseRDVActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choisir_adresse_rdv);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Récupère la liste des contacts passée par l'activity RenseignerNumeroActivity
        Intent intent = getIntent();
        contactsList = intent.getStringExtra("contactsList");
        Log.i("contactsList", "ChoisirAdresseRDVActivity -> contactsList : " + contactsList);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        //Permet d'ajouter un marker sur la map lorsque l'on clique dessus
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                Marker markePointRDV = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("title")
                        .snippet("snippet"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                markePointRDV.showInfoWindow();

                //TODO Transformer une latitude et un longitude en une adresse grace à l'API Google
                /**
                 *  Permet de connaitre une adresse à partir d'une latitude et d'une longitude : https://developers.google.com/maps/documentation/geocoding/start?hl=fr
                 *  https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=AIzaSyDAX7lTjLLLk1bA1uf6mZaRDfThjhMiCJA
                 */
            }
        });


        //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyDAX7lTjLLLk1bA1uf6mZaRDfThjhMiCJA

    }

    public void validerAdresse(View v)
    {
        //TODO gérer la validation de l'adresse
        Log.i("validerAdresse", "bouton appuyé");
    }

}
