package com.cdx.onestepsos;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * Created by CDX on 2017/3/31.
 */

public class BluetoothCommunThread extends Thread{
    private Handler serviceHandler;        //与Service通信的Handler
    private BluetoothSocket socket;
    private ObjectInputStream inStream;        //对象输入流
    private ObjectOutputStream outStream;    //对象输出流
    public volatile boolean isRun = false;    //运行标志位

    /**
     * 构造函数
     * @param handler 用于接收消息
     * @param socket
     */
    public BluetoothCommunThread(Handler handler, BluetoothSocket socket) {
        this.serviceHandler = handler;
        this.socket = socket;
        Log.i("CDX","BluetoothCommunThread");

        try {
            this.inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            isRun = true;
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //发送连接失败消息
            serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!isRun) {
                break;
            }
            Log.i("CDX","BluetoothCommunThread isRunning");
            try {
                Object obj = inStream.readObject();
                //发送成功读取到对象的消息，消息的obj参数为读取到的对象
                Message msg = serviceHandler.obtainMessage();
                msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
                msg.obj = obj;
                msg.sendToTarget();
                Log.i("CDX","接收消息成功");
            } catch (Exception ex) {
                //发送连接失败消息
                serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
                ex.printStackTrace();
                return;
            }
        }
       /* //关闭流
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
