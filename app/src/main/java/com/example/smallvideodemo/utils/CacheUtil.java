package com.example.smallvideodemo.utils;


import com.example.smallvideodemo.MyApplication;
import com.example.smallvideodemo.weight.Constants;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class CacheUtil {

    public static final String getCacheKey(String url) {
        if (StringUtil.isEmpty(url)) return null;
        String userName = SharePreferenceUtil.getInstance(MyApplication.context).getData(Constants.SHARE_TAG_USERNAME);
        return userName + url + ViewUtil.getAppVersion(MyApplication.context);
    }


    public static final String getCacheKey(String url, String tagKey) {
        if (StringUtil.isEmpty(url) || StringUtil.isEmpty(tagKey)) return null;
        String userName = SharePreferenceUtil.getInstance(MyApplication.context).getData(Constants.SHARE_TAG_USERNAME);
        return userName + url + tagKey + ViewUtil.getAppVersion(MyApplication.context);
    }
}
