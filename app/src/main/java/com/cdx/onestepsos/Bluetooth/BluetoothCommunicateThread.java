package com.cdx.onestepsos.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by CDX on 2017/5/17.
 */

public class BluetoothCommunicateThread extends Thread {
    private Handler serviceHandler;        //与Service通信的Handler
    private BluetoothSocket socket;
    private ObjectInputStream inStream;        //对象输入流
    public boolean isRun = false;    //运行标志位

    /**
     * 构造函数
     * @param handler 用于接收消息
     * @param socket
     */
    public BluetoothCommunicateThread(Handler handler, BluetoothSocket socket) {
        this.serviceHandler = handler;
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            this.inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            isRun = true;
        } catch (Exception e) {
            try {
                Log.i("CDX","socket.close");
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //发送连接失败消息
            Log.i("CDX","获取instream失败");
            serviceHandler.obtainMessage(0).sendToTarget();
            e.printStackTrace();
        }
        while (true) {
            if (!isRun) {
                break;
            }
            try {
                Log.i("CDX","接收消息.......");
                Object obj = inStream.readObject();
                Message msg = serviceHandler.obtainMessage();
                msg.what = 2;
                msg.obj = obj;
                msg.sendToTarget();
                isRun = false;
            } catch (Exception ex) {
                Log.i("CDX","接收消息失败");
                serviceHandler.obtainMessage(0).sendToTarget();
                ex.printStackTrace();
            }
        }

        if(inStream != null){
            Log.i("CDX","instream  close");
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket != null){
            try {
                Log.i("CDX","socket.close");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
