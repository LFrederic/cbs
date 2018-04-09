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
    public List<String> phoneNumbers = new ArrayList<>();
    private IntentFilter mIntentFilter;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private SmsServiceBroadcastReceiver mReceiver;


    @Override
    public void onCreate() {
        Log.e("TAG", "onCreateSMS");
        mReceiver = new SmsServiceBroadcastReceiver(phoneNumbers) {
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
            mReceiver.setPhoneNumbers(phoneNumbers);
        }
        startService(new Intent(this, GPSLocalisationService.class));
        return START_STICKY;
    }
}
