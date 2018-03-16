package com.wtuadn.imageloader.base;

import android.graphics.Bitmap;

/**
 * Created by wtuadn on 2018/3/14.
 */

public abstract class LoadListener {
    public final boolean returnBitmap;

    /**
     * @param returnBitmap 是否返回bitmap
     */
    public LoadListener(boolean returnBitmap) {
        this.returnBitmap = returnBitmap;
    }

    public abstract void onSuccess(Bitmap bitmap);

    public abstract void onFail() ;
}
