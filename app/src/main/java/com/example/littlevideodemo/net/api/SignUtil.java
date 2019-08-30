package com.example.littlevideodemo.net.api;

import com.example.littlevideodemo.weight.Constants;
import com.example.littlevideodemo.utils.LogUtil;
import com.example.littlevideodemo.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class SignUtil {

    private static final String TAG = SignUtil.class.getSimpleName();

    /**
     * 接口无参数时的签名
     *
     * @return
     */
    public static Map<String, String> signParams() {
        Map<String, String> params = new HashMap<>();
        params.putAll(getDefaultParams());
        String sign = sign(params);
        LogUtil.i(TAG, "sign:" + sign);
        params.put("sign", sign);
        return params;
    }

    /**
     * 接口带参数时签名
     *
     * @param params
     * @return
     */
    public static Map<String, String> signParams(Map<String, String> params) {
        params.putAll(getDefaultParams());
        String sign = sign(params);
        params.put("sign", sign);
        return params;
    }

    /**
     * 默认参数
     *
     * @return
     */
    public static Map<String, String> getDefaultParams() {
        String timestamp = System.currentTimeMillis() / 1000 + "";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("timestamp", timestamp);
        return paramsMap;
    }

    /**
     * 排序后签名
     *
     * @param params
     * @return
     */
    private static String sign(Map<String, String> params) {
        List<String> paramKeys = new ArrayList<>(params.keySet());
        Collections.sort(paramKeys);
        StringBuilder stringBuilder = new StringBuilder();
        for (String paramKey : paramKeys) {
            stringBuilder.append(paramKey).append(params.get(paramKey));
        }
        LogUtil.i(TAG, "sorted params:" + stringBuilder.toString());
        String sign = StringUtil.getMd5Value(Constants.sign + stringBuilder.toString()).toUpperCase();
        return sign;
    }
}
