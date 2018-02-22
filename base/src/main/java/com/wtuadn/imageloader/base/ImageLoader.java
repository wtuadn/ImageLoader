package com.wtuadn.imageloader.base;

import android.content.Context;

/**
 * Created by wtuadn on 2018/2/10.
 */

public class ImageLoader{
    private static Loader loader;

    public static Loader getLoader() {
        if (loader == null) {
            throw new RuntimeException("Must call ImageLoader.init() first!");
        }
        return loader;
    }

    public static void init(Loader loader) {
        ImageLoader.loader = loader;
    }

    public static LoadOption with(Context context){
        return new LoadOption();
    }

    public static void resume(){
        getLoader().resume();
    }

    public static void pause(){
        getLoader().pause();
    }

    public static void clearDiskCache() {
        getLoader().clearDiskCache();
    }

    public static void clearMemoryCache() {
        getLoader().clearMemoryCache();
    }

    public static void trimMemory(int level) {
        getLoader().trimMemory(level);
    }

    public static void onLowMemory() {
        getLoader().onLowMemory();
    }
}
