package com.example.smallvideodemo.net.api;

import android.content.Context;
import android.util.Log;


import com.example.smallvideodemo.net.okhttp.RetrofitCreator;
import com.example.smallvideodemo.weight.Constants;

import retrofit2.Retrofit;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class ApiUtil {
    private static String TAG = ApiUtil.class.getSimpleName();
    private static ApiService sApiService;
    private static Retrofit sRetrofit;

    private ApiUtil() {
    }

    public static void initApiService(Context context) {
        if (sRetrofit == null) {
            synchronized (ApiUtil.class) {
                if (sRetrofit == null) {
                    sRetrofit = new RetrofitCreator().initRetrofit(context, Constants.SERVICE_HOST, Constants.isDebug);
                }
            }
        }
    }

    public static ApiService getApiService() {
        if (sRetrofit == null) {
            Log.e(TAG, "Retrofit has not bean init");
            throw new NullPointerException("Retrofit is null");
        }
        if (sApiService == null) {
            sApiService = sRetrofit.create(ApiService.class);
        }
        return sApiService;
    }
}
