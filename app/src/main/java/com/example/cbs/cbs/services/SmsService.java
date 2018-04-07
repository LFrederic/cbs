package com.example.cbs.cbs.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SmsService extends Service {
    private List<String> phoneNumbers = new ArrayList<>();
    private IntentFilter mIntentFilter;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.broadcast.GPS_NOTIFICATION")) {
                boolean isArrived = intent.getBooleanExtra("isArrived",false);
                if(isArrived){
                    //envoi sms
                    for(int i =0; i<phoneNumbers.size() ;i++){
                        if (phoneNumbers.get(i).startsWith("+33")) {
                            phoneNumbers.set(i, phoneNumbers.get(i).replace("+33", "0"));
                        }
                        SmsManager sms = SmsManager.getDefault();
                        phoneNumbers.set(i, phoneNumbers.get(i).replaceAll(" ", ""));
                        sms.sendTextMessage((((SmsService)context).phoneNumbers.get(i)), null, "correspondant bien arrivé", null, null);
                        Log.i("INFO", "SMS Envoyé à : " + phoneNumbers.get(i));
                    }
                    Intent stopIntent = new Intent( SmsService.this, GPSLocalisationService.class);
                    stopService(stopIntent);
                    stopSelf();
                }
            }

        }
    };

    @Override
    public void onCreate() {
        Log.e("TAG", "onCreateSMS");
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
        }

        //TODO Pas certain que l'appel au service doit se faire à ce moment-la, et pas dans le SmsService
        startService(new Intent(this, GPSLocalisationService.class));
        return START_STICKY;
    }

}
