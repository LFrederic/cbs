package com.example.cbs.cbs.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.cbs.cbs.R;

public class MainActivity extends AppCompatActivity {

    private final Integer REQUEST_CODE_FINE_GPS = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();
    }

    /**
     * Demande de permissions à l'utilisateur pour l'accès à sa position GPS, l'envoi de SMS et accès aux contacts
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE_FINE_GPS);
    }

    public void goToParametreDefautActivity(View view) {
        startActivity(new Intent(MainActivity.this, ParametreDefautActivity.class));
    }

    public void goToRenseignerNumeroActivity(View view) {
        startActivity(new Intent(MainActivity.this, RenseignerNumeroActivity.class));
    }
}
