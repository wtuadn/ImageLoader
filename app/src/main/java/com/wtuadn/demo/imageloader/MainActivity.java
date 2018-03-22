package com.wtuadn.demo.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.base.LoadConfig;
import com.wtuadn.imageloader.base.LoadListener;
import com.wtuadn.imageloader.base.transforms.CircleTransformation;
import com.wtuadn.imageloader.frescoloader.FrescoLoader;
import com.wtuadn.imageloader.glideloader.GlideLoader;

public class MainActivity extends Activity {
            private String url = "http://4k.znds.com/20140314/4kznds3.jpg";
//    private String url = "http://img.soogif.com/8Xu7aeBRwgq4Cokz6cai6bdTjLSAGc3A.gif_s400x0";
//    private String url = "http://storage.slide.news.sina.com.cn/slidenews/77_ori/2018_10/74766_815135_885755.gif";
    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);

        ImageLoader.init(this, new GlideLoader());
        test((ImageView) container.findViewById(R.id.img1));
        ImageLoader.init(this, new FrescoLoader());
        test((ImageView) container.findViewById(R.id.img2));
//        container.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final ImageView img1 = container.findViewById(R.id.img1);
//                container.removeView(img1);
//                container.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        container.addView(img1, 0);
//                        ImageLoader.with(getApplicationContext())
//                                .load("http://img.soogif.com/8Xu7aeBRwgq4Cokz6cai6bdTjLSAGc3A.gif_s400x0")
//                                .into(img1);
//                    }
//                }, 2000);
//            }
//        }, 2000);
    }

    void test(ImageView img1){
        ImageLoader.with(this)
                .load(url)
//                .load(R.mipmap.a)
                .diskCache(LoadConfig.DISK_CACHE_NONE)
                .skipMemory(true)
                .asBitmap(true)
                .scaleType(ImageView.ScaleType.CENTER_CROP)
                .placeholder(R.mipmap.a)
                .error(R.mipmap.ic_launcher)
//                .round(20)
                .fadeDuration(500)
//                .circle(true)
                .blur(2, 15)
                .addTransform(new CircleTransformation(ImageView.ScaleType.CENTER_CROP))
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
//        img1.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        Glide.with(this)
////                .asBitmap()
//                .load(url)
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true).placeholder(R.mipmap.a)
////                        .override(Target.SIZE_ORIGINAL)
//                        .transform(new RoundedCorners(20))
//                        .downsample(DownsampleStrategy.CENTER_OUTSIDE)
////                        .centerInside()
//                )
//                .transition(DrawableTransitionOptions.withCrossFade(new DrawableCrossFadeFactory.Builder(800).setCrossFadeEnabled(true).build()))
//                .into(img1);
    }

    void bb() {
//        try{
//            final Class<?> activityThreadClass =
//                    Class.forName("android.app.ActivityThread");
//            final Method method = activityThreadClass.getMethod("currentApplication");
//            Context context = (Context) method.invoke(null, (Object[]) null);
//            FileOutputStream stream = new FileOutputStream(new File(context.getExternalCacheDir()+"/1.jpg"));
//            result.compress(Bitmap.CompressFormat.JPEG, 80, stream);
//            stream.flush();
//            stream.close();
//        }catch (Exception e){
//
//        }
    }
}
