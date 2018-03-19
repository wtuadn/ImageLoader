package com.wtuadn.imageloader.base.transforms;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.widget.ImageView;

/**
 * Created by wtuadn on 2018/3/16.
 */

public class RoundTransformation extends BitmapTransformation {
    private ImageView.ScaleType scaleType;
    private float radius;
    private float[] radii;

    public RoundTransformation(ImageView.ScaleType scaleType, float radius) {
        this.radius = radius;
        if (scaleType != null) {
            this.scaleType = scaleType;
        } else {
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
        }
    }

    public RoundTransformation(ImageView.ScaleType scaleType, float[] radii) {
        this.radii = radii;
        if (scaleType != null) {
            this.scaleType = scaleType;
        } else {
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
        }
    }

    @Override
    public Bitmap transform(Bitmap toTransform, int outWidth, int outHeight) {
        if (radius <= 0 && (radii == null || radii.length != 8)) return toTransform;

        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        int[] resultWH = ScaleUtils.getResultWH(scaleType, width, height, outWidth, outHeight);
        int resultWidth = resultWH[0];
        int resultHeight = resultWH[1];

        Matrix matrix = ScaleUtils.getMatrix(scaleType, width, height, outWidth, outHeight);

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
        if (radius > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, resultWidth, resultHeight, radius, radius, paint);
            } else {
                canvas.drawRoundRect(new RectF(0, 0, resultWidth, resultHeight), radius, radius, paint);
            }
        } else if (radii != null && radii.length == 8) {
            Path path = new Path();
            //left-top corner
            path.moveTo(0f, radii[1]);
            path.quadTo(0f, 0f, radii[0], 0f);
            //right-top corner
            path.lineTo(resultWidth - radii[2], 0f);
            path.quadTo(resultWidth, 0f, resultWidth, radii[3]);
            //left-bottom corner
            path.lineTo(resultWidth, resultHeight - radii[5]);
            path.quadTo(resultWidth, resultHeight, resultWidth - radii[4], resultHeight);
            //right-bottom corner
            path.lineTo(radii[6], resultHeight);
            path.quadTo(0f, resultHeight, 0f, resultHeight - radii[7]);
            path.close();
            canvas.drawPath(path, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(toTransform, matrix, paint);

        canvas.setBitmap(null);
        return result;
    }

    @Override
    public String getCacheKey() {
        StringBuilder builder = new StringBuilder("RoundTransformation ");
        builder.append(ScaleUtils.getWrapedScaleType(scaleType)).append(" ");
        builder.append(radius).append(" ");
        if (radii != null) {
            for (int i = 0; i < radii.length; ++i) {
                builder.append(radii[i]).append(" ");
            }
        }
        return builder.toString();
    }
}
