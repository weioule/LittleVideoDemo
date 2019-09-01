package com.example.littlevideodemo.presenter;

import android.content.Context;

import com.example.littlevideodemo.MyApplication;
import com.example.littlevideodemo.base.BaseActivity;
import com.example.littlevideodemo.base.BaseModel;
import com.example.littlevideodemo.view.v.BaseView;

/**
 * Author by weioule.
 * Date on 2018/8/15.
 */
public abstract class BasePresenter<V extends BaseView, M extends BaseModel> {

    private V mView;
    private M mModel;
    protected Context mContext;

    public void attachView(V view) {
        mView = view;
        mModel = createModel();
        mContext = MyApplication.context;
    }

    public abstract M createModel();

    public V getMvpView() {
        return mView;
    }

    public M getMvpModel() {
        return mModel;
    }

    public void detachView() {
        this.mView = null;
        this.mModel = null;
    }

    protected void showLoadingDialog() {
        if (null != getMvpView() && getMvpView() instanceof BaseActivity)
            ((BaseActivity) getMvpView()).showLodingDialog();
    }

    protected void hideLoadingDialog() {
        if (null != getMvpView() && getMvpView() instanceof BaseActivity)
            ((BaseActivity) getMvpView()).hideLodingDialog();
    }
}
