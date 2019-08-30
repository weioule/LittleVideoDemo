package com.example.littlevideodemo.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.littlevideodemo.R;
import com.example.littlevideodemo.presenter.BasePresenter;
import com.example.littlevideodemo.utils.LogUtil;
import com.example.littlevideodemo.utils.Utility;
import com.example.littlevideodemo.view.v.BaseView;
import com.example.littlevideodemo.weight.SingletonToast;
import com.example.littlevideodemo.weight.SweetAlertDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.Method;


/**
 * @author weioule
 * @date 2019/8/3.
 */
public abstract class BaseActivity<V extends BaseView, P extends BasePresenter> extends RxAppCompatActivity {

    protected ImageView mErrorImg;
    protected TextView mErrorContent;
    protected RelativeLayout mErrorRl;
    private SweetAlertDialog mSweetAlertDialog;
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(getRootViewId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏

        mPresenter = createPresenter();
        if (null != mPresenter)
            mPresenter.attachView((V) this);

        initView();

        if (Build.VERSION.SDK_INT >= 21)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initData();
    }

    protected abstract P createPresenter();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int getRootViewId();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        super.onDestroy();
    }

    public void showErrorView(TextView titleText) {
        hideLodingDialog();
        if (null != titleText) {
            titleText.setText(getString(R.string.no_network));
            LayoutInflater.from(this).inflate(R.layout.view_net_error, findViewById(R.id.container), true);
        } else {
            View view = LayoutInflater.from(this).inflate(R.layout.view_net_error, null, false);
            setContentView(view);
        }

        RelativeLayout error_title = findViewById(R.id.net_error_title);
        error_title.setVisibility(View.GONE);
        mErrorRl = findViewById(R.id.rl_net_error);
        mErrorImg = findViewById(R.id.iv_neterr);
        mErrorContent = findViewById(R.id.tv_neterr);
        if (Utility.isNetworkAvailable(this)) {
            mErrorContent.setText(getString(R.string.net_error));
        } else {
            mErrorContent.setText(getString(R.string.no_net_error));
        }
        mErrorRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    hideErrorView();
                } else {
                    toast(R.string.no_network);
                }
            }
        });
    }

    protected void hideErrorView() {
        setContentView(getRootViewId());
        initView();
        initData();
    }

    protected final void toast(int resid) {
        SingletonToast.getInstance().show(resid);
    }

    protected final void toast(int resid, int duration) {
        SingletonToast.getInstance().show(resid, duration);
    }

    protected final void toast(String strText) {
        SingletonToast.getInstance().show(strText);
    }

    protected final void toast(String strText, int duration) {
        SingletonToast.getInstance().show(strText, duration);
    }

    protected void forwardAndFinish(Class<?> cls) {
        forward(cls);
        finish();
    }

    protected void forward(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    protected void forward(Intent intent) {
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void showLodingDialog() {
        showLodingDialog(getString(R.string.please_wait));
    }

    public void showLodingDialog(String txt) {
        if (null == mSweetAlertDialog)
            mSweetAlertDialog = new SweetAlertDialog(this);
        if (!TextUtils.isEmpty(txt)) {
            mSweetAlertDialog.setTitleText(txt);
        }
        mSweetAlertDialog.show();
    }

    public void hideLodingDialog() {
        if (mSweetAlertDialog != null && mSweetAlertDialog.isShowing()) {
            mSweetAlertDialog.dismiss();
        }
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    protected int getNavigationBarHeight(Context context) {
        int resourceId;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            LogUtil.e("navigation：", "-----> navigation_bar_height：" + context.getResources().getDimensionPixelSize(resourceId) + "");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

    /**
     * 获取底部虚拟功能键的高度
     */
    public int getBottomKeyboardHeight() {
        int screenHeight = getAccurateScreenDpi()[1];
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightDifference = screenHeight - dm.heightPixels;
        return heightDifference;
    }

    /**
     * 获取精确的屏幕大小
     */
    public int[] getAccurateScreenDpi() {
        int[] screenWH = new int[2];
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class<?> c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            screenWH[0] = dm.widthPixels;
            screenWH[1] = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWH;
    }
}
