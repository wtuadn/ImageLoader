package com.wtuadn.demo.imageloader;

import android.app.Application;

import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.glideloader.GlideLoader;

/**
 * Created by wtuadn on 2018/3/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this, new GlideLoader());
    }
}
