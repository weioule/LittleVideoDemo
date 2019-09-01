package com.example.littlevideodemo.view;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.littlevideodemo.R;
import com.example.littlevideodemo.RefreshItemEvent;
import com.example.littlevideodemo.adapter.MainAdapter;
import com.example.littlevideodemo.base.BaseRecylerViewAdapter;
import com.example.littlevideodemo.base.BaseTitleActivity;
import com.example.littlevideodemo.bean.VideoBean;
import com.example.littlevideodemo.presenter.MainPresenter;
import com.example.littlevideodemo.utils.GsonUtil;
import com.example.littlevideodemo.view.v.MainView;
import com.example.littlevideodemo.weight.Constants;
import com.example.littlevideodemo.weight.MyRefreshHeader;
import com.example.littlevideodemo.weight.RefreshHeaderView.ToutiaoRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class MainActivity extends BaseTitleActivity<MainView, MainPresenter> implements MainView, BaseRecylerViewAdapter.OnListItemClickedListener {

    private MainAdapter mAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private SmartRefreshLayout refreshView;

    private String tag = "todayVideo";
    private RecyclerView recyclerView;
    private boolean preloading, notPreload;
    private int lastPosition;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        //页面数据异常时重新加载并初始化，EventBus不能多次注册
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        hideLeftImage();
        mTitleText.setText(R.string.little_video);
        recyclerView = findViewById(R.id.recycler_view);
        refreshView = findViewById(R.id.refresh_view);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);

        mAdapter = new MainAdapter(this, Constants.videoDatas);
        mAdapter.setOnListItemClickedListener(this);
        recyclerView.setAdapter(mAdapter);

        refreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                notPreload = false;
                mPresenter.getDatas(tag, 1, bindUntilEvent(ActivityEvent.DESTROY));
            }
        });

        refreshView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (!preloading) {
                    preloading = true;
                    mPresenter.getDatas(tag, 2, bindUntilEvent(ActivityEvent.DESTROY));
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] positions = layoutManager.findLastVisibleItemPositions(new int[2]);
                //上拉预加载
                if (!preloading && !notPreload && (Constants.videoDatas.size() > 1 && Constants.videoDatas.size() - positions[1] <= 4)) {
                    preloading = true;
                    mPresenter.getDatas(tag, 2, bindUntilEvent(ActivityEvent.DESTROY));
                }

            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.getCacheData(tag);
    }

    @Override
    public void loadDatas() {
        showLodingDialog();
        mPresenter.getDatas(tag, 0, bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    public void refresh() {
        refreshView.autoRefresh();
    }

    @Override
    public void setDatas(String result, int state) {
        if (0 == state) {
            hideLodingDialog();
        } else if (1 == state) {
            Constants.videoDatas.clear();
            refreshView.finishRefresh();
        } else if (state > 1) {
            preloading = false;
            refreshView.finishLoadMore();
        }

        List<VideoBean> beanList = GsonUtil.toList(result, VideoBean.class);
        if (null == beanList || beanList.size() <= 0) {
            if (Constants.videoDatas.size() <= 0) {
                showErrorView(null);
            } else {
                showHint(state, getString(R.string.no_recommendation));
                notPreload = true;
            }
            return;
        }

        int count = 0;
        int oldSize = Constants.videoDatas.size();
        for (VideoBean bean : beanList) {
            if (null != bean.getType() && "followCard".equals(bean.getType())) {
                Constants.videoDatas.add(bean);
                count++;
            }
        }

        String hint = String.format(getResources().getString(R.string.have_recommended), count);
        showHint(state, hint);

        mAdapter.notifyItemRangeChanged(oldSize, count);
    }

    private void showHint(int state, String hint) {
        if (1 == state) {
            RefreshHeader refreshHeader = refreshView.getRefreshHeader();
            if (refreshHeader instanceof MyRefreshHeader) {
                ((MyRefreshHeader) refreshHeader).setRecommendView(hint);
            } else if (refreshHeader instanceof ToutiaoRefreshHeader) {
                ((ToutiaoRefreshHeader) refreshHeader).setRecommendView(hint);
            }
        }
    }

    @Override
    public void onError(String msg, int state) {
        if (1 == state) {
            refreshView.finishRefresh();
        } else if (state > 1) {
            preloading = false;
            refreshView.finishLoadMore();
        }
        if (Constants.videoDatas.size() <= 0) showErrorView(null);
    }

    @Override
    public void onItemClicked(int position) {
        lastPosition = layoutManager.findLastVisibleItemPositions(new int[2])[1];
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("position", position);
        forward(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RefreshItemEvent event) {
        if (event.getType() == 1) {
            mAdapter.notifyItemChanged(event.getPosition());
        } else if (event.getType() == 2) {
            mAdapter.notifyItemRangeChanged(lastPosition + 1, Constants.videoDatas.size() - lastPosition);
            recyclerView.smoothScrollToPosition(event.getPosition());
        }
    }

}
