package com.example.smallvideodemo.weight.RefreshHeaderView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smallvideodemo.utils.LogUtil;
import com.example.smallvideodemo.utils.Utility;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * @author weioule
 * @date 2019/7/26.
 */
@SuppressLint("RestrictedApi")
public class TodayNewsHeader extends LinearLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉推荐";
    public static String REFRESH_HEADER_REFRESHING = "正在努力加载";
    public static String REFRESH_HEADER_RELEASE = "松开推荐";
    private NewRefreshView mNewRefreshView;
    private TextView releaseText;
    private RefreshKernel mRefreshKernel;
    protected int mBackgroundColor;

    public TodayNewsHeader(Context context) {
        this(context, null);
    }

    public TodayNewsHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TodayNewsHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0x00000000);

        mNewRefreshView = new NewRefreshView(context);
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout.LayoutParams lpNewRefresh = new LayoutParams(Utility.dp2px(getContext(), 25), Utility.dp2px(getContext(), 25));
        lpNewRefresh.setMargins(0, Utility.dp2px(context, 10), 0, 0);

        this.addView(mNewRefreshView, lpNewRefresh);

        LinearLayout.LayoutParams lpReleaseText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpReleaseText.setMargins(0, Utility.dp2px(context, 6), 0, Utility.dp2px(context, 5));

        releaseText = new TextView(context);
        releaseText.setText(REFRESH_HEADER_PULLDOWN);
        releaseText.setTextColor(0xff666666);
        releaseText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        addView(releaseText, lpReleaseText);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mNewRefreshView.setDragState();
            mHandler.sendEmptyMessageDelayed(0, 350);
        }
    };

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            if (!(getBackground() instanceof BitmapDrawable)) {
                setPrimaryColor(colors[0]);
            }
        }
    }

    public void setPrimaryColor(@ColorInt int primaryColor) {
        setBackgroundColor(mBackgroundColor = primaryColor);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundForHeader(mBackgroundColor);
        }
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundForHeader(mBackgroundColor);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        LogUtil.e("TAG", "fraction:" + percent);
        mNewRefreshView.setFraction((percent - 0.8f) * 6f);
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        onPulling(percent, offset, height, extendHeight);
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mHandler.removeCallbacksAndMessages(null);
        mNewRefreshView.setDrag();
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                releaseText.setText(REFRESH_HEADER_PULLDOWN);
                break;
            case PullUpToLoad:
                break;
            case ReleaseToRefresh:
                releaseText.setText(REFRESH_HEADER_RELEASE);
                break;
            case Refreshing:
                releaseText.setText(REFRESH_HEADER_REFRESHING);
                break;
            case Loading:
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

}