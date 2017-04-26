package com.cdx.onestepsos;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by CDX on 2017/4/26.
 */

public class HttpThread extends Thread{
    String url;
    String fileName;

    public HttpThread(String url){
        this.url = url;
    }

    private void doGet(){
        url = url+"?latitude="+"";
        try {
            URL httpUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) httpUrl.openConnection();
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
            HttpsURLConnection conn = (HttpsURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            OutputStream out = conn.getOutputStream();
            String content = null;
            out.write(content.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = reader.readLine())!= null){
                sb.append(str);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upLoadPicture(){
        String boundary  = "---------------------------7de2c25201d48";
        String prefix = "--";
        String end = "\r\n";

        try {
            URL httpUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(prefix+boundary+end);
            out.writeBytes("Content-Disposition:form-data;"+"name=\"file\";filename=\""+"Sky.jpg"+"\""+end);
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
        super.run();
    }
}
