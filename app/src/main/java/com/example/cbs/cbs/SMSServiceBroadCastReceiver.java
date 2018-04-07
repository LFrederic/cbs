package com.example.cbs.cbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SMSServiceBroadCastReceiver extends BroadcastReceiver {
    private List<String> phoneNumbers;

    public SMSServiceBroadCastReceiver() {
        phoneNumbers = new ArrayList<>();
    }

    public SMSServiceBroadCastReceiver(List<String> phones) {
        phoneNumbers = phones;

    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.broadcast.GPS_NOTIFICATION")) {
            boolean isArrived = intent.getBooleanExtra("isArrived", false);
            if (isArrived) {
                //envoi sms
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    if (phoneNumbers.get(i).startsWith("+33")) {
                        phoneNumbers.set(i, phoneNumbers.get(i).replace("+33", "0"));
                    }
                    SmsManager sms = SmsManager.getDefault();
                    phoneNumbers.set(i, phoneNumbers.get(i).replaceAll(" ", ""));
                    sms.sendTextMessage((((SmsService) context).phoneNumbers.get(i)), null, "correspondant bien arrivé", null, null);
                    Log.i("INFO", "SMS Envoyé à : " + phoneNumbers.get(i));
                }
            }
        }

    }
}
