package com.wtuadn.demo.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.base.LoadConfig;
import com.wtuadn.imageloader.base.LoadListener;

public class MainActivity extends Activity {
    private String url = "http://4k.znds.com/20140314/4kznds3.jpg";
//    private String url = "http://img.soogif.com/8Xu7aeBRwgq4Cokz6cai6bdTjLSAGc3A.gif_s400x0";
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
                .scaleType(ImageView.ScaleType.FIT_CENTER)
                .round(20)
                .circle(true)
//                .blur(1, 15)
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

    }
}
