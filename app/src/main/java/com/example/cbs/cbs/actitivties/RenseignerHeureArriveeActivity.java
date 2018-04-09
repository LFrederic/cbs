package com.example.cbs.cbs.actitivties;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cbs.cbs.R;

import java.util.Calendar;

public class RenseignerHeureArriveeActivity extends AppCompatActivity {


    EditText edtDatePicker;
    EditText edtTimePicker;
    String strAdresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choisir_heure_arrivee);

        //Permet de récupérer l'adresse qui est passée par l'activité précédente : ChoisirAdresseRDVActivity
        Intent intent = getIntent();
        strAdresse = intent.getStringExtra("adresse");
    }

    public void choisirDate(View view)
    {
        edtDatePicker = (EditText) findViewById(R.id.edtDatePicker);
        // On initialise le calendrier à la date d'aujourd'hui
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // On ouvre le calendrier
        DatePickerDialog datePickerDialog = new DatePickerDialog(RenseignerHeureArriveeActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // On ajoute la date à l'edittext
                        edtDatePicker.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void choisirHeure(View view)
    {
        edtTimePicker = (EditText) findViewById(R.id.edtTimePicker);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(RenseignerHeureArriveeActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtTimePicker.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Choisir l'heure");
        mTimePicker.show();
    }

    public void validerHeureArrivee(View view)
    {
        EditText edtDatePicker = (EditText) findViewById(R.id.edtDatePicker);
        EditText edtTimePicker = (EditText) findViewById(R.id.edtTimePicker);

         if (edtDatePicker.getText().toString().equals("") || edtTimePicker.getText().toString().equals("")) {Toast.makeText(RenseignerHeureArriveeActivity.this, "Veuillez choisir une date pour le rendez-vous", Toast.LENGTH_LONG).show(); return;}

        String dateString = edtDatePicker.getText().toString() + " " + edtTimePicker.getText().toString();
        Log.i("dateString", "ChoisirHeureArrveeActivity -> dateString : " + dateString);

    }

}
