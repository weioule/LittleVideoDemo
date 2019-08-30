package com.example.littlevideodemo.weight;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.littlevideodemo.R;
import com.example.littlevideodemo.utils.StringUtil;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static com.scwang.smartrefresh.layout.constant.RefreshState.PullDownToRefresh;

/**
 * Created by weioule
 * on 2019/4/9.
 */
@SuppressLint("RestrictedApi")
public class MyRefreshHeader extends RelativeLayout implements RefreshHeader {

    public static String REFRESH_HEADER_REFRESHING = "正在努力加载";
    public static String REFRESH_HEADER_PULLDOWN = "下拉推荐";
    public static String REFRESH_HEADER_RELEASE = "松开推荐";

    private Context mContext;
    private View mRecommendView;
    private View mContentView;
    private String mRecommendText;

    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected RefreshKernel mRefreshKernel;
    protected int mBackgroundColor;

    private TextView tv_recommend;
    private TextView mReleaseText;
    protected int mPaddingTop = 0;
    protected int mPaddingBottom = 0;

    public MyRefreshHeader(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MyRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public MyRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View mBackGroundView = mLayoutInflater.inflate(R.layout.refresh_header, this, false);
        mReleaseText = mBackGroundView.findViewById(R.id.tv_header_detail);
        addView(mBackGroundView);

        mRecommendView = mBackGroundView.findViewById(R.id.ll_recommend);
        mContentView = mBackGroundView.findViewById(R.id.ll_content);
        tv_recommend = mBackGroundView.findViewById(R.id.tv_show_top_recommend);
    }

    public void setRecommendView(String recommendText) {
        mRecommendText = recommendText;
        tv_recommend.setText(mRecommendText);
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
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
                mRefreshKernel.moveSpinner(DensityUtil.dp2px(30), true);
            }
            return 800;
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

    public MyRefreshHeader setPrimaryColor(@ColorInt int primaryColor) {
        setBackgroundColor(mBackgroundColor = primaryColor);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundForHeader(mBackgroundColor);
        }
        return this;
    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            if (!(getBackground() instanceof BitmapDrawable)) {
                setPrimaryColor(colors[0]);
            }
            if (colors.length > 1) {
                setAccentColor(colors[1]);
            } else {
                setAccentColor(colors[0] == 0xffffffff ? 0xff666666 : 0xffffffff);
            }
        }
    }

    public MyRefreshHeader setAccentColor(@ColorInt int accentColor) {
        return this;
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundForHeader(mBackgroundColor);
    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {
    }

    protected ValueAnimator.AnimatorUpdateListener reboundUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (mRefreshKernel != null) {
                mRefreshKernel.moveSpinner((int) animation.getAnimatedValue(), true);
            }
        }
    };


    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
    }


}
