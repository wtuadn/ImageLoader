package com.wtuadn.imageloader.base.transforms;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by wtuadn on 2018/3/19.
 */

public class ScaleUtils {
    public static final int MATRIX = 0;
    public static final int CENTER_CROP = 1;
    public static final int CENTER_INSIDE = 2;
    public static final int FIT_CENTER = 3;

    public static int getWrappedScaleType(ImageView.ScaleType scaleType) {
        switch (scaleType) {
            case CENTER_CROP:
                return CENTER_CROP;
            case CENTER_INSIDE:
            case FIT_XY:
                return CENTER_INSIDE;
            case FIT_CENTER:
            case FIT_START:
            case FIT_END:
                return FIT_CENTER;
        }
        return MATRIX;
    }

    public static float getValue(Matrix matrix, int index) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[index];
    }

    @NonNull
    public static int[] getResultWH(ImageView.ScaleType scaleType, int bitmapWidth, int bitmapHeight, int outWidth, int outHeight) {
        switch (getWrappedScaleType(scaleType)) {
            case CENTER_CROP:
                return new int[]{outWidth, outHeight};
            case CENTER_INSIDE:
                if (bitmapWidth > outWidth || bitmapHeight > outHeight) {
                    float maxScale = Math.max(((float) bitmapWidth / outWidth), ((float) bitmapHeight / outHeight));
                    return new int[]{(int) (bitmapWidth / maxScale), (int) (bitmapHeight / maxScale)};
                }
                break;
            case FIT_CENTER:
                float maxScale = Math.max(((float) bitmapWidth / outWidth), ((float) bitmapHeight / outHeight));
                return new int[]{(int) (bitmapWidth / maxScale), (int) (bitmapHeight / maxScale)};
        }
        return new int[]{bitmapWidth, bitmapHeight};
    }

    @NonNull
    public static Matrix getMatrix(ImageView.ScaleType scaleType, int bitmapWidth, int bitmapHeight, int outWidth, int outHeight) {
        switch (getWrappedScaleType(scaleType)) {
            case CENTER_CROP:
                return getCenterCropMatrix(bitmapWidth, bitmapHeight, outWidth, outHeight);
            case CENTER_INSIDE:
                return getCenterInsideMatrix(bitmapWidth, bitmapHeight, outWidth, outHeight);
            case FIT_CENTER:
                return getFitCenterMatrix(bitmapWidth, bitmapHeight, outWidth, outHeight);
        }
        return new Matrix();
    }

    private static Matrix getCenterCropMatrix(int bitmapWidth, int bitmapHeight, int outWidth, int outHeight) {
        // From ImageView/Bitmap.createScaledBitmap.
        final float scale;
        final float dx;
        final float dy;
        Matrix m = new Matrix();
        if (bitmapWidth * outHeight > outWidth * bitmapHeight) {
            scale = (float) outHeight / (float) bitmapHeight;
            dx = (outWidth - bitmapWidth * scale) * 0.5f;
            dy = 0;
        } else {
            scale = (float) outWidth / (float) bitmapWidth;
            dx = 0;
            dy = (outHeight - bitmapHeight * scale) * 0.5f;
        }

        m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        return m;
    }

    private static Matrix getCenterInsideMatrix(int bitmapWidth, int bitmapHeight, int outWidth, int outHeight) {
        Matrix m = new Matrix();
        if (bitmapWidth > outWidth || bitmapHeight > outHeight) {
            float maxScale = Math.max(((float) bitmapWidth / outWidth), ((float) bitmapHeight / outHeight));
            m.setScale(1 / maxScale, 1 / maxScale);
        }
        return m;
    }

    private static Matrix getFitCenterMatrix(int bitmapWidth, int bitmapHeight, int outWidth, int outHeight) {
        Matrix m = new Matrix();
        float maxScale = Math.max(((float) bitmapWidth / outWidth), ((float) bitmapHeight / outHeight));
        m.setScale(1 / maxScale, 1 / maxScale);
        return m;
    }
}
