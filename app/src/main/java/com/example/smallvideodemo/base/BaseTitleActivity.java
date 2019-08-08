package com.example.smallvideodemo.base;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.example.smallvideodemo.R;
import com.example.smallvideodemo.presenter.BasePresenter;
import com.example.smallvideodemo.view.v.BaseView;


/**
 * Author by weioule.
 * Date on 2018/10/29.
 */
@SuppressWarnings({"unchecked", "deprecation"})
public abstract class BaseTitleActivity<V extends BaseView, M extends BaseModel, P extends BasePresenter<V, M>> extends BaseActivity {

    protected P mPresenter;
    public TextView mTitleText;
    protected TextView mLeftText;
    protected TextView mRightText;
    protected ImageView mLeftImage;
    public ImageView mRightImage;
    protected RelativeLayout titleBar;

    @Override
    protected int getRootViewId() {
        return R.layout.activity_title_base;
    }

    @Override
    protected final void initView() {
        mPresenter = createPresenter();
        if (null != mPresenter)
            mPresenter.attachView((V) this);
        titleBar = findViewById(R.id.title_layout);
        mTitleText = titleBar.findViewById(R.id.title_tv_title);
        mLeftImage = titleBar.findViewById(R.id.title_iv_left);
        mLeftText = titleBar.findViewById(R.id.title_tv_left);
        mRightImage = titleBar.findViewById(R.id.title_iv_right);
        mRightText = titleBar.findViewById(R.id.title_tv_right);
        mLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLeftImageClick();
            }
        });

        int body = getLayoutId();
        if (body != 0) {
            LayoutInflater.from(this).inflate(body, (ViewGroup) findViewById(R.id.container), true);
        }

        initViews();
    }

    protected abstract int getLayoutId();

    protected abstract P createPresenter();

    protected abstract void initViews();

    @Override
    protected void onDestroy() {
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        super.onDestroy();
    }


    public void showErrorViews() {
        showErrorView(mTitleText);
    }

    protected void onLeftImageClick() {
        finish();
    }

    protected void addLeftImage(@DrawableRes int resid) {
        addLeftImage(getResources().getDrawable(resid));
    }

    protected void addLeftImage(Drawable drawable) {
        mLeftImage.setImageDrawable(drawable);
    }

    protected void addRightImage(@DrawableRes int resid) {
        addRightImage(getResources().getDrawable(resid));
    }

    protected void addRightImage(Drawable drawable) {
        mRightImage.setImageDrawable(drawable);
        mRightImage.setVisibility(View.VISIBLE);
    }
}
