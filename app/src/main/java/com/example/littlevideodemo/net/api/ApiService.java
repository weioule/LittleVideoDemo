package com.example.littlevideodemo.net.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public interface ApiService {

    /**
     * 请求数据
     * 使用baseUrl模式
     */
    @GET("/todayVideo")
    Observable<String> requestData();

    /**
     * 请求数据
     *
     * @param url    后台下发的全路径url
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<String> requestData(@Url String url, @FieldMap Map<String, String> params);

}
