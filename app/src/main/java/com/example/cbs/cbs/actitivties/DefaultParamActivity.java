package com.example.cbs.cbs.actitivties;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cbs.cbs.R;

public class DefaultParamActivity extends Activity {

    private static final int RESULT_PICK_CONTACT = 1;
    //Variable globales à mettre dans les SharedPreferences
    String phoneNumber = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_param);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final TextView displayDefaultNumero = findViewById(R.id.defaultNumDisplay);

        String defaultPerson = prefs.getString("defaultNum", "") + " " + prefs.getString("defaultName", "");
        displayDefaultNumero.setText(defaultPerson);

        findViewById(R.id.contact).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }


        });

        findViewById(R.id.paramValidate).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!phoneNumber.equals("")) {
                    String defaultPerson = name + " " + phoneNumber;
                    displayDefaultNumero.setText(defaultPerson);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("defaultNum", phoneNumber);
                    editor.putString("defaultName", name);
                    editor.apply();

                    Toast.makeText(DefaultParamActivity.this, "Voici le nouveau numéro par défaut : " + prefs.getString("defaultNum", ""),
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DefaultParamActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(DefaultParamActivity.this, "Veuillez choisir un contact pour changer le numéro par défaut",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    public void contactPicked(Intent data) {

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
    }
}
