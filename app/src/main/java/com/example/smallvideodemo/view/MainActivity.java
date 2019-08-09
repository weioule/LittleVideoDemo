package com.example.smallvideodemo.view;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smallvideodemo.R;
import com.example.smallvideodemo.RefreshItemEvent;
import com.example.smallvideodemo.adapter.MainAdapter;
import com.example.smallvideodemo.base.BaseActivity;
import com.example.smallvideodemo.base.BaseRecylerViewAdapter;
import com.example.smallvideodemo.bean.VideoBean;
import com.example.smallvideodemo.presenter.MainPresenter;
import com.example.smallvideodemo.utils.GsonUtil;
import com.example.smallvideodemo.view.v.MainView;
import com.example.smallvideodemo.weight.Constants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView, BaseRecylerViewAdapter.OnListItemClickedListener {

    private MainAdapter mAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private SmartRefreshLayout refreshView;

    private String tag = "todayVideo";
    private RecyclerView recyclerView;
    private boolean preloading, notPreload;
    private int lastPosition;

    @Override
    protected int getRootViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
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
                notPreload = true;
            }
            return;
        }

        int oldSize = Constants.videoDatas.size();
        for (VideoBean bean : beanList) {
            if (null != bean.getType() && "followCard".equals(bean.getType())) {
                Constants.videoDatas.add(bean);
            }
        }

        mAdapter.notifyItemRangeChanged(oldSize, beanList.size());
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
            mAdapter.notifyItemRangeChanged(lastPosition + 1, event.getPosition() - lastPosition);
            lastPosition = event.getPosition();
        } else if (event.getType() == 2) {
            recyclerView.smoothScrollToPosition(event.getPosition());
        }
    }

}
