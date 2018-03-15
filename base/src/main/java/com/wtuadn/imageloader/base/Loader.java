package com.wtuadn.imageloader.base;

/**
 * Created by wtuadn on 2018/2/10.
 */

public interface Loader {
    void resume();

    void pause();

    void clearDiskCache();

    void clearMemoryCache();

    void trimMemory(int level);

    void onLowMemory();

    void load(LoadConfig loadOption);
}
