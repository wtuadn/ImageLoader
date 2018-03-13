package com.wtuadn.imageloader.base;

import android.graphics.Bitmap;

/**
 * 用于对bitmap作变换
 * Created by wtuadn on 2018/3/13.
 */
public interface BitmapTransformation {
    /**
     *
     * @param toTransform 待变换的bitmap
     * @param outWidth 期望的宽
     * @param outHeight 期望的高
     * @return 变换好的bitmap
     */
    Bitmap transform(Bitmap toTransform, int outWidth, int outHeight);

    /**
     * @return fresco会用此字段来缓存修改后的图片，默认null就是不缓存
     */
    String getCacheKey();
}
