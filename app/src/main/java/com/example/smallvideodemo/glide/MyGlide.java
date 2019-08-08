package com.example.smallvideodemo.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class MyGlide {

    public static void load(Context context, String url, ImageView imageView) {
        GlideApp.with(context).load(url).dontAnimate().into(imageView);
    }

    public static void load(Context context, String url, RequestListener listener, ImageView imageView) {
        GlideApp.with(context).asBitmap().load(url).dontAnimate().listener(listener).into(imageView);
    }

    public static void load(Context context, String url, RequestOptions options, ImageView imageView) {
        GlideApp.with(context).load(url).dontAnimate().apply(options).into(imageView);
    }

    public static void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GlideApp.get(context).clearDiskCache();
            }
        }).start();
    }

    public static void clearMemoryCache(Context context) {
        GlideApp.get(context).clearMemory();
    }
}
