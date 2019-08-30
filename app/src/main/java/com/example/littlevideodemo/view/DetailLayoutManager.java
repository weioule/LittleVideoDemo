package com.example.littlevideodemo.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by weioule
 * on 2019/8/4.
 */
public class DetailLayoutManager extends LinearLayoutManager implements RecyclerView.OnChildAttachStateChangeListener {
    private int mDrift;//位移，用来判断移动方向
    private PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private boolean onSelect;

    public DetailLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
    }

    public void setOnSelect(boolean onSelect) {
        this.onSelect = onSelect;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        view.addOnChildAttachStateChangeListener(this);
        mPagerSnapHelper.attachToRecyclerView(view);
        super.onAttachedToWindow(view);
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        int position = getPosition(view);
        if (onSelect && mOnViewPagerListener != null) {
            onSelect = false;
            mOnViewPagerListener.onPageSelected(position, mDrift >= 0);
        }
    }

    public void setOnViewPagerListener(OnViewPagerListener mOnViewPagerListener) {
        this.mOnViewPagerListener = mOnViewPagerListener;
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                View view = mPagerSnapHelper.findSnapView(this);
                int position = getPosition(view);
                if (mOnViewPagerListener != null) {
                    mOnViewPagerListener.onPageSelected(position, mDrift >= 0);
                }
                break;
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        //暂停播放操作
        if (mOnViewPagerListener != null)
            mOnViewPagerListener.onPageRelease(mDrift >= 0, getPosition(view));
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    public interface OnViewPagerListener {
        /*释放的监听*/
        void onPageRelease(boolean isNext, int position);

        /*选中的监听以及判断是否滑动到底部*/
        void onPageSelected(int position, boolean isNext);
    }

}