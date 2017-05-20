package com.cdx.onestepsos.ConnectServer;

/**
 * Created by CDX on 2017/5/20.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 判断网络状态的工具类
 *
 */
public class NetworkUtil {

    /**
     * 判断当前是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetwork(Context context) {
        boolean network = isWifi(context);
        boolean mobilework = isMobile(context);
        if (!network && !mobilework) { // 无网络连接
            Log.i("CDX", "无网络连接！");
            return false;
        } else if (network == true && mobilework == false) { // wifi连接
            Log.i("CDX", "wifi连接！");
        } else { // 网络连接
            Log.i("CDX", "手机网络连接！");
            return true;
        }
        return true;
    }


    /**
     * 判断当前网络是否是wifi局域网
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null) {
            return info.isConnected(); // 返回网络连接状态
        }
        return false;
    }

    /**
     * 判断当前网络是否是手机网络
     *
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info != null) {
            return info.isConnected(); // 返回网络连接状态
        }
        return false;
    }
}
