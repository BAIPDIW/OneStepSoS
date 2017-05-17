package com.cdx.onestepsos.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by CDX on 2017/5/17.
 */

public class MyConnectedThread extends Thread{
    public static final UUID PRIVATE_UUID = UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
    private Handler serviceHandler;		//用于同Service通信的Handler
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;		//用于通信的Socket
    private BluetoothServerSocket serverSocket;
    public MyConnectedThread(Handler handler){
        Log.i("CDX","MyConnectedThread");
        this.serviceHandler = handler;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(!adapter.isEnabled()){
            adapter.enable();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server", PRIVATE_UUID);
            socket = serverSocket.accept();
        } catch (Exception e) {
            if(socket != null){
                try {
                    Log.i("CDX","socket.close");
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            serviceHandler.obtainMessage(0).sendToTarget();
            e.printStackTrace();
            //return;
        } finally {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            Log. i("CDX","成功获得socket");
            //发送连接成功消息，消息的obj字段为连接的socket
            Message msg = serviceHandler.obtainMessage();
            msg.what = 1;
            msg.obj = socket;
            msg.sendToTarget();
        } else {
            //发送连接失败消息
            serviceHandler.obtainMessage(0).sendToTarget();
        }

    }
}
