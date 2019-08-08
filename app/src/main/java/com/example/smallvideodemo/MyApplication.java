package com.example.smallvideodemo;

import android.app.Application;
import android.content.Context;
import android.os.Build;


import androidx.annotation.NonNull;

import com.example.smallvideodemo.net.api.ApiUtil;
import com.example.smallvideodemo.utils.LogUtil;
import com.example.smallvideodemo.weight.Constants;
import com.example.smallvideodemo.weight.MyRefreshFooter;
import com.example.smallvideodemo.weight.MyRefreshHeader;
import com.example.smallvideodemo.weight.RefreshHeaderView.TodayNewsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class MyApplication extends Application {
    public static Context context;

    static {
        //static 代码段可以防止内存泄露
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return new TodayNewsHeader(context);
                } else {
                    return new MyRefreshHeader(context);
                }
            }
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public MyRefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new MyRefreshFooter(context);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

        ApiUtil.initApiService(this);

        if (Constants.isDebug) {
            LogUtil.setLogLevel(LogUtil.VERBOSE);
        } else {
            LogUtil.setLogLevel(LogUtil.NOTHING);
        }
    }

}
