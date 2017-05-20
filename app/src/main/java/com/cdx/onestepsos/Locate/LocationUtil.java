package com.cdx.onestepsos.Locate;
import android.text.TextUtils;
import com.amap.api.location.AMapLocation;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 定位功能辅助工具类
 * Created by CDX on 2017/3/27.
 *
 */

public class LocationUtil {
    /**
     *根据定位结果返回定位信息的字符串
     * @param location
     * @return 结果字符串
     */
    public synchronized static String getLocationStr(AMapLocation location){

        if(location == null){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        if(location.getErrorCode() == 0){
            sb.append("定位成功" + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            // sb.append("兴趣点    : " + location.getPoiName() + "\n");
            //定位完成的时间
            sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
        }else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        //定位之后的回调时间
        //sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
        return sb.toString();
    }

    private static SimpleDateFormat sdf = null;

    public synchronized static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }
}
