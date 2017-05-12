package com.cdx.onestepsos.MessageAndDial;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;


/**
 * Created by CDX on 2017/5/2.
 */

public class Dial {
    Context context;
    public Dial(Context context){
        this.context = context;
    }
    public void call(String mobile){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);
    }
}
