package com.example.cbs.cbs.Models;
import com.google.android.gms.maps.model.LatLng;

public class Adresse {

    String adresse;
    LatLng latLng;

    public Adresse(String adresse, LatLng latLng) {
        this.adresse = adresse;
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getAdresse() {
        return adresse;
    }
}
