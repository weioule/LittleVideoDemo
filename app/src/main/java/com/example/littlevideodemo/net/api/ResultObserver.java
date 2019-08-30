package com.example.littlevideodemo.net.api;

import android.text.TextUtils;

import com.example.littlevideodemo.MyApplication;
import com.example.littlevideodemo.utils.LogUtil;
import com.example.littlevideodemo.utils.NetWorkUtil;
import com.example.littlevideodemo.utils.SharePreferenceUtil;
import com.example.littlevideodemo.utils.StringUtil;
import com.example.littlevideodemo.weight.SingletonToast;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * @author weioule
 * @date 2019/8/3.
 */
public abstract class ResultObserver implements Observer<String> {

    private static final String TAG = ResultObserver.class.getSimpleName();
    private Disposable mDisposable;
    private String mCacheKey;

    public ResultObserver() {
    }

    public ResultObserver(String cacheKey) {
        mCacheKey = cacheKey;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if (!NetWorkUtil.hasNetWorkConnection(MyApplication.context)) {
            onError(new ApiError(ApiError.HTTP_ERROR_NETWORK, ApiError.HTTP_ERROR_NETWORK_MSG));
        }
    }

    @Override
    public void onNext(String t) {
        if (StringUtil.isEmpty(t)) {
            onError(new ApiError(ApiError.HTTP_ERROR_SERVER, ApiError.HTTP_ERROR_SERVER_MSG));
            return;
        }
        try {
            JSONObject json = new JSONObject(t);
            int code = json.getInt("code");
            if (code == ApiError.HTTP_SUCCESS_CODE || code == ApiError.HTTP_ERROR_REQUEST_OTHER) {
                String data = json.get("result").toString();
                if (StringUtil.isEmpty(data) || data.equals("[]"))
                    successNoData();
                else {
                    success(data);
                    if (!StringUtil.isEmpty(mCacheKey)) {
                        //使用SharedPreference根据传入的cache_key来缓存数据
                        SharePreferenceUtil.getInstance(MyApplication.context).setData(mCacheKey, data);
                    }
                }
            } else {
                ApiError apiError = new ApiError(json.getInt("code"), json.getString("message"));
                onError(apiError);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @Override
    public void onComplete() {
        dispose();
    }


    @Override
    public void onError(Throwable e) {

        String errorMsg;
        if (e instanceof IOException) {
            // 网络异常
            errorMsg = ApiError.HTTP_ERROR_NETWORK_MSG;
            SingletonToast.getInstance().show(errorMsg);
        } else if (e instanceof HttpException) {
            // 网络异常，http 请求失败，即 http 状态码不在 [200, 300) 之间, such as: "server internal error".
            errorMsg = ((HttpException) e).response().message();
        } else if (e instanceof ApiError) {
            // 网络正常，http 请求成功，服务器返回逻辑错误
            errorMsg = ((ApiError) e).getMsg();
            switch (((ApiError) e).code) {
                case ApiError.HTTP_ERROR_NEED_LOGIN:
                    // 清除登录状态 ---> to login
                    toLogin();
                    break;
                case ApiError.HTTP_SUCCESS_CODE_FAILED:
                    if (!StringUtil.isEmpty(errorMsg))
                        SingletonToast.getInstance().show(errorMsg);
                    break;
                case ApiError.HTTP_ERROR_NETWORK:
                    if (!StringUtil.isEmpty(errorMsg))
                        SingletonToast.getInstance().show(errorMsg);
                    break;
            }
        } else if (e instanceof JSONException) {
            errorMsg = e.getMessage();
        } else if (e instanceof JsonParseException) {
            errorMsg = e.getMessage();
        } else {
            /** 其他未知错误 */
            errorMsg = !TextUtils.isEmpty(e.getMessage()) ? e.getMessage() : "unknown error";
        }
        LogUtil.i(TAG, errorMsg);
        error(errorMsg);
        dispose();
    }

    private void dispose() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }


    /**
     * 请求成功
     *
     * @param result data数据
     */
    public abstract void success(String result);

    /**
     * 请求成功
     * data 无数据
     */
    public void successNoData() {
    }

    /**
     * 请求未成功
     *
     * @param msg
     */
    public void error(String msg) {
    }

    /**
     * 请求成功
     * data 需要登陆
     */
    public void toLogin() {
    }

}