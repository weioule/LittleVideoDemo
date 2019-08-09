package com.example.smallvideodemo.view.v;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public interface MainView extends BaseView {

    void loadDatas();

    void refresh();

    void setDatas(String result, int state);

    void onError(String msg, int state);
}
