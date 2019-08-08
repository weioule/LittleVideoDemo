package com.example.smallvideodemo.presenter;

import com.example.smallvideodemo.MyApplication;
import com.example.smallvideodemo.utils.SharePreferenceUtil;
import com.example.smallvideodemo.utils.StringUtil;
import com.example.smallvideodemo.view.v.MainView;
import com.example.smallvideodemo.base.BaseModel;
import com.example.smallvideodemo.net.api.ResultObserver;
import com.example.smallvideodemo.utils.CacheUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class MainPresenter extends BasePresenter<MainView, BaseModel> {
    @Override
    public BaseModel createModel() {
        return new BaseModel();
    }

    public void getDatas(String tag, int state, LifecycleTransformer transformer) {
        String cacheKey = CacheUtil.getCacheKey(tag);
        getMvpModel().request(transformer, new ResultObserver(cacheKey) {
            @Override
            public void success(String result) {
                getMvpView().setDatas(result, state);
            }

            @Override
            public void error(String msg) {
                getMvpView().onError(msg);
            }
        });
    }

    public void getCacheData(String url) {
        final String cacheKey = CacheUtil.getCacheKey(url);
        if (null != cacheKey) {
            String result = SharePreferenceUtil.getInstance(MyApplication.context).getData(cacheKey);
            if (!StringUtil.isEmpty(result)) {
                getMvpView().setDatas(result, -1);
                getMvpView().refresh();
            } else {
                getMvpView().loadDatas();
            }
        } else {
            getMvpView().loadDatas();
        }
    }
}
