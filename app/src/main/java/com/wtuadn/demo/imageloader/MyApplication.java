package com.wtuadn.demo.imageloader;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.wtuadn.imageloader.base.ImageLoader;

/**
 * Created by wtuadn on 2018/3/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        ImageLoader.init(this, new GlideLoader());
//        ImageLoader.init(this, new FrescoLoader());

        Fresco.initialize(this);//本库只处理图片加载，初始化、配置缓存等请另外配置
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ImageLoader.onTrimMemory(level);
    }
}
