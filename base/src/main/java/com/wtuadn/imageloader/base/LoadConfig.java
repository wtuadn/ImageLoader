package com.wtuadn.imageloader.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtuadn on 2018/2/10.
 */

public class LoadConfig {
    public static final int DISK_CACHE_DEFAULT = 0;//默认磁盘缓存
    public static final int DISK_CACHE_NONE = 1;//无磁盘缓存
    public static final int DISK_CACHE_ALL = 2;//缓存原始图片及变换后的图片
    public static final int DISK_CACHE_SOURCE = 3;//只缓存原始图片
    public static final int DISK_CACHE_RESULT = 4;//只缓存变换后的图片

    @IntDef({DISK_CACHE_DEFAULT, DISK_CACHE_NONE, DISK_CACHE_ALL, DISK_CACHE_SOURCE, DISK_CACHE_RESULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DiskCache {
    }

    public Context context;
    public String url;
    public File file;
    public int resId = Integer.MIN_VALUE;
    public Uri uri;
    public int placeholderResId = Integer.MIN_VALUE;
    public Drawable placeholderDrawable;
    public int errorResId = Integer.MIN_VALUE;
    public Drawable errorDrawable;
    public int diskCache = DISK_CACHE_DEFAULT;//磁盘缓存策略，glide支持所有，fresco只支持有和没有两种
    public ImageView.ScaleType scaleType;//除了对图片做变换，同时也会修改ImageView的ScaleType
    public boolean skipMemory;//跳过内存缓存,fresco无效
    public boolean asBitmap;//为true的话gif不会自动播放
    public int width, height;//期望获得的图片宽高
    public boolean isCircle;//圆形图片，支持placeholder和error，最好搭配centerCrop使用，否则fresco会用镜像显示小图片
    public int roundCornerRadius;//圆角图片，支持placeholder和error，最好搭配centerCrop使用，否则fresco会用镜像显示小图片
    public float blurSampleSize;//高斯模糊时先将原图缩小多少倍
    public int blurRadius;//高斯模糊采样半径
    public int fadeDuration;//透明渐变动画时长，0为关闭动画
    public List<BitmapTransformation> transformationList;
    public LoadListener loadListener;
    public ImageView targetView;

    public LoadConfig with(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public LoadConfig load(String url) {
        this.url = url;
        return this;
    }

    public LoadConfig load(Uri uri) {
        this.uri = uri;
        return this;
    }

    public LoadConfig load(File file) {
        this.file = file;
        return this;
    }

    public LoadConfig load(int resId) {
        this.resId = resId;
        return this;
    }

    public LoadConfig placeholder(int placeholderResId) {
        this.placeholderResId = placeholderResId;
        return this;
    }

    public LoadConfig placeholder(Drawable placeholderDrawable) {
        this.placeholderDrawable = placeholderDrawable;
        return this;
    }

    public LoadConfig error(int errorResId) {
        this.errorResId = errorResId;
        return this;
    }

    public LoadConfig error(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        return this;
    }

    public LoadConfig diskCache(@DiskCache int diskCache) {
        this.diskCache = diskCache;
        return this;
    }

    public LoadConfig scaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    public LoadConfig skipMemory(boolean skipMemory) {
        this.skipMemory = skipMemory;
        return this;
    }

    public LoadConfig asBitmap(boolean asBitmap) {
        this.asBitmap = asBitmap;
        return this;
    }

    public LoadConfig override(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public LoadConfig isCircle(boolean isCircle) {
        this.isCircle = isCircle;
        return this;
    }

    public LoadConfig roundCornerRadius(int roundCornerRadius) {
        this.roundCornerRadius = roundCornerRadius;
        return this;
    }

    public LoadConfig blur(float blurSampleSize, int blurRadius) {
        this.blurSampleSize = blurSampleSize;
        this.blurRadius = blurRadius;
        return this;
    }

    public LoadConfig fadeDuration(int fadeDuration) {
        this.fadeDuration = fadeDuration;
        return this;
    }

    public LoadConfig addTransform(BitmapTransformation bitmapTransformation) {
        if (transformationList == null) transformationList = new ArrayList<>(1);
        transformationList.add(bitmapTransformation);
        return this;
    }

    public LoadConfig listener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    public void into(ImageView targetView) {
        this.targetView = targetView;
        ImageLoader.getLoader().load(this);
    }
}
