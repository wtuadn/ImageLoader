package com.wtuadn.imageloader.base.transforms;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.widget.ImageView;

/**
 * Created by wtuadn on 2018/3/16.
 */

public class CircleTransformation extends BitmapTransformation {
    private ImageView.ScaleType scaleType;

    public CircleTransformation(ImageView.ScaleType scaleType) {
        if (scaleType != null) {
            this.scaleType = scaleType;
        } else {
            this.scaleType = ImageView.ScaleType.FIT_CENTER;
        }
    }

    @Override
    public Bitmap transform(Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int minEdge = Math.min(width, height);

        int[] resultWH = ScaleUtils.getResultWH(scaleType, minEdge, minEdge, outWidth, outHeight);
        int resultWidth = resultWH[0];
        int resultHeight = resultWH[1];

        Matrix matrix = ScaleUtils.getMatrix(scaleType, minEdge, minEdge, outWidth, outHeight);
        float scale = ScaleUtils.getValue(matrix, Matrix.MSCALE_X);
        float radius = minEdge / 2f * scale;
        float cx = resultWidth / 2f;
        float cy = resultHeight / 2f;

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap result = getReuseableBitmap(resultWidth, resultHeight, config);
        if (result == null || result.getWidth() != resultWidth || result.getHeight() != resultHeight) {
            result = Bitmap.createBitmap(resultWidth, resultHeight, config);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        canvas.drawCircle(cx, cy, radius, paint);

        canvas.setBitmap(null);
        return result;
    }

    @Override
    public String getCacheKey() {
        return "CircleTransformation " + ScaleUtils.getWrapedScaleType(scaleType);
    }
}
