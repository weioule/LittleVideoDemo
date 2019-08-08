package com.example.smallvideodemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smallvideodemo.R;
import com.example.smallvideodemo.base.BaseRecylerViewAdapter;
import com.example.smallvideodemo.base.BaseViewHolder;
import com.example.smallvideodemo.bean.VideoBean;
import com.example.smallvideodemo.glide.GlideApp;
import com.example.smallvideodemo.listener.NoDoubleClickListener;
import com.example.smallvideodemo.utils.Utility;

import java.util.List;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class MainAdapter extends BaseRecylerViewAdapter<VideoBean> {

    private OnListItemClickedListener onListItemClickedListener;
    private int actual_width;

    public MainAdapter(Context context, List<VideoBean> datas) {
        super(context, datas);
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.main_item;
    }

    @Override
    protected void bindData(BaseViewHolder holder, VideoBean bean, int adapterPosition) {
        TextView tv_title = holder.getView(R.id.item_title);
        TextView browse_num = holder.getView(R.id.browse_num);
        final TextView give_a_like_num = holder.getView(R.id.give_a_like_num);
        final ImageView give_a_like = holder.getView(R.id.give_a_like);
        final ImageView item_img = holder.getView(R.id.item_img);
        ImageView item_bg = holder.getView(R.id.item_bg);

        int itemHeight;
        if (0 == adapterPosition) {
            itemHeight = Utility.dp2px(holder.itemView.getContext(), 235);
            //高度一致则无需再次设置
            if (itemHeight != holder.itemView.getLayoutParams().height)
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        } else {
            itemHeight = Utility.dp2px(holder.itemView.getContext(), 275);
            //高度一致则无需再次设置
            if (itemHeight != holder.itemView.getLayoutParams().height)
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        }

        if (actual_width <= 0)
            actual_width = (Utility.getsW((Activity) mContext) - Utility.dp2px(holder.itemView.getContext(), 8) / 2);

        GlideApp.with(holder.itemView.getContext())
                .asDrawable()
                .load(bean.getData().getContent().getData().getCover().getBlurred())
                .placeholder(R.drawable.shape_black)
                .error(R.drawable.shape_black)
                .into(item_bg);

        GlideApp.with(holder.itemView.getContext())
                .asDrawable()
                .load(bean.getData().getContent().getData().getCover().getDetail())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        if (null == item_img || null == holder || null == holder.itemView)
                            return false;
                        int image_width = resource.getIntrinsicWidth();
                        int image_height = resource.getIntrinsicHeight();

                        int scale = actual_width / image_width;
                        int iamgHeight = image_height * (scale > 1 ? 1 : scale);
                        if (iamgHeight > 0) {
                            ViewGroup.LayoutParams params = item_img.getLayoutParams();
                            params.height = iamgHeight;
                            item_img.setLayoutParams(params);
                        }
                        return false;
                    }
                })
                .into(item_img);

        tv_title.setText(bean.getData().getContent().getData().getTitle());
        browse_num.setText(bean.getData().getContent().getData().getConsumption().getCollectionCount() + "");
        give_a_like_num.setText(bean.getData().getContent().getData().getConsumption().getShareCount() + "");
        give_a_like.setSelected(bean.getData().getContent().getData().getConsumption().isHas_favour());

        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (null != onListItemClickedListener)
                    onListItemClickedListener.onItemClicked(adapterPosition);
            }
        });
    }

    public void setOnListItemClickedListener(OnListItemClickedListener onListItemClickedListener) {
        this.onListItemClickedListener = onListItemClickedListener;
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        ImageView item_img = holder.getView(R.id.item_img);
        ImageView item_bg = holder.getView(R.id.item_bg);
        if (item_img != null) {
            GlideApp.with(holder.itemView.getContext()).clear(item_img);
        }
        if (item_bg != null) {
            GlideApp.with(holder.itemView.getContext()).clear(item_bg);
        }
    }
}
