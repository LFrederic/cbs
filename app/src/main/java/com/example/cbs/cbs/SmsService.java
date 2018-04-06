package com.example.cbs.cbs;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
             phoneNumbers = intent.getStringArrayListExtra("phoneNumbers");
        }
        startService(new Intent(this, GPSLocalisationService.class));
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.example.broadcast.GPS_NOTIFICATION");
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(SmsService.this, "Arrêt du service SMSService", Toast.LENGTH_SHORT).show();
        unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.broadcast.GPS_NOTIFICATION")) {
                boolean isArrived = intent.getBooleanExtra("isArrived",false);
                if(isArrived){
                    //envoi sms
                    for(int i =0; i<phoneNumbers.size() ;i++){
                        //TODO Formater le numero +33 par 06, autrement on arrive à pas à envoyer un SMS
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage((((SmsService)context).phoneNumbers.get(i)), null, "correspondant bien arrivé", null, null);
                    }
                    Intent stopIntent = new Intent( SmsService.this, GPSLocalisationService.class);
                    stopService(stopIntent);
                    stopSelf();
                }
            }

        }
    };

}
