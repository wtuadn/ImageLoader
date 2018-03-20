package com.wtuadn.demo.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.base.LoadConfig;
import com.wtuadn.imageloader.base.LoadListener;

public class MainActivity extends Activity {
//            private String url = "http://4k.znds.com/20140314/4kznds3.jpg";
    private String url = "http://img.soogif.com/8Xu7aeBRwgq4Cokz6cai6bdTjLSAGc3A.gif_s400x0";
    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);

        final ImageView img1 = container.findViewById(R.id.img1);

        ImageLoader.with(this)
                .load(url)
                .diskCache(LoadConfig.DISK_CACHE_NONE)
                .skipMemory(true)
//                .asBitmap(true)
                .scaleType(ImageView.ScaleType.CENTER_CROP)
                .placeholder(R.mipmap.a)
                .error(R.mipmap.ic_launcher)
//                .round(20)
                .fadeDuration(800)
                .circle(true)
//                .blur(1, 150)
//                .override(-1, -1)
                .listener(new LoadListener(false) {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
//                        img1.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFail() {
                    }
                })
                .into(img1);
//        img1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        Glide.with(this)
//                .asBitmap()
//                .load(url)
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true).placeholder(R.mipmap.a)
////                        .override(Target.SIZE_ORIGINAL)
//                        .transform(new RoundedCorners(20))
//                        .downsample(DownsampleStrategy.FIT_CENTER)
////                        .centerInside()
//                )
////                .transition(BitmapTransitionOptions.withCrossFade(new DrawableCrossFadeFactory.Builder(800).setCrossFadeEnabled(true).build()))
//                .into(img1);
    }

    void test1(Bitmap bitmap) {
        int minEdge = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap result = Bitmap.createBitmap(minEdge, minEdge, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);

        float radius = minEdge / 2f;
        float cx = minEdge / 2f;
        float cy = minEdge / 2f;

        canvas.drawCircle(cx, cy, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.setBitmap(null);
//        ((ImageView) findViewById(R.id.img2)).setImageBitmap(result);
    }

    void test2(Bitmap bitmap) {
        int minEdge = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap result = Bitmap.createBitmap(minEdge, minEdge, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);

        float radius = minEdge / 2f;
        float cx = minEdge / 2f;
        float cy = minEdge / 2f;

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.setBitmap(null);
//        ((ImageView) findViewById(R.id.img3)).setImageBitmap(result);
    }
}
