package com.example.littlevideodemo.view;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.littlevideodemo.R;
import com.example.littlevideodemo.RefreshItemEvent;
import com.example.littlevideodemo.adapter.DetailAdapter;
import com.example.littlevideodemo.base.BaseActivity;
import com.example.littlevideodemo.base.BaseViewHolder;
import com.example.littlevideodemo.bean.VideoBean;
import com.example.littlevideodemo.presenter.MainPresenter;
import com.example.littlevideodemo.utils.GsonUtil;
import com.example.littlevideodemo.utils.NetWorkUtil;
import com.example.littlevideodemo.view.v.MainView;
import com.example.littlevideodemo.weight.Constants;
import com.example.littlevideodemo.weight.DoubleLikeView;
import com.example.littlevideodemo.weight.FullWindowVideoView;
import com.example.littlevideodemo.weight.LikeView;
import com.example.littlevideodemo.weight.ProgressView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by weioule
 * on 2019/8/4.
 */
public class DetailActivity extends BaseActivity<MainView, MainPresenter> implements MainView, Handler.Callback, DetailAdapter.RemoveItemListener {
    private final int START_PROGRESS_ANIMATION = 8080;

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout refreshView;
    private DetailLayoutManager myLayoutManager;
    private DetailAdapter mAdapter;

    private ImageView imgPlay;
    private ImageView img_thumb;
    private FullWindowVideoView videoView;
    private ProgressView progress;

    private Handler mHandler;

    //最后播放的一条视频的数据
    private int currentPlaying = -1;
    private int mCurrentPage = 1;
    private int isPrepared;
    private boolean doubleClickWindow;
    private boolean preloading, notPreload;
    private boolean hasVideoUri, isBuffer;


