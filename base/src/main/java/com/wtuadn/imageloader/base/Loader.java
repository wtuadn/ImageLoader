package com.wtuadn.imageloader.base;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by wtuadn on 2018/2/10.
 */

public interface Loader {
    void resume(@NonNull Context context);

    void pause(@NonNull Context context);

    void clearDiskCache();

    void clearMemoryCache();

    void onTrimMemory(int level);

    void onLowMemory();

    void load(@NonNull LoadConfig loadConfig);
}
