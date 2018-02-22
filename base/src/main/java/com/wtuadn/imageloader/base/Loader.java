package com.wtuadn.imageloader.base;

import android.view.View;

/**
 * Created by wtuadn on 2018/2/10.
 */

public interface Loader {
    void load(View targetView, LoadOption loadOption);

    void resume();

    void pause();

    void clearDiskCache();

    void clearMemoryCache();

    void trimMemory(int level);

    void onLowMemory();
}
