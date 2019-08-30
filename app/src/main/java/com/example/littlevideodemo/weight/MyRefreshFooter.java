package com.example.littlevideodemo.weight;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.littlevideodemo.R;
import com.example.littlevideodemo.utils.Utility;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * Created by weioule
 * on 2019/4/9.
 */
@SuppressLint("RestrictedApi")
public class MyRefreshFooter extends LinearLayout implements RefreshFooter {

    private View rootView;
    private Context mContext;
    private ImageView iv_loading;
    private TextView tv_footer;
    private String footerText;
    private ObjectAnimator objectAnimator;

    public MyRefreshFooter(Context context) {
        this(context, null);
    }

    public MyRefreshFooter(Context context, String text) {
        this(context, text, null);
    }

    public MyRefreshFooter(Context context, String text, @Nullable AttributeSet attrs) {
        this(context, text, attrs, 0);
    }

    public MyRefreshFooter(Context context, String text, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.footerText = text;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.refresh_footer, null);
            iv_loading = rootView.findViewById(R.id.footer_loading);
            tv_footer = rootView.findViewById(R.id.footer_text);
            if (!TextUtils.isEmpty(footerText))
                tv_footer.setText(footerText);
            int size = Utility.dp2px(mContext, 45);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, size);
            rootView.setLayoutParams(params);
            addView(rootView);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {
        objectAnimator = ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 360f);
        objectAnimator.setDuration(2500);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(10);
        objectAnimator.setRepeatMode(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (null != objectAnimator && objectAnimator.isRunning()) {
            objectAnimator.end();
        }
        return 0;
    }

    @Override
    public void setPrimaryColors(int... colors) {
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
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
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return false;
    }
}
