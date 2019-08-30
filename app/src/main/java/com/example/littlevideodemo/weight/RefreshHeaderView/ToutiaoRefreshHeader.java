package com.example.littlevideodemo.weight.RefreshHeaderView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.littlevideodemo.R;
import com.example.littlevideodemo.utils.LogUtil;
import com.example.littlevideodemo.utils.StringUtil;
import com.example.littlevideodemo.utils.Utility;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import static com.scwang.smartrefresh.layout.constant.RefreshState.PullDownToRefresh;

/**
 * @author weioule
 * @date 2019/7/26.
 */
@SuppressLint("RestrictedApi")
public class ToutiaoRefreshHeader extends RelativeLayout implements RefreshHeader {

    public static String REFRESH_HEADER_REFRESHING = "正在努力加载";
    public static String REFRESH_HEADER_PULLDOWN = "下拉推荐";
    public static String REFRESH_HEADER_RELEASE = "松开推荐";
    private NewRefreshView mNewRefreshView;
    private TextView mReleaseText;
    private RefreshKernel mRefreshKernel;
    protected int mBackgroundColor;
    private String mRecommendText;
    private TextView tv_recommend;
    private View mRecommendView;
    private View mContentView;
    protected int mPaddingTop = 0;
    protected int mPaddingBottom = 0;

    public ToutiaoRefreshHeader(Context context) {
        this(context, null);
    }

    public ToutiaoRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToutiaoRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        View mBackGroundView = mLayoutInflater.inflate(R.layout.toutiao_refresh_header, this, false);
        mReleaseText = mBackGroundView.findViewById(R.id.tv_header_detail);
        addView(mBackGroundView);

        mNewRefreshView = mBackGroundView.findViewById(R.id.toutiao_refresh_view);
        mRecommendView = mBackGroundView.findViewById(R.id.ll_recommend);
        mContentView = mBackGroundView.findViewById(R.id.ll_content);
        tv_recommend = mBackGroundView.findViewById(R.id.tv_show_top_recommend);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mNewRefreshView.setDragState();
            mHandler.sendEmptyMessageDelayed(0, 350);
        }
    };

    public void setRecommendView(String recommendText) {
        mRecommendText = recommendText;
        tv_recommend.setText(mRecommendText);
    }

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

        if (!StringUtil.isEmpty(mRecommendText)) {
            mContentView.setVisibility(View.GONE);
            mRecommendView.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tv_recommend, "scaleX", 0.5f, 1.1f, 1f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(tv_recommend, "scaleY", 0.5f, 1.1f, 1f);
            objectAnimator.setDuration(300);
            objectAnimator2.setDuration(300);
            objectAnimator.start();
            objectAnimator2.start();

            if (mRefreshKernel != null) {
                mRefreshKernel.moveSpinner(Utility.dp2px(getContext(), 30), true);
            }
            return 1000;
        }
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
        } else {
            setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        if (newState == PullDownToRefresh) {
            mRecommendView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mReleaseText.setText(REFRESH_HEADER_PULLDOWN);
                break;
            case PullUpToLoad:
                break;
            case ReleaseToRefresh:
                mReleaseText.setText(REFRESH_HEADER_RELEASE);
                break;
            case Refreshing:
                mReleaseText.setText(REFRESH_HEADER_REFRESHING);
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