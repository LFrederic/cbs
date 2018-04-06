package com.example.cbs.cbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RenseignerNumeroActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 1;
    //Variable globales à mettre dans les SharedPreferences
    String phoneNumber = "";
    String name = "";
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
                phoneNumbers.add(phoneNumber);
                names.add(name);
                displayContactList();
                cursor.close();
            }
        }
    }

    public void displayContactList(){
        TextView numDisplay = findViewById(R.id.numDisplay);
        String contactsList = "";
        for(int i =0; i<phoneNumbers.size();i++){
            contactsList += "+ "+ names.get(i)+ " "+ phoneNumbers.get(i) +"\n";
        }
        numDisplay.setText(contactsList);

        /**
        //Permet de lancer l'activité ChoisirAdresseRDVActivity en passant en paramètre la contactsList
        Intent intentChoisirAdresseRDVActicity =  new Intent(this, ChoisirAdresseRDVActivity.class);
        Log.i("contactsList", "RenseignerNumeroActivity -> contactsList : " + contactsList);
        intentChoisirAdresseRDVActicity.putExtra("contactsList", contactsList);
        startActivity(intentChoisirAdresseRDVActicity);
        */
    }
}
