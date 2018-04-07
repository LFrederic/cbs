package com.example.cbs.cbs.actitivties;

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
import android.widget.Button;
import android.widget.TextView;

import com.example.cbs.cbs.R;
import com.example.cbs.cbs.services.SmsService;

import java.util.ArrayList;

public class RenseignerNumeroActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 1;
    //Variable globales à mettre dans les SharedPreferences
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renseigner_numero);

        Button btnContact = findViewById(R.id.btnContact);
        Button btnValidate = findViewById(R.id.btnValidate);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getString("defaultNum", "").equals("")) {
            String defaultNum = prefs.getString("defaultNum", "");
            String defaultName = prefs.getString("defaultName", "");
            phoneNumbers.add(defaultNum);
            names.add(defaultName);
            displayContactList();
        }

        btnContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }


        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent smsIntent = new Intent(RenseignerNumeroActivity.this, SmsService.class);
                smsIntent.putStringArrayListExtra("phoneNumbers", phoneNumbers);
                startService(smsIntent);
                startActivity(new Intent(RenseignerNumeroActivity.this, RenseignerAdresseActivity.class));
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

    public void displayContactList() {
        TextView numDisplay = findViewById(R.id.numDisplay);
        String contactsList = "";
        for (int i = 0; i < phoneNumbers.size(); i++) {
            contactsList += "+ " + names.get(i) + " " + phoneNumbers.get(i) + "\n";
        }
        numDisplay.setText(contactsList);
    }
}
