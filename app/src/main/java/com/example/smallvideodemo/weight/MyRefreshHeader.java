package com.example.smallvideodemo.weight;

import android.animation.ValueAnimator;
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

import com.example.smallvideodemo.R;
import com.example.smallvideodemo.utils.StringUtil;
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
public class MyRefreshHeader extends RelativeLayout implements RefreshHeader {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private View mRecommendView;
    private View mContentView;

    private TextView tv_show_top_recommend;
    private String mRecommendText;

    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected RefreshKernel mRefreshKernel;
    protected int mBackgroundColor;

    private View mBackGroundView;
    private TextView tv_header_detail;
    protected int mPaddingTop = 0;
    protected int mPaddingBottom = 0;

    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";

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
        mLayoutInflater = LayoutInflater.from(mContext);
        mBackGroundView = mLayoutInflater.inflate(R.layout.refresh_header, this, false);
        tv_header_detail = mBackGroundView.findViewById(R.id.tv_header_detail);
        addView(mBackGroundView);

        mRecommendView = mBackGroundView.findViewById(R.id.ll_recommend);
        mContentView = mBackGroundView.findViewById(R.id.ll_content);
        tv_show_top_recommend = mBackGroundView.findViewById(R.id.tv_show_top_recommend);
    }

    public TextView getRecommendView() {
        return tv_show_top_recommend;
    }

    public void setRecommendView(String recommendText) {
        mRecommendText = recommendText;
        tv_show_top_recommend.setText(mRecommendText);
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (!StringUtil.isEmpty(mRecommendText)) {
            mRecommendView.setVisibility(View.GONE);
            mContentView.setVisibility(View.GONE);
            mRecommendView.setVisibility(View.VISIBLE);
            if (mRefreshKernel != null) {
                mRefreshKernel.moveSpinner(DensityUtil.dp2px(30), true);
            }
            return 1000;
        } else {
            if (success) {
                tv_header_detail.setText(REFRESH_HEADER_FINISH);
            } else {
                tv_header_detail.setText(REFRESH_HEADER_FAILED);
            }
            return 500;
        }
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
            case PullDownToRefresh:
                tv_header_detail.setText("下拉可以刷新");
                break;
            case Refreshing:
            case RefreshReleased:
                tv_header_detail.setText("正在刷新");
                break;
            case ReleaseToRefresh:
                tv_header_detail.setText("释放立即刷新");
                break;
            case Loading:
                tv_header_detail.setText("正在加载...");
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
