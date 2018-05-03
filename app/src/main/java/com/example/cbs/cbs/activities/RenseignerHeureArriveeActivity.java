package com.example.cbs.cbs.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cbs.cbs.R;
import com.example.cbs.cbs.services.SmsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RenseignerHeureArriveeActivity extends AppCompatActivity {

    //View
    private EditText edit_date_picker;
    private EditText edit_time_picker;
    private String strAdresse;

    //Variables
    private int heure;
    private int minutes;
    private int jour;
    private int mois;
    private int annee;
    private ArrayList<String> phoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choisir_heure_arrivee);

        //Permet de récupérer l'adresse qui est passée par l'activité précédente
        Intent intent = getIntent();
        strAdresse = intent.getStringExtra("adresse");
        phoneNumbers = intent.getStringArrayListExtra("phoneNumbers");

        initialiserView();
    }

    /**
     * Ouvre un DatePickerDialog permettant à l'utilisateur de choisir une date et procéde à des vérifications. L'utilisateur ne peut pas sélectionner une date passée.
     * @param view EditText edit_date_picker
     */
    public void choisirDate(View view)
    {
        // On initialise le calendrier à la date d'aujourd'hui
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // On ouvre le calendrier
        final DatePickerDialog datePickerDialog = new DatePickerDialog(RenseignerHeureArriveeActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // On ajoute la date à l'edittext
                        edit_date_picker.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);
                        jour = dayOfMonth;
                        mois = monthOfYear;
                        annee = year;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    /**
     * Ouvre un TimePickerDialog permettant à l'utilisateur de choisir une heure et procéde à des vérifications. L'utilisateur ne peut pas sélectionner une heure passée.
     * @param view EditText edit_time_picker
     */
    public void choisirHeure(View view)
    {
        //Verifications des conditions
        if (edit_date_picker.getText().toString().isEmpty())
        {
            Toast.makeText(RenseignerHeureArriveeActivity.this, "Veuillez d'abord choisir une date pour le rendez-vous.", Toast.LENGTH_LONG).show();
            return;
        }


        //Ouverture du timePicker + récupération des données
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        final TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(RenseignerHeureArriveeActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String dtStart = edit_date_picker.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                try
                {
                    Date date = format.parse(dtStart);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int selectedDay = cal.get(Calendar.DAY_OF_MONTH);

                    if (isPastDate(selectedDay, selectedHour, selectedMinute))
                    {
                        edit_time_picker.setText("");
                        return;
                    }



                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                    Toast.makeText(RenseignerHeureArriveeActivity.this, "Erreur dans la date.", Toast.LENGTH_LONG).show();
                    return;
                }


                    edit_time_picker.setText( selectedHour + ":" + selectedMinute);
                    heure = selectedHour;
                    minutes = selectedMinute;
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Choisir l'heure");
        mTimePicker.show();
    }


    /**
     * Vérifie si la date précisé en paramètre est inférieure ou non à la date actuelle
     * @param selectedDay       Un jour de mois             (1..31)
     * @param selectedHour      Une heure de la journée     (0..24)
     * @param selectedMinute    Une minute de la journée    (0..60)
     * @return (true) Si la date précisé en paramètres est inférieure à la date actuelle
     */
    private boolean isPastDate(int selectedDay, int selectedHour, int selectedMinute)
    {
        boolean isPastDate = false;

        int jourActuel = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int heureActuelle = Calendar.getInstance().get(Calendar.HOUR);
        int minutesActuelles = Calendar.getInstance().get(Calendar.MINUTE);

        System.out.println("selectedday : " + selectedDay + " - jour actuel : " + jourActuel);
        System.out.println("heure actuelle : " + heureActuelle + " - minute acutelle : " + minutesActuelles);
        System.out.println("heure selected : " + selectedHour + " - minute selected : " + selectedMinute);

        if (selectedDay == jourActuel)
        {
            System.out.println("selectedDay == jourActuel");
            if (heureActuelle == selectedHour && selectedMinute <  minutesActuelles || selectedHour < heureActuelle)
            {
                System.out.println("heureActuelle == selectedHour && minutesActuelles < selectedMinute | heureActuelle < selectedHour");
                Toast.makeText(RenseignerHeureArriveeActivity.this, "Vous ne pouvez pas choisir une date passée.", Toast.LENGTH_LONG).show();
                isPastDate = true;
            }
        }

        return  isPastDate;
    }

    /**
     * Vérifie qu'une date ainsi qu'une heure soit renseignées sur l'activity, démarre le service SmsService puis renvoie l'utilisateur sur l'acitivity MainActivity
     * @param view Button btnValiderHeureArrivee
     */
    public void validerHeureArrivee(View view)
    {
         if (edit_date_picker.getText().toString().equals("") || edit_time_picker.getText().toString().equals("")) {Toast.makeText(RenseignerHeureArriveeActivity.this, "Veuillez choisir une date pour le rendez-vous", Toast.LENGTH_LONG).show(); return;}

        String dateString = edit_date_picker.getText().toString() + " " + edit_time_picker.getText().toString();
        Log.i("dateString", "RenseignerHeureArriveeActivity -> dateString : " + dateString);


        Intent smsIntent = new Intent(RenseignerHeureArriveeActivity.this, SmsService.class);
        smsIntent.putStringArrayListExtra("phoneNumbers", phoneNumbers);
        smsIntent.putExtra("strAdresse", strAdresse);
        smsIntent.putExtra("minutes", minutes);
        smsIntent.putExtra("heure", heure);
        smsIntent.putExtra("jour", jour);
        smsIntent.putExtra("mois", mois);
        smsIntent.putExtra("annee", annee);

        if(android.os.Build.VERSION.SDK_INT >= 26){
            startForegroundService(smsIntent);
        }
        else{
            startService(smsIntent);
        }

        //TODO Revenir à l'écran d'accueil avec un toast "Rentrez bien !"
        Intent returnToMainActivity = new Intent(this, MainActivity.class);
        Toast.makeText(RenseignerHeureArriveeActivity.this, "Rentrez bien !", Toast.LENGTH_LONG).show();
        startActivity(returnToMainActivity);
    }

    public void initialiserView() {
        edit_date_picker = findViewById(R.id.edtDatePicker);
        edit_time_picker = findViewById(R.id.edtTimePicker);
    }

}




















