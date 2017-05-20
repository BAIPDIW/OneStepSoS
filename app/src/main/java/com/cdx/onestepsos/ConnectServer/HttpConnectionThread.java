package com.cdx.onestepsos.ConnectServer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by CDX on 2017/5/3.
 */

public class HttpConnectionThread extends Thread{
    public static final int USER_REGISTER = 0;
    public static final int USER_LOG = 1;
    public static final int USER_REVISE = 2;
    public static final int UPLOAD_LOCATION = 3;
    public static final int UPLOAD_PHOTO = 4;
    public static final int UPLOAD_HELP_CALL = 5;
    public static final int STOP = 6;
    public static final int DELETE_HELP_CALL = 7;

    private String fileName;
    private String content;
    private String ID;
    private int type;
    private Handler handler;
    public HttpConnectionThread(String content,int type){
        this.content = content;
        this.type = type;
    }
    public HttpConnectionThread(String ID,String fileName,Handler handler){
        this.ID = ID;
        this.fileName = fileName;
        this.type = UPLOAD_PHOTO;
        this.handler = handler;
    }
    public HttpConnectionThread(String content,Handler handler,int type){
        this.content = content;
        this.handler = handler;
        this.type = type;
    }

    public void userRegister() {
        Properties pro = System.getProperties();
        pro.list(System.out);
        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/userRegister");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());
            Log.i("CDX",content);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "userregister:  "+ sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "userregister: MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "userregister: IOException e");
            e.printStackTrace();
        }
    }

    public String userLog(){
        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/userLog");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "userlog   "+sb.toString());
           return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "IOException e");
            e.printStackTrace();
        }
        return null;
    }

    public void UserRevise(){
        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/UserRevise");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "userRevise   "+sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "IOException e");
            e.printStackTrace();
        }
    }
    public void uploadLocation(){
        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/uploadLocation");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "uploadlocation   "+sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "IOException e");
            e.printStackTrace();
        }
    }

    public void uploadPhoto(){
        String boundary  = "---------------------------7de2c25201d48";
        String prefix = "--";
        String end = "\r\n";

        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/uploadPhoto");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());

            out.writeBytes(prefix+boundary+end);
            out.writeBytes("Content-Disposition:form-data;"+"name=\"file\";filename=\""+ID+getFileName(fileName)+".jpg"+"\""+end);
            out.writeBytes(end);

            FileInputStream fileInputStream = new FileInputStream(new File(fileName));
            byte[] b = new byte[1024*2];
            int len;
            while((len = fileInputStream.read(b))!=-1){
                out.write(b,0,len);
            }
            out.writeBytes(end);
            out.writeBytes(prefix + boundary + prefix + end);
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while( (str = reader.readLine())!=null){
                sb.append(str);
            }

            Log.i("CDX","uploadphoto    "+sb.toString());
            handler.obtainMessage(2).sendToTarget();
            if(out != null){
                out.close();
            }
            if(reader != null){
                reader.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX","uploadphoto MalformedURLException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("CDX","uploadphoto IOException");
        }
    }

    public void httpstop(){
        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/stop");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "stop   "+ sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "stop   MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "stop   IOException e");
            e.printStackTrace();
        }
    }
    public void deleteHelpCall(){
        try {
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/delete");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "deleteHelpCall   "+ sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "deleteHelpCall MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "deleteHelpCall IOException e");
            e.printStackTrace();
        }
    }

    public void upLoadHelpCall(){
        try {
            Log.i("CDX",content);
            URL httpUrl = new URL("http://119.29.219.15:8080/webHC/upLoadHelpCall");
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            Log.i("CDX", "uploadHelpCall   "+ sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX", "uploadHelpCall MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX", "uploadHelpCall IOException  a");
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        switch(type){
            case USER_REGISTER :
                userRegister();
                break;
            case USER_LOG:
                Message msg = handler.obtainMessage();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("user",userLog());
                msg.setData(bundle);
                msg.sendToTarget();
                break;
            case USER_REVISE:
                UserRevise();
                break;
            case UPLOAD_LOCATION:
                uploadLocation();
                break;
            case UPLOAD_PHOTO:
                uploadPhoto();
                break;
            case UPLOAD_HELP_CALL:
                upLoadHelpCall();
                break;
            case STOP:
                httpstop();
                break;
            case DELETE_HELP_CALL:
                deleteHelpCall();
                break;
        }
    }
    public String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }
    }
}
