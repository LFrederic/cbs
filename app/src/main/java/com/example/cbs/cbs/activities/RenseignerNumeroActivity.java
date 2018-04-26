package com.example.cbs.cbs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cbs.cbs.R;

import java.util.ArrayList;

public class RenseignerNumeroActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 1;
    //Variable globales à mettre dans les SharedPreferences
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();

    //SharedPreferences
    private SharedPreferences prefs;

    //View
    private TextView numDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renseigner_numero);

        initializingView();
        loadSharedPreferences();
        displayContactList();
    }

    private void initializingView() {
        numDisplay = findViewById(R.id.numDisplay);
    }

    private void loadSharedPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getString("nomContact", "").equals("")) {
            String defaultNum = prefs.getString("numeroContact", "");
            String defaultName = prefs.getString("nomContact", "");
            phoneNumbers.add(defaultNum);
            names.add(defaultName);
        }
    }

    public void choisirContact(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    public void validerNumero(View view) {
        Intent i = new Intent(RenseignerNumeroActivity.this, RenseignerAdresseActivity.class);
        i.putStringArrayListExtra("phoneNumbers" ,phoneNumbers);
        startActivity(i);
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
            String phoneNumber;
            String name;
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phoneNumber = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);
                phoneNumbers.add(phoneNumber);
                names.add(name);
                displayContactList();
                cursor.close();
            }
        }
    }

    private void displayContactList() {
        String contactsList = "";
        for (int i = 0; i < phoneNumbers.size(); i++) {
            contactsList += "+ " + names.get(i) + " " + phoneNumbers.get(i) + "\n";
        }
        numDisplay.setText(contactsList);
    }
}
