package com.cdx.onestepsos;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Created by CDX on 2017/3/28.
 */

public class BluetoothServerService extends Service{

    //蓝牙适配器
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //蓝牙通讯线程
    private BluetoothCommunThread communThread;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i("CDX","BluetoothServerService is on create");
        bluetoothAdapter.enable();	//打开蓝牙
        //开启后台连接线程
        new BluetoothServerConnThread(serviceHandler).start();
        super.onCreate();
    }

    //接收其他线程消息的Handler
    private Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
                    //连接成功
                    //开启通讯线程
                    Log.i("CDX","连接成功开启通信线程");
                    communThread = new BluetoothCommunThread(serviceHandler, (BluetoothSocket)msg.obj);
                    communThread.start();
                    sendBroadcast(new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS));
                    break;
                case BluetoothTools.MESSAGE_CONNECT_ERROR:
                    //连接错误
                    Log.i("CDX","socket连接出错");
                    break;

                case BluetoothTools.MESSAGE_READ_OBJECT:
                    //读取到数据
                    //发送数据广播（包含数据对象）
                    /*Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
                    dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
                    sendBroadcast(dataIntent);*/
                    Intent intent = new Intent(getBaseContext(),ProgressActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
