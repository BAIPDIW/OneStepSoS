package com.cdx.onestepsos.MessageAndDial;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by CDX on 2017/5/13.
 */

public class DialContentObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    Context context;
    Handler handler;
    Long startTime;
    public DialContentObserver(Context context,Handler handler,Long startTime) {
        super(handler);
        this.context = context;
        this.handler = handler;
        this.startTime = startTime;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        DialAccept dialAccept = new DialAccept(context,startTime);
        Message msg = handler.obtainMessage();
        if(dialAccept.isAccept("18850043013")){
            Log.i("CDX","已接通");
            msg.what = 1;
        }else{
            Log.i("CDX","未接通");
            msg.what = 0;
        }
        msg.sendToTarget();
    }
}
