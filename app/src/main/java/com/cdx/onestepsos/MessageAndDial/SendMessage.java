package com.cdx.onestepsos.MessageAndDial;
import android.telephony.SmsManager;
import java.util.List;

/**
 * Created by CDX on 2017/4/1.
 */

public class SendMessage {

    private String phoneNumber;
    private String content;
    private SmsManager smsManager = SmsManager.getDefault();
    public SendMessage(String phoneNumber,String content){
           this.phoneNumber = phoneNumber;
           this.content = content;
    }

    public void Send(){
            if(content.length()>70){
                List<String> contents = smsManager.divideMessage(content);
                for(String sms:contents){
                    smsManager.sendTextMessage(phoneNumber,null,sms,null,null);
                }
            }else{
                smsManager.sendTextMessage(phoneNumber,null,content,null,null);
            }
    }
}
