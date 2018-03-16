package com.wtuadn.imageloader.base.transforms;

import android.graphics.Bitmap;

/**
 * 用于对bitmap作变换
 * Created by wtuadn on 2018/3/13.
 */
public abstract class BitmapTransformation {
    private ReuseBitmapListener reuseBitmapListener;

    /**
     * @param toTransform 待变换的bitmap
     * @param outWidth    期望的宽
     * @param outHeight   期望的高
     * @return 变换好的bitmap
     */
    public abstract Bitmap transform(Bitmap toTransform, int outWidth, int outHeight);

    /**
     * @return 用此字段来缓存修改后的图片，默认null就是不缓存
     */
    public abstract String getCacheKey();

    public void setReuseBitmapListener(ReuseBitmapListener reuseBitmapListener) {
        this.reuseBitmapListener = reuseBitmapListener;
    }

    public ReuseBitmapListener getReuseBitmapListener() {
        return reuseBitmapListener;
    }

    public Bitmap getReuseableBitmap(int width, int height, Bitmap.Config config) {
        if (reuseBitmapListener == null) return null;
        return reuseBitmapListener.getReuseableBitmap(width, height, config);
    }

    public interface ReuseBitmapListener {
        Bitmap getReuseableBitmap(int width, int height, Bitmap.Config config);
    }
}
