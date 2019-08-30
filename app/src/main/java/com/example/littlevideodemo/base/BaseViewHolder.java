package com.example.littlevideodemo.base;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

/**
 * Author by weioule.
 * Date on 2018/8/17.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public <T extends View> T getView(int viewId) {
        return itemView.findViewById(viewId);
    }

    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener mLis) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(mLis);
        }
        return this;
    }

    public BaseViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        if (view != null)
            view.setText(text);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (view != null)
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        if (view != null)
            view.setImageResource(drawableId);
        return this;
    }

    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        if (view != null)
            view.setImageBitmap(bitmap);
        return this;
    }

    public BaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (view != null)
            view.setImageDrawable(drawable);
        return this;
    }

    public BaseViewHolder setImgUrl(int viewId, String url) {
        ImageView view = getView(viewId);
        if (view != null)
            Glide.with(view)
                    .load(url)
                    .into(view);
        return this;
    }

    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        if (view != null)
            view.setBackgroundColor(color);
        return this;
    }

    public BaseViewHolder setBackgroundResource(int viewId, int backgroundRes) {
        View view = getView(viewId);
        if (view != null)
            view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        if (view != null)
            view.setTextColor(textColor);
        return this;
    }

    public BaseViewHolder setTextColorResource(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        if (view != null)
            view.setTextColor(ContextCompat.getColor(itemView.getContext(), textColorRes));
        return this;
    }

    public BaseViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            View view = getView(viewId);
            if (view != null)
                view.setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            View view = getView(viewId);
            if (view != null)
                view.startAnimation(alpha);
        }
        return this;
    }

    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        if (view != null)
            view.setOnTouchListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null)
            view.setOnLongClickListener(listener);
        return this;
    }
}
