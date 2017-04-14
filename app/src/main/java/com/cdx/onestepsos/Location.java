package com.cdx.onestepsos;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import static com.amap.api.location.AMapLocationClient.setApiKey;

/**
 * 定位类
 * Created by CDX on 2017/3/27.
 */

public class Location {

    private boolean flag = true;
    private TextView tv_location;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    private Handler handler;

    public void set(TextView tv,Handler handler){
        this.tv_location = tv;
        this.handler = handler;
    }

    //定义定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            tv_location.setText(LocationUtil.getLocationStr(aMapLocation));
            tv_location.setTextSize(10);
            if(flag){
                //一次定位成功,进度切换
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("经度",aMapLocation.getLongitude()+"");
                bundle.putString("纬度",aMapLocation.getLatitude()+"");
                bundle.putString("地点",aMapLocation.getAddress());
                message.setData(bundle);
                message.what = 1;
                message.sendToTarget();

            }
            flag = false;
        }

    };

    /**
     * 初始化AMapLocationClient
     * @param context  上下文,getApplicationContext（）
     *
     */
    public void init(Context context){
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        setApiKey("c8461266662e6925776445963edf6e96");
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setGpsFirst(true);
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
       // mLocationClient.startLocation();
    }
    /**
     * 开始定位
     */
    public void startLocation(){
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public  void stopLocation(){
        mLocationClient.stopLocation();
    }

}
