package com.wtuadn.imageloader.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

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
    // TODO: 2018/3/9 添加统一对bitmap变换的接口
}
