package com.example.littlevideodemo.net.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.example.littlevideodemo.weight.Constants;
import com.example.littlevideodemo.utils.GsonUtil;
import com.example.littlevideodemo.utils.NetWorkUtil;
import com.example.littlevideodemo.utils.SharePreferenceUtil;
import com.example.littlevideodemo.utils.ViewUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class ParamsConstant {

    public static boolean TEXT_DEVICE_ID_ISOPEN = false; //设备号的测试开关
    private static String sChannelName = "official";//渠道号

    public static String getChannelName() {
        return sChannelName;
    }

    public static void setChannelName(String channelName) {
        sChannelName = channelName;
    }

    public static Map<String, String> headers(Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", getCookie(context));

        HeaderBean headerBean = new HeaderBean();

        try {
            headerBean.setDevice(ViewUtil.getDeviceName());
            if (Constants.isDebug && ParamsConstant.TEXT_DEVICE_ID_ISOPEN) {
                int one = (int) (Math.random() * 100);
                int two = (int) (Math.random() * 100);
                int three = (int) (Math.random() * 100);
                int four = (int) (Math.random() * 100);
                headerBean.setDeviceId(one + two + three + four + "");
            } else {
                headerBean.setDeviceId(ViewUtil.getDeviceId(context));
            }
            headerBean.setMarket(getChannelName());
            headerBean.setName("name1");

            headerBean.setOs(android.os.Build.MODEL);
            headerBean.setVer(ViewUtil.getAppVersion(context));

            headerBean.setOs_ver(ViewUtil.getOsVersion());
            headerBean.setOs_type("Android");
            headerBean.setNet_type(NetWorkUtil.getNetworkState(context));
        } catch (Exception e) {
            headerBean.setDevice("");
            headerBean.setMarket("");
            headerBean.setDeviceId(ViewUtil.getDeviceId(context));
            headerBean.setName("name2");
            headerBean.setOs_ver("");
            headerBean.setOs("");
            headerBean.setVer(ViewUtil.getAppVersion(context));
            headerBean.setOs_type("Android");
            headerBean.setNet_type(NetWorkUtil.getNetworkState(context));
        }

        headers.put("app_info", GsonUtil.toString(headerBean));
        return headers;
    }


    public static String getCookie(Context context) {
        String sessionId = SharePreferenceUtil.getInstance(context).getData(Constants.SHARE_TAG_SESSIONID);
        sessionId = TextUtils.isEmpty(sessionId) ? "" : sessionId;
        return "SESSIONID=" + sessionId + ";";
    }
}