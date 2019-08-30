package com.example.littlevideodemo.net.okhttp.interceptor;

import android.content.Context;
import android.text.TextUtils;


import com.example.littlevideodemo.utils.SharePreferenceUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class CacheInterceptor implements Interceptor {

    private Context mContext;

    public CacheInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String cacheKey = request.header("cache_key");
        if (!TextUtils.isEmpty(cacheKey)) {
            final BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE);
            final String resp = source.buffer().snapshot().utf8();
            //final String resp=response.peekBody(Long.MAX_VALUE).string();
            //使用SharedPreference根据传入的header cache_key来缓存数据（与方法无关）
            SharePreferenceUtil.getInstance(mContext).setData(cacheKey, resp);
        }
        int maxAge = 600;//缓存失效时间，单位为秒
        return response.newBuilder()
                //GET 缓存，POST不支持
                .removeHeader("Pragma")//清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .header("Cache-Control", "max-age=" + maxAge)
                .build();
    }
}