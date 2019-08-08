package com.example.smallvideodemo.glide;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.example.smallvideodemo.net.okhttp.OkHttpForGlide;
import com.example.smallvideodemo.utils.LogUtil;

import java.io.InputStream;

/**
 * @author weioule
 * @date 2019/8/3.
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    private static final String TAG = MyAppGlideModule.class.getSimpleName();

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();// 分配的可用内存
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;//最大内存缓存
    public static final int MAX_DISK_CACHE_SIZE = 300 * 1024 * 1024;// 默认图磁盘缓存的最大值
    private static final String IMAGE_PIPELINE_CACHE_DIR = "kdlc_default_cache";// 默认图所放路径的文件夹名

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        Log.i(TAG, (MAX_MEMORY_CACHE_SIZE / 1024 / 1024) + "M");
        builder.setMemoryCache(new LruResourceCache(MAX_MEMORY_CACHE_SIZE));
        //根据SD卡是否可用选择是在内部缓存还是SD卡缓存
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, IMAGE_PIPELINE_CACHE_DIR, MAX_DISK_CACHE_SIZE));
        } else {
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, IMAGE_PIPELINE_CACHE_DIR, MAX_DISK_CACHE_SIZE));
        }
        builder.setLogLevel(LogUtil.WARN);
        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.default_img)
                //.error(R.drawable.default_img)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)//为true则不使用内存缓存
                .fitCenter();
        builder.setDefaultRequestOptions(options);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //使用定义的OkhttpClient替换原有的请求
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(OkHttpForGlide.getOkhttpClient()));
    }

    //针对V4用户可以提升速度
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
