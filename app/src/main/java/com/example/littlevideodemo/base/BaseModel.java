package com.example.littlevideodemo.base;

import com.example.littlevideodemo.net.api.ApiUtil;
import com.example.littlevideodemo.net.api.SignUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
/**
 * @author weioule
 * @date 2019/8/3.
 */
public class BaseModel {

    /**
     * 请求数据(无参)
     *
     * @param transformer
     * @param observer
     */
    public void request(LifecycleTransformer transformer, Observer observer) {
        ApiUtil.getApiService()
                .requestData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(transformer)
                .subscribe(observer);
    }


    /**
     * 请求数据(有参)
     *
     * @param url
     * @param params
     * @param transformer
     * @param observer
     */
    public void request(String url, Map<String, String> params, LifecycleTransformer transformer, Observer observer) {
        ApiUtil.getApiService()
                .requestData(url, SignUtil.signParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(transformer)
                .subscribe(observer);
    }


    /**
     * 请求数据(无参)
     *
     * @param url
     * @param transformer
     * @param observer
     */
    public void request(String url, LifecycleTransformer transformer, Observer observer) {
        ApiUtil.getApiService()
                .requestData(url, SignUtil.signParams())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(transformer)
                .subscribe(observer);
    }


    /**
     * 返回结果后自己处理
     *
     * @param url
     * @param transformer
     * @return Observable
     */
    public Observable<String> request(String url, LifecycleTransformer transformer) {
        return ApiUtil.getApiService()
                .requestData(url, SignUtil.signParams())
                .subscribeOn(Schedulers.io())
                .compose(transformer);
    }


    /**
     * 返回结果后自己处理
     *
     * @param url
     * @param params
     * @param transformer
     * @return Observable
     */
    public Observable<String> request(String url, Map<String, String> params, LifecycleTransformer transformer) {
        return ApiUtil.getApiService()
                .requestData(url, SignUtil.signParams(params))
                .subscribeOn(Schedulers.io())
                .compose(transformer);
    }
}
