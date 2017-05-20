package com.cdx.onestepsos.Voice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by CDX on 2017/5/20.
 */

public class SpeechService extends Service{
    public static final String LOCATED_SUCCEED = "LOCATE_SUCCEED";
    public static final String SMS_COMPLETED = "SMS_COMPLETED";
    public static final String PHOTO_TAKE_COMPLETED = "PHOTO_TAKE_COMPLETED";
    public static final String STOP_SPEECH = "STOP_SPEECH";
    private Speech speech;
    private Thread thread;
    private boolean interrupt = false;
    private String other = null;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                case LOCATED_SUCCEED:
                    other = "定位成功";
                    interrupt = true;
                    break;
                case SMS_COMPLETED:
                    other = "发送短信完成";
                    interrupt = true;
                    break;
                case PHOTO_TAKE_COMPLETED:
                    other = "拍照完成";
                    interrupt = true;
                    break;
                case STOP_SPEECH:
                    stopSelf();
                    break;
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        speech = new Speech(this);
        thread = new MyThread();
        thread.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LOCATED_SUCCEED);
        intentFilter.addAction(SMS_COMPLETED);
        intentFilter.addAction(PHOTO_TAKE_COMPLETED);
        intentFilter.addAction(STOP_SPEECH);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public class MyThread extends Thread{
        @Override
        public void run() {
            while(true){
                if(!interrupt) {
                    speech.TextToSpeech("请帮助我!!!");
                }else{
                    speech.TextToSpeech(other);
                    interrupt = false;
                }
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
