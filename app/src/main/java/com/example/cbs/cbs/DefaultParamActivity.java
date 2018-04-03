package com.example.cbs.cbs;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DefaultParamActivity extends Activity {

    private static final int RESULT_PICK_CONTACT = 1;
    String phoneNumber = null ;
    String name = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_param);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final TextView displayDefaultNumero= findViewById(R.id.defaultNumDisplay);

        displayDefaultNumero.setText(prefs.getString("defaultNum", ""));


        findViewById(R.id.paramValidate).setOnClickListener(new View.OnClickListener( ) {
            public void onClick(View v) {
                displayDefaultNumero.setText(name+ " "+ phoneNumber);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("defaultNum", phoneNumber);
                editor.putString("defaultName", name);
                editor.commit();

                Toast.makeText(DefaultParamActivity.this, "Voici le nouveau numéro par défaut : " + prefs.getString("defaultNum", "RIEN"),
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DefaultParamActivity.this, MainActivity.class));
                }
                });


        findViewById(R.id.contact).setOnClickListener(new View.OnClickListener( ) {
            public void onClick(View v){
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
                }



    });

    }


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

    public void contactPicked(Intent data){
        Cursor cursor = null;
        Uri uri = data.getData();
        cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();

        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        phoneNumber = cursor.getString(phoneIndex);
        name = cursor.getString(nameIndex);
    }
}