    @Override
    protected int getRootViewId() {
        return R.layout.activity_detail;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initView() {
        //禁止锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mHandler = new Handler(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        refreshView = findViewById(R.id.refreshView);
        refreshView.setEnableScrollContentWhenLoaded(false);
        myLayoutManager = new DetailLayoutManager(this, OrientationHelper.VERTICAL, false);
        mRecyclerView.setLayoutManager(myLayoutManager);
        initLisenter();
    }

    @Override
    protected void initData() {
        currentPlaying = getIntent().getIntExtra("position", 0);

        mAdapter = new DetailAdapter(this, Constants.videoDatas);
        mAdapter.setRemoveItemListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(currentPlaying);
        myLayoutManager.setOnSelect(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentPlaying >= 0) {
            playVideo(currentPlaying);
        }
    }

    public void loadData() {
        preloading = true;
        mPresenter.getDatas("", mCurrentPage, bindUntilEvent(ActivityEvent.DESTROY));
    }

    public void initLisenter() {
        refreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshView.setEnableLoadMore(true);
                mCurrentPage = 1;
                notPreload = false;
                loadData();
            }
        });

        refreshView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (!preloading) {
                    preloading = true;
                    mCurrentPage++;
                    loadData();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = myLayoutManager.findLastVisibleItemPosition();
                //上拉预加载
                if (!preloading && !notPreload && (Constants.videoDatas.size() > 1 && Constants.videoDatas.size() - position < 3)) {
                    mCurrentPage++;
                    loadData();
                }
            }
        });

        myLayoutManager.setOnViewPagerListener(new DetailLayoutManager.OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext, final int position) {
                mHandler.removeMessages(START_PROGRESS_ANIMATION);
                int index;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isNext) {
                playVideo(position);
            }
        });
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        if (null == itemView) return;

        final VideoView videoView = itemView.findViewById(R.id.video_view);
        if (null == videoView) return;
        ImageView imgPlay = itemView.findViewById(R.id.img_play);
        ProgressView progress = itemView.findViewById(R.id.progress);
        videoView.stopPlayback();
        progress.cancel();
        imgPlay.animate().alpha(0f).start();
    }

    private void playVideo(int position) {
        if (currentPlaying == position && null != videoView && videoView.isPlaying()) return;
        currentPlaying = position;
        BaseViewHolder holder = (BaseViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
        if (null == holder || null == holder.itemView) return;
        videoView = holder.getView(R.id.video_view);
        if (null == videoView) return;
        videoView.requestFocus();

        imgPlay = holder.getView(R.id.img_play);
        img_thumb = holder.getView(R.id.img_thumb);
        progress = holder.getView(R.id.progress);
        img_thumb.animate().alpha(1).start();
        doPlay();
        setListener(holder);
    }

    private GestureDetector mDetector;
    private long lastDubleTime;

    private void setListener(BaseViewHolder holder) {
        final DoubleLikeView like = holder.getView(R.id.like);
        final LikeView likeView = holder.getView(R.id.lv_icon);
        final TextView give_a_like_num = holder.getView(R.id.give_a_like_num);

        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //连续双击最后的单数，google的GestureDetector把它归类为单击，我认为不应该归类为单击的，在这里将它归属于双击
                if (System.currentTimeMillis() - lastDubleTime <= 600) {
                    onDoubleTap(e);
                    return false;
                }

                lastDubleTime = 0;

                //正在加载中则不响应、缓冲中不响应
                if (null == videoView || isPrepared < 0 && !error || (isBuffer && videoView.isPlaying())) {
                    return false;
                }

                if (videoView.isPlaying() && !isBuffer) {
                    imgPlay.animate().alpha(01f).scaleX(0.4f).scaleY(0.4f).start();
                    videoView.pause();
                    progress.pause();
                } else {
                    //之前没网络或是4G网络，没进入播放流程，没设置过uri，则进入播放逻辑
                    if (!hasVideoUri) {
                        doPlay();
                        return false;
                    }

                    if (0 == NetWorkUtil.getAPNType(DetailActivity.this)) {
                        toast("当前无网络可用,请检查网络!");
                        return false;
                    }

                    imgPlay.animate().alpha(0f).scaleX(1f).scaleY(1f).start();
                    videoView.start();
                    if (error) {
                        //之前播放失败，则重新展示加载动画
                        progress.startAnimation();
                    } else {
                        //正常情况下，就是点击继续播放
                        progress.resume();
                    }
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                lastDubleTime = System.currentTimeMillis();
                doubleClickWindow = true;
                like.startAnimator(e);
                likeView.performClick();
                return true;
            }
        });

        like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoBean.Consumption consumption = Constants.videoDatas.get(currentPlaying).getData().getContent().getData().getConsumption();

                //双击只能点赞，不能取消点赞
                if ((doubleClickWindow && likeView.isChecked()) || (doubleClickWindow && consumption.isHas_favour()) && !likeView.isChecked()) {
                    doubleClickWindow = false;
                    return;
                }

                doubleClickWindow = false;

                //之前就已经点过赞的，再点就取消
                if (consumption.isHas_favour() && !likeView.isChecked()) {
                    likeView.setChecked(false);
                    likeView.setDefaultIcon(getResources().getDrawable(R.drawable.not_thumb_up));
                } else {
                    likeView.toggle();
                }

                consumption.setHas_favour(likeView.isChecked() ? true : false);

                int shareCount = consumption.getShareCount();
                if (likeView.isChecked()) {
                    consumption.setShareCount(++shareCount);
                } else {
                    consumption.setShareCount(--shareCount);
                }

                give_a_like_num.setText(shareCount + "");

                EventBus.getDefault().post(new RefreshItemEvent(currentPlaying, 1));
            }
        });
    }

    private boolean error;

    private void doPlay() {
        error = false;
        hasVideoUri = false;

        //在无线网下自己播放
        if (NetWorkUtil.getAPNType(this) == ConnectivityManager.TYPE_WIFI) {
            startVideoPlay();
        } else if (0 == NetWorkUtil.getAPNType(this)) {
            imgPlay.animate().alpha(01f).scaleX(0.4f).scaleY(0.4f).start();
            toast("当前无网络可用,请检查网络!");
        } else {
            startVideoPlay();
        }
    }

    private void startVideoPlay() {
        if (null == videoView) return;
        isPrepared = -1;
        imgPlay.animate().alpha(0f).scaleX(1f).scaleY(1f).start();
        videoView.setVideoURI((Uri) videoView.getTag());
        videoView.start();
        hasVideoUri = true;

        //延迟一秒开启加载动画
        Message message = Message.obtain();
        message.what = START_PROGRESS_ANIMATION;
        mHandler.sendMessageDelayed(message, 1000);

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                img_thumb.animate().alpha(0).setDuration(200).start();

                isPrepared = 1;

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    //如果没在播放，则手动播放
                    if (!videoView.isPlaying()) {
                        mp.seekTo(videoView.getCurrentPosition());
                        mp.start();
                    }

                    if (videoView.isPlaying()) {
                        //播放后移除加载动画任务
                        mHandler.removeMessages(START_PROGRESS_ANIMATION);
                        progress.cancelAnimation();
                        progress.setProgress(0, videoView);
                        error = false;
                        isBuffer = false;

                        //观看次数++
                        int collectionCount = Constants.videoDatas.get(currentPlaying).getData().getContent().getData().getConsumption().getCollectionCount();
                        Constants.videoDatas.get(currentPlaying).getData().getContent().getData().getConsumption().setCollectionCount(++collectionCount);

                        EventBus.getDefault().post(new RefreshItemEvent(currentPlaying, 1));
                    }

                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    //缓冲中
                    isBuffer = true;
                    progress.end();
                    progress.startAnimation();

                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    //恢复播放
                    isBuffer = false;
                    progress.setProgress(videoView.getCurrentPosition(), videoView);
                }
                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                if (null == arg0 || arg0.isPlaying()) return;
                if (!error) {
                    videoView.seekTo(0);
                    videoView.start();
                    progress.setProgress(0, videoView);
                }
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progress.cancelAnimation();

                error = true;
                imgPlay.animate().alpha(01f).scaleX(0.4f).scaleY(0.4f).start();
                if (0 == NetWorkUtil.getAPNType(DetailActivity.this)) {
                    toast("当前无网络可用,请检查网络!");
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != videoView) videoView.pause();
        if (null != progress) progress.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != videoView)
            videoView.stopPlayback();
        mHandler.removeCallbacksAndMessages(null);

        EventBus.getDefault().post(new RefreshItemEvent(currentPlaying, 2));
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (null != msg && START_PROGRESS_ANIMATION == msg.what) {
            if (null == videoView) return false;

            if (!error && null != progress)
                progress.startAnimation();

        }
        return true;
    }

    public void removeItem(final int position) {
        Constants.videoDatas.remove(position);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void setDatas(String result, int state) {
        preloading = false;
        if (1 == state) {
            currentPlaying = -1;
            myLayoutManager.setOnSelect(true);
            Constants.videoDatas.clear();
            mAdapter.notifyDataSetChanged();
            refreshView.finishRefresh();
        } else {
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
        preloading = false;
        if (1 == state) {
            currentPlaying = -1;
            refreshView.finishRefresh();
        } else {
            refreshView.finishLoadMore();
        }
        if (Constants.videoDatas.size() <= 0) showErrorView(null);
    }

    @Override
    public void loadDatas() {
    }

    @Override
    public void refresh() {
    }

}