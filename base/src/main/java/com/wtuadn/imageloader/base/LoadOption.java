package com.wtuadn.imageloader.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by wtuadn on 2018/2/10.
 */

public class LoadOption {
    public static int DISK_CACHE_DEFAULT = 0;
    public static int DISK_CACHE_NONE = 1;
    public static int DISK_CACHE_ALL = 2;
    public static int DISK_CACHE_SOURCE = 3;
    public static int DISK_CACHE_RESULT = 4;

    public Context context;
    public String url;
    public int placeholderResId = Integer.MIN_VALUE;
    public Drawable placeholderDrawable;
    public int errorResId = Integer.MIN_VALUE;
    public Drawable errorDrawable;
    public int diskCache = DISK_CACHE_DEFAULT;
    public ImageView.ScaleType scaleType;
    public boolean skipMemory;
    public boolean autoPlay = true;//如果是gif的话自动播放
    public boolean asBitmap;
    public List<BitmapTransformation> transformationList;
    public boolean isCircle;
    public int roundCornerRadius;
    public float blurSampleSize;//高斯模糊时先将原图缩小多少倍
    public int blurRadius;//高斯模糊采样半径
}
