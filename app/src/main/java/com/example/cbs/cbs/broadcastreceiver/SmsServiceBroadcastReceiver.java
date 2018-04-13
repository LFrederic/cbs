package com.example.cbs.cbs.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.cbs.cbs.services.GPSLocalisationService;
import com.example.cbs.cbs.services.SmsService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsServiceBroadcastReceiver extends BroadcastReceiver {
    private List<String> phoneNumbers;

    public SmsServiceBroadcastReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.broadcast.GPS_NOTIFICATION")) {
            phoneNumbers = intent.getStringArrayListExtra("phoneNumbers");
            boolean isArrived = intent.getBooleanExtra("isArrived", false);
            if (isArrived) {
                //envoi sms
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    if (phoneNumbers.get(i).startsWith("+33")) {
                        phoneNumbers.set(i, phoneNumbers.get(i).replace("+33", "0"));
                    }
                    SmsManager sms = SmsManager.getDefault();
                    phoneNumbers.set(i, phoneNumbers.get(i).replaceAll(" ", ""));
                    sms.sendTextMessage((phoneNumbers.get(i)), null, "Le correspondant bien arrivé au point de rendez-vous", null, null);
                    Log.i("INFO", "SMS Envoyé à : " + phoneNumbers.get(i));
                }
            }
            else{
                //envoi sms
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    if (phoneNumbers.get(i).startsWith("+33")) {
                        phoneNumbers.set(i, phoneNumbers.get(i).replace("+33", "0"));
                    }
                    SmsManager sms = SmsManager.getDefault();
                    phoneNumbers.set(i, phoneNumbers.get(i).replaceAll(" ", ""));
                    sms.sendTextMessage((phoneNumbers.get(i)), null, "Le correspondant n'est pas encore arrivé au point de rendez-vous", null, null);
                    Log.i("INFO", "SMS Envoyé à : " + phoneNumbers.get(i));
                }
            }
        }

    }
}
