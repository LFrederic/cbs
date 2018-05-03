package com.example.cbs.cbs.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.cbs.cbs.R;
import com.example.cbs.cbs.broadcastreceiver.SmsServiceBroadcastReceiver;

import java.util.ArrayList;

public class SmsService extends Service {
    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private String strAdresse;
    private int heure;
    private int minutes;
    private int jour;
    private int mois;
    private int annee;
    private IntentFilter mIntentFilter;
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private SmsServiceBroadcastReceiver mReceiver = new SmsServiceBroadcastReceiver();


    @Override
    public void onCreate() {
        Log.e("TAG", "onCreateSMS");

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, "CBS")
                .setContentTitle("CBS")
                .setContentText("trajet en cours...")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification);
        Notification notification = b.build();
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            // create android channel
            NotificationChannel androidChannel = new NotificationChannel("CBS",
                    "CBS CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(androidChannel);


            //Notification notification = new Notification(R.drawable.ic_notification, "trajet en cour...", System.currentTimeMillis());
            startForeground(1337, notification);
        }
        else{
            getManager().notify(1337, notification);
        }

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.example.broadcast.GPS_NOTIFICATION");
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroySMS");
        unregisterReceiver(mReceiver);
        getManager().cancel(1337);
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
        }
        Intent gpsIntent = new Intent(this, GPSLocalisationService.class);
        gpsIntent.putExtra("adresse", strAdresse);
        gpsIntent.putExtra("minutes", minutes);
        gpsIntent.putExtra("heure", heure);
        gpsIntent.putExtra("jour", jour);
        gpsIntent.putExtra("mois", mois);
        gpsIntent.putExtra("annee", annee);
        gpsIntent.putStringArrayListExtra("phoneNumbers", phoneNumbers);
        startService(gpsIntent);
        return START_REDELIVER_INTENT;
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
