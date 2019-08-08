package com.example.smallvideodemo.weight;

import com.example.smallvideodemo.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置常量和全局变量
 */
public class Constants {

    public static final String sign = "fKf#jFfdksNlfjk@fddjkdDs7jhd6%Sf";

    public static final String SERVICE_HOST = "https://api.apiopen.top";

    public static final String SHARE_TAG_USERNAME = "userName";

    /**
     * log
     */
    public static final boolean isDebug = true;

    public static List<VideoBean> videoDatas = new ArrayList<>();

    /**
     * sharepreference key
     */
    public static final String SHARE_TAG_SESSIONID = "sessionid";
    public static final String SP_KEY_APP_LOGIN_TOKEN = "app_login_token";
    public static final String SP_KEY_USER_LOGIN_TOKEN = "user_login_token";
    public static final String SP_KEY_USER_LOGIN_USERID = "user_login_userid";
    public static final String SP_KEY_USER_LOGIN_SHAREID = "user_login_shareId";
    public static final String SP_KEY_USER_UPLOAD_TOKEN = "user_upload_token";
}
