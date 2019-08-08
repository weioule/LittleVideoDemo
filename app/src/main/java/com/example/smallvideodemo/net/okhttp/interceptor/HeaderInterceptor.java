package com.example.smallvideodemo.net.okhttp.interceptor;


import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.example.smallvideodemo.weight.Constants;
import com.example.smallvideodemo.net.okhttp.ParamsConstant;
import com.example.smallvideodemo.utils.ViewUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class HeaderInterceptor implements Interceptor {

    private static final String TAG = HeaderInterceptor.class.getSimpleName();
    private Context mContext;

    public HeaderInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        String userAgent = userAgent() + "/kdxj/" + ViewUtil.getAppVersion(mContext);
        Request newRequest = originRequest.newBuilder()
                .headers(Headers.of(ParamsConstant.headers(mContext)))
                .addHeader("User-Agent", userAgent)
                .method(originRequest.method(), originRequest.body())
                .build();
        logHeader(newRequest);


        return chain.proceed(newRequest);
    }


    private void logHeader(Request newRequest) {
        if (Constants.isDebug) {
            Log.i(TAG, "-----------HEADER START----------\n");
            for (String str : newRequest.headers().names()) {
                Log.i(TAG, str + ":" + newRequest.header(str) + "\n");
            }
            Log.i(TAG, "-----------HEADER   END----------\n");
        }
    }

    /**
     * Get User-Agent of System.
     *
     * @return UA.
     */
    public static String userAgent() {
        String webUserAgent = "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 %sSafari/533.1";

        StringBuffer buffer = new StringBuffer();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append("1.0");
        }
        buffer.append("; ");

        Locale locale = Locale.getDefault();
        final String language = locale.getLanguage();
        if (language != null) {
            buffer.append(language.toLowerCase(locale));
            final String country = locale.getCountry();
            if (!TextUtils.isEmpty(country)) {
                buffer.append("-");
                buffer.append(country.toLowerCase(locale));
            }
        } else {
            // default to "en"
            buffer.append("en");
        }
        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            if (Build.MODEL.length() > 0) {
                buffer.append("; ");
                buffer.append(Build.MODEL);
            }
        }
        if (Build.ID.length() > 0) {
            buffer.append(" Build/");
            buffer.append(Build.ID);
        }
        return String.format(webUserAgent, buffer, "Mobile ");
    }
}
