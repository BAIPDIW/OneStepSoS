package com.cdx.onestepsos.MessageAndDial;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

/**
 * Created by CDX on 2017/5/12.
 */

public class DialAccept {
    private Context context;

    private Long startTime;

    public DialAccept(Context context, Long startTime) {
        this.context = context;
        this.startTime = startTime;
    }

    public boolean isAccept(String mobile) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, //系统方式获取通讯录存储地址
                new String[]{
                        CallLog.Calls.CACHED_NAME,  //姓名
                        CallLog.Calls.NUMBER,    //号码
                        CallLog.Calls.TYPE,  //呼入/呼出(2)/未接
                        CallLog.Calls.DATE,  //拨打时间
                        CallLog.Calls.DURATION   //通话时长
                }, CallLog.Calls.NUMBER + "= ?", new String[]{mobile}, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (c != null) {
            while (c.moveToNext()) {
                int callType = Integer.parseInt(c.getString(2));
                if (callType == CallLog.Calls.OUTGOING_TYPE) {
                    //拨打时间
                    Long time = Long.parseLong(c.getString(3));
                    if (time > startTime) {
                        //通话时长
                        int callDuration = Integer.parseInt(c.getString(4));
                        if (callDuration > 0)
                            return true;
                    }
                }
            }
        }
        c.close();
        return false;
    }
}
