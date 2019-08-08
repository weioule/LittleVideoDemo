package com.example.smallvideodemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.smallvideodemo.R;
import com.example.smallvideodemo.base.BaseRecylerViewAdapter;
import com.example.smallvideodemo.base.BaseViewHolder;
import com.example.smallvideodemo.bean.VideoBean;
import com.example.smallvideodemo.glide.GlideApp;
import com.example.smallvideodemo.utils.Utility;
import com.example.smallvideodemo.view.DetailActivity;
import com.example.smallvideodemo.weight.FullWindowVideoView;
import com.example.smallvideodemo.weight.SmallVideoLikeView;

import java.util.List;

/**
 * @author weioule
 * @date 2019/8/4.
 */
public class DetailAdapter extends BaseRecylerViewAdapter<VideoBean> {

    protected RemoveItemListener removeItemListener;
    private int actual_width;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.detail_item;
    }

    public DetailAdapter(Context activity, List<VideoBean> mDataLists) {
        super(activity, mDataLists);
    }

    @Override
    protected void bindData(BaseViewHolder holder, VideoBean bean, int adapterPosition) {
        if (bean == null) {
            if (null != removeItemListener)
                removeItemListener.removeItem(adapterPosition);
            return;
        }

        final FullWindowVideoView videoView = holder.getView(R.id.video_view);
        final ImageView img_thumb = holder.getView(R.id.img_thumb);

        if (actual_width <= 0)
            actual_width = Utility.getsW((Activity) mContext);

        VideoBean.ContentData data = bean.getData().getContent().getData();

        GlideApp.with(holder.itemView.getContext())
                .asDrawable()
                .placeholder(R.drawable.shape_black)
                .load(data.getCover().getDetail())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        if (null == img_thumb || null == holder || null == holder.itemView)
                            return false;
                        int image_width = resource.getIntrinsicWidth();
                        int image_height = resource.getIntrinsicHeight();

                        int scale = actual_width / image_width;
                        int iamgHeight = image_height * (scale > 1 ? 1 : scale);
                        if (iamgHeight > 0) {
                            ViewGroup.LayoutParams params = img_thumb.getLayoutParams();
                            params.height = iamgHeight;
                            img_thumb.setLayoutParams(params);
                            videoView.setLayoutParams(params);
                        }
                        return false;
                    }
                })
                .into(img_thumb);


        GlideApp.with(holder.itemView.getContext())
                .asDrawable()
                .load(data.getAuthor().getIcon())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into((ImageView) holder.getView(R.id.author_header));

        ((TextView) holder.getView(R.id.author_name)).setText(data.getAuthor().getName());
        ((TextView) holder.getView(R.id.item_title)).setText(data.getAuthor().getDescription());
        ((TextView) holder.getView(R.id.comments_num)).setText(data.getConsumption().getReplyCount() + "");
        ((TextView) holder.getView(R.id.give_a_like_num)).setText(data.getConsumption().getShareCount() + "");
        holder.getView(R.id.video_view).setTag(Uri.parse(data.getPlayUrl()));

        if (data.getConsumption().isHas_favour())
            ((SmallVideoLikeView) holder.getView(R.id.lv_icon)).setDefaultIcon(mContext.getResources().getDrawable(R.drawable.give_a_like));

        holder.getView(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mContext && mContext instanceof DetailActivity)
                    ((DetailActivity) mContext).finish();
            }
        });
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        ImageView img_thumb = holder.getView(R.id.img_thumb);
        if (img_thumb != null) {
            GlideApp.with(holder.itemView.getContext()).clear(img_thumb);
        }
    }

    public void setRemoveItemListener(RemoveItemListener removeItemListener) {
        this.removeItemListener = removeItemListener;
    }

    public interface RemoveItemListener {
        void removeItem(int podition);
    }
}
