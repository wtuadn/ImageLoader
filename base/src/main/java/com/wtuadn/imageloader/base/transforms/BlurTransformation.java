package com.wtuadn.imageloader.base.transforms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;

import com.wtuadn.imageloader.base.ImageLoader;

/**
 * Created by wtuadn on 2018/3/16.
 */

public class BlurTransformation extends BitmapTransformation {
    private float blurSampleSize;//高斯模糊时将原图缩小多少倍，可以节省内存，提高效率，不过会影响生成的图片大小，在使用CENTER_INSIDE之类的不会缩放小图的ScaleType时，请填1
    private int blurRadius;//高斯模糊采样半径

    public BlurTransformation(float blurSampleSize, int blurRadius) {
        this.blurSampleSize = blurSampleSize;
        this.blurRadius = blurRadius;
    }

    @Override
    public Bitmap transform(Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int scaledWidth = (int) (width / blurSampleSize);
        int scaledHeight = (int) (height / blurSampleSize);

        Bitmap.Config config = toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap toReuse = getReuseableBitmap(scaledWidth, scaledHeight, config);
        if (toReuse == null || toReuse.getWidth() != scaledWidth || toReuse.getHeight() != scaledHeight) {
            toReuse = Bitmap.createBitmap(scaledWidth, scaledHeight, config);
        }

        Canvas canvas = new Canvas(toReuse);
        canvas.scale(1 / blurSampleSize, 1 / blurSampleSize);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                try {
                    toReuse = RSBlur.blur(ImageLoader.getContext(), toReuse, blurRadius);
                } catch (RSRuntimeException e) {
                    toReuse = FastBlur.blur(toReuse, blurRadius, true);
                }
            } else {
                toReuse = FastBlur.blur(toReuse, blurRadius, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReuse;
    }

    @Override
    public String getCacheKey() {
        return "BlurTransformtion " + blurSampleSize + " " + blurRadius;
    }
}
