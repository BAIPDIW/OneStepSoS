package com.cdx.onestepsos.ConnectServer;

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


/**
 * Created by CDX on 2017/4/26.
 */

public class HttpThread extends Thread{
    public static int UPLOAD_SETTING_INFORMATION = 0;
    public static int UPLOAD_LOCATION = 1;
    public static int UPLOAD_PHOTOS = 2;

    private int uploadType;
    private double longitude;
    private double latitude;
    private String url;
    private String fileName;
    private String content;
    public HttpThread(String url,double longitude,double latitude,int uploadType){
        this.url = url;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uploadType = uploadType;
    }
    public HttpThread(String url,String content){
        this.url = url;
        this.content = content;
    }

    public HttpThread(String url,String fileName,int uploadType){
        this.url = url;
        this.fileName = fileName;
        this.uploadType = uploadType;
    }


    private void doGet(){
        url = url+"?latitude="+"";
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String str;
            StringBuffer sb = new StringBuffer();
            while((str = reader.readLine()) != null){
                sb.append(str);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void doPost(){
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            out.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = reader.readLine())!= null){
                sb.append(str);
            }
            Log.i("CDX",sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("CDX","MalformedURLException e");
        } catch (IOException e) {
            Log.i("CDX","IOException e");
            e.printStackTrace();
        }
    }

    public void upLoadPicture(){
        String boundary  = "---------------------------7de2c25201d48";
        String prefix = "--";
        String end = "\r\n";

        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(prefix+boundary+end);
            out.writeBytes("Content-Disposition:form-data;"+"name=\"file\";file=\""+"Sky.jpg"+"\""+end);
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
            Log.i("CDX",sb.toString());
            if(out != null){
                out.close();
            }
            if(reader != null){
                reader.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
       // upLoadPicture();
        doPost();
    }
}
