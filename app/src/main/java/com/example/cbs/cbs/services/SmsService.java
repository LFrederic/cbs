package com.example.cbs.cbs.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.cbs.cbs.broadcastreceiver.SmsServiceBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class SmsService extends Service {
    private List<String> phoneNumbers = new ArrayList<>();
    private String strAdresse;
    private int heure;
    private int minutes;
    private int jour;
    private int mois;
    private int annee;
    private IntentFilter mIntentFilter;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private SmsServiceBroadcastReceiver mReceiver;


    @Override
    public void onCreate() {
        Log.e("TAG", "onCreateSMS");
        mReceiver = new SmsServiceBroadcastReceiver() {
        };
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.example.broadcast.GPS_NOTIFICATION");
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroySMS");
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            phoneNumbers = intent.getStringArrayListExtra("phoneNumbers");
            strAdresse = intent.getStringExtra("strAdresse");
            heure = intent.getIntExtra("heure", 0);
            minutes = intent.getIntExtra("minutes", 0);
            jour = intent.getIntExtra("jour", 0);
            mois = intent.getIntExtra("mois", 0);
            annee = intent.getIntExtra("annee", 0);
            mReceiver.setPhoneNumbers(phoneNumbers);
        }
        Intent gpsIntent = new Intent(this, GPSLocalisationService.class);
        gpsIntent.putExtra("adresse", strAdresse);
        gpsIntent.putExtra("minutes", minutes);
        gpsIntent.putExtra("heure", heure);
        gpsIntent.putExtra("jour", jour);
        gpsIntent.putExtra("mois", mois);
        gpsIntent.putExtra("annee", annee);
        startService(gpsIntent);
        return START_STICKY;
    }
}
