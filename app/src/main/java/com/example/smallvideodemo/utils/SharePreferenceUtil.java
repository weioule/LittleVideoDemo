package com.example.smallvideodemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class SharePreferenceUtil {

    private static SharePreferenceUtil util;
    private static SharedPreferences share;

    public SharePreferenceUtil(Context context) {
        // TODO Auto-generated constructor stub
        share = context.getSharedPreferences("loan_Share", Context.MODE_PRIVATE);
    }

    public static SharePreferenceUtil getInstance(Context context) {
        if (util == null) {
            util = new SharePreferenceUtil(context);
        }
        return util;
    }

    public void setData(String key, String data) {
        if (StringUtil.isEmpty(key)) {
            throw new NullPointerException("SharePreferenceUtil.setData has a null key");
        }
        Editor editor = share.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public String getData(String key) {
        return share.getString(key, "");
    }

    public void setIntData(String key, int data) {
        if (StringUtil.isEmpty(key)) {
            throw new NullPointerException("SharePreferenceUtil.setIntData has a null key");
        }
        Editor editor = share.edit();
        editor.putInt(key, data);
        editor.commit();
    }

    public void setData(String key, String data, CallBackListener callBackListener) {
        if (StringUtil.isEmpty(key)) {
            throw new NullPointerException("SharePreferenceUtil.setData has a null key");
        }
        Editor editor = share.edit();
        editor.putString(key, data);
        if (callBackListener != null) {
            callBackListener.result(editor.commit());
        }
    }

    public interface CallBackListener {
        void result(boolean result);
    }

    public int getIntData(String key, int defValue) {
        return share.getInt(key, defValue);
    }

    public void setBoolData(String key, boolean data) {
        if (StringUtil.isEmpty(key)) {
            throw new NullPointerException("SharePreferenceUtil.setBoolData has a null key");
        }
        Editor editor = share.edit();
        editor.putBoolean(key, data);
        editor.commit();
    }

    public boolean getBoolData(String key, boolean defValue) {
        return share.getBoolean(key, defValue);
    }

    public void setLongData(String key, long data) {
        if (StringUtil.isEmpty(key)) {
            throw new NullPointerException("SharePreferenceUtil.setLongData has a null key");
        }
        Editor editor = share.edit();
        editor.putLong(key, data);
        editor.commit();
    }

    public long getlongData(String key, long defValue) {
        return share.getLong(key, defValue);
    }
}
