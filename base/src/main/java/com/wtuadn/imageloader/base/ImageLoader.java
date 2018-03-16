package com.wtuadn.imageloader.base;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by wtuadn on 2018/2/10.
 */

public class ImageLoader {
    private static Context context;
    private static Loader loader;

    public static Loader getLoader() {
        if (loader == null) {
            throw new RuntimeException("Must call ImageLoader.init() first!");
        }
        return loader;
    }

    public static Context getContext() {
        if (context == null) {
            throw new RuntimeException("Must call ImageLoader.init() first!");
        }
        return context;
    }

    public static void init(@NonNull Context context, @NonNull Loader loader) {
        ImageLoader.loader = loader;
        ImageLoader.context = context.getApplicationContext();
    }

    public static LoadConfig with(@NonNull Context context) {
        return new LoadConfig().with(context);
    }

    public static void resume(Context context) {
        getLoader().resume(context);
    }

    public static void pause(Context context) {
        getLoader().pause(context);
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
