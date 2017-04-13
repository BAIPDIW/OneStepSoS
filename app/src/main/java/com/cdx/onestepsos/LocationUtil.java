package com.cdx.onestepsos;
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

    public final static int MSG_LOCATION_START = 0;  //开始定位

    public final static int MSG_LOCATION_FINISH = 1; //定位完成

    public final static int MSG_LOCATION_STOP= 2;    //停止定位

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
            //sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            //sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            //sb.append("提供者    : " + location.getProvider() + "\n");

            //sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            //sb.append("角    度    : " + location.getBearing() + "\n");
            // 获取当前提供定位服务的卫星个数
            //sb.append("星    数    : " + location.getSatellites() + "\n");
            //sb.append("国    家    : " + location.getCountry() + "\n");
            //sb.append("省            : " + location.getProvince() + "\n");
            //sb.append("市            : " + location.getCity() + "\n");
            //sb.append("城市编码 : " + location.getCityCode() + "\n");
            //sb.append("区            : " + location.getDistrict() + "\n");
            //sb.append("区域 码   : " + location.getAdCode() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            sb.append("兴趣点    : " + location.getPoiName() + "\n");
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
        sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
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
