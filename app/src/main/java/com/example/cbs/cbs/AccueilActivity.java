package com.example.cbs.cbs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class AccueilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        EditText inputNumero = findViewById(R.id.inputNumero);

        if (!prefs.getString("defaultNum", "").equals("")) {
            String defaultPerson = prefs.getString("defaultNum", "") + " " + prefs.getString("defaultName", "");
            inputNumero.setText(defaultPerson);
        }
    }
}
