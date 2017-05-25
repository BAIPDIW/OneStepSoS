package com.cdx.onestepsos.Voice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by CDX on 2017/5/20.
 */

public class SpeechService extends Service{

    private Speech speech;
    private Thread thread;
    private boolean interrupt = false;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                case "SPEECH_STOP_SOS":
                    interrupt = true;
                    Log.i("CDX","SpeechService  STOP_SOS");

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

        intentFilter.addAction("SPEECH_STOP_SOS");
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        Log.i("CDX","SpeechService onDestroy");
        thread.interrupt();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public class MyThread extends Thread{
        @Override
        public void run() {
            while(!interrupt){
                Log.i("CDX","thread is running");
                    speech.TextToSpeech("请帮助我!!!");
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
