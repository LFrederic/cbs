package com.example.cbs.cbs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cbs.cbs.Models.Adresse;
import com.example.cbs.cbs.Models.Contact;
import com.example.cbs.cbs.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParametreDefautActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int RESULT_PICK_CONTACT = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final float zoomLevel = 16.0f;

    //GoogleMap
    GoogleMap mMap;

    //SharedPreferences
    SharedPreferences prefs;

    //View variables
    TextView text_contact;
    TextView text_adresse;

    EditText edit_code_pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_param);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        initializingView();
        initializingDefaultVariables();
    }

    /**
     * Permet d'initialiser la view
     */
    private void initializingView() {
        text_contact = findViewById(R.id.text_numero_telephone);
        text_adresse = findViewById(R.id.text_adresse);

        edit_code_pin = findViewById(R.id.edit_code_pin);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);

                    String nomContact = prefs.getString("nomContact", null);
                    String numeroContact = prefs.getString("numeroContact", null);

                    text_contact.setText(nomContact + " " + numeroContact);
                    break;
                case PLACE_PICKER_REQUEST:
                    locationPicked(data);

                    String adresse = prefs.getString("adresse", null);

                    text_adresse.setText("Adresse actuelle par défaut :  " + adresse);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        initMapLocation();
    }

    /**
     * Démarre une activité permettant à l'utilisateur de choisir un contact
     * @param view
     */
    public void launchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    /**
     * Démarre une activité permettant à l'utilisateur de choisir un lieu d'arrivée sur la map
     * @param view
     */
    public void launchPlacePicker(View view) {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(ParametreDefautActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour le codePin dans les SharedPreferences et ferme l'activity
     * @param view
     */
    public void updateDefaultParam(View view) {

    if (codePinHasChanged()) {
        String codePin = edit_code_pin.getText().toString();
        updateCodePinSharedPreferences(codePin);
    }

    startActivity(new Intent(ParametreDefautActivity.this, MainActivity.class));

    }

    /**
     * Permet de savoir si l'utilisateur a changé son code pin dans les paramètres
     * @return (true) Si l'utilisateur a change le code pin
     */
    private boolean codePinHasChanged() {
        final String codePin = prefs.getString("codePin", "");
        Boolean hasChanged = false;

        if (edit_code_pin.getText().length() > 0 && !edit_code_pin.getText().toString().equals(codePin)){
            hasChanged = true;
        }

        return hasChanged;
    }


    /**
     * Permet de récupérer les données du picker de la map et de mofifier les SharedPreferences
     * @param data Les données retournées
     */
    private void locationPicked(Intent data) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Place place = PlacePicker.getPlace(this, data);
        mMap.clear();
        String addr = (String) place.getAddress();
        LatLng actualLatLng = place.getLatLng();
        mMap.addMarker(new MarkerOptions()
                .position(actualLatLng)
                .title("Actual Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLatLng, zoomLevel));

        Adresse adresse = new Adresse(addr, actualLatLng);

        updateAdresseSharedPreferences(adresse);
    }

    /**
     * Est appelé lorsqu'un contact a été choisi par l'utilisateur. Ce contact vient ensuite remplacer le contact dans les SharedPreferences
     * @param data
     */
    private void contactPicked(Intent data) {

        Uri uri = data.getData();
        if (uri != null) {
            Cursor cursor;
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                Contact contact = new Contact(cursor.getString(nameIndex), cursor.getString(phoneIndex));
                updateContactSharedPreferences(contact);
                cursor.close();
            }
        }
    }

    /**
     * Met à jour le contact dans les SharedPreferences
     * @param contact
     */
    private void updateContactSharedPreferences(Contact contact) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nomContact", contact.getNom());
        editor.putString("numeroContact", contact.getNumero());

        text_adresse.setText(contact.getNom() + " " + contact.getNumero());

        editor.apply();
    }

    /**
     * Met à jour l'adresse et le defaultLatLng dans les SharedPreferences
     * @param adresse
     */
    private void updateAdresseSharedPreferences(Adresse adresse) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("adresse", adresse.getAdresse());
        editor.putString("defaultLatLng", adresse.getLatLng().toString());

        editor.apply();
    }

    /**
     * Met à jour codePin dans les SharedPreferences
     * @param codePin
     */
    private void updateCodePinSharedPreferences(String codePin) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("codePin", codePin);

        editor.apply();
    }

    /**
     * Initialise la map
     */
    private void initMapLocation() {
        String actualLatLng2 = PreferenceManager.getDefaultSharedPreferences(this).getString("defaultLatLng", "");
        if (!"".equals(actualLatLng2)) {
            mMap.clear();
            String sub = actualLatLng2.substring(10, actualLatLng2.length() - 1);
            String[] latlong = sub.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng location = new LatLng(latitude, longitude);

            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Default Place"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
        }

    }

    /**
     * Lit les SharedPreferences et mets à jours l'interface graphique
     */
    private void initializingDefaultVariables() {
        String nomContact = prefs.getString("nomContact", null);
        String numeroContact = prefs.getString("numeroContact", null);
        String adresse = prefs.getString("adresse", null);
        String codePin = prefs.getString("codePin",null);

        text_contact.setText(nomContact + " " + numeroContact);
        text_adresse.setText("Adresse actuelle par défaut :  " + adresse);
        edit_code_pin.setText(codePin);
    }
}