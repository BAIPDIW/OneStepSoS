package com.cdx.onestepsos.MessageAndDial;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.List;

/**
 * Created by CDX on 2017/4/1.
 */

public class SendMessage {

    private String phoneNumber;
    private String content;
    private SmsManager smsManager = SmsManager.getDefault();
    private Context context;

    public SendMessage(String phoneNumber, String content, Context context) {
        this.phoneNumber = phoneNumber;
        this.content = content;
        this.context = context;
    }

    public void Send() {
        Intent itSend = new Intent("SMS_SEND_SUCCESS");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, itSend, 0);

        if (content.length() > 70) {
            List<String> contents = smsManager.divideMessage(content);
            for (String sms : contents) {
                smsManager.sendTextMessage(phoneNumber, null, sms, pendingIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, pendingIntent, null);
        }
    }
}
