package com.wtuadn.imageloader.glideloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.base.LoadConfig;
import com.wtuadn.imageloader.base.Loader;
import com.wtuadn.imageloader.base.transforms.BitmapTransformation;
import com.wtuadn.imageloader.base.transforms.BlurTransformation;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtuadn on 2018/2/10.
 */

public class GlideLoader implements Loader {

    @Override
    public void resume(@NonNull Context context) {
        Glide.with(context).resumeRequestsRecursive();
    }

    @Override
    public void pause(@NonNull Context context) {
        Glide.with(context).pauseRequestsRecursive();
    }

    @Override
    public void clearDiskCache() {
        Glide.get(ImageLoader.getContext()).clearDiskCache();
    }

    @Override
    public void clearMemoryCache() {
        Glide.get(ImageLoader.getContext()).clearMemory();
    }

    @Override
    public void trimMemory(int level) {
        Glide.get(ImageLoader.getContext()).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        Glide.get(ImageLoader.getContext()).onLowMemory();
    }

    @Override
    public void load(@NonNull final LoadConfig loadConfig) {
        if (loadConfig.targetView == null && loadConfig.loadListener == null) return;
        RequestManager requestManager = Glide.with(loadConfig.context);
        RequestBuilder requestBuilder = null;
        boolean isBitmap = false;
        if (loadConfig.asBitmap || (loadConfig.loadListener != null && loadConfig.loadListener.returnBitmap)) {
            isBitmap = true;
            requestBuilder = requestManager.asBitmap();
        }
        if (!TextUtils.isEmpty(loadConfig.url)) {
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.url);
            else requestBuilder = requestBuilder.load(loadConfig.url);
        } else if (loadConfig.file != null) {
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.url);
            else requestBuilder = requestBuilder.load(loadConfig.file);
        } else if (loadConfig.resId != Integer.MIN_VALUE) {
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.resId);
            else requestBuilder = requestBuilder.load(loadConfig.resId);
        } else {
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.uri);
            else requestBuilder = requestBuilder.load(loadConfig.uri);
        }
        if (loadConfig.fadeDuration > 0) {
            requestBuilder = requestBuilder.transition(isBitmap ? BitmapTransitionOptions.withCrossFade(loadConfig.fadeDuration)
                    : DrawableTransitionOptions.withCrossFade(loadConfig.fadeDuration));
        }

        RequestOptions requestOptions = RequestOptions.skipMemoryCacheOf(loadConfig.skipMemory);
        if (loadConfig.placeholderResId != Integer.MIN_VALUE) {
            requestOptions = requestOptions.placeholder(loadConfig.placeholderResId);
        } else if (loadConfig.placeholderDrawable != null) {
            requestOptions = requestOptions.placeholder(loadConfig.placeholderDrawable);
        }
        if (loadConfig.errorResId != Integer.MIN_VALUE) {
            requestOptions = requestOptions.error(loadConfig.errorResId);
        } else if (loadConfig.errorDrawable != null) {
            requestOptions = requestOptions.error(loadConfig.errorDrawable);
        }
        if (loadConfig.diskCache != LoadConfig.DISK_CACHE_DEFAULT) {
            switch (loadConfig.diskCache) {
                case LoadConfig.DISK_CACHE_NONE:
                    requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                    break;
                case LoadConfig.DISK_CACHE_ALL:
                    requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                    break;
                case LoadConfig.DISK_CACHE_RESULT:
                    requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    break;
                case LoadConfig.DISK_CACHE_SOURCE:
                    requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
                    break;
            }
        }
        if (loadConfig.width > 0 && loadConfig.height > 0) {
            requestOptions = requestOptions.override(loadConfig.width, loadConfig.height);
        } else if (loadConfig.width < 0 || loadConfig.height < 0 || loadConfig.targetView == null) {
            requestOptions = requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        List<Transformation> list = new ArrayList<>(3);
        if (loadConfig.blurRadius > 0) {
            list.add(new TransformationWrapper(new BlurTransformation(loadConfig.blurSampleSize, loadConfig.blurRadius)));
        }
        if (loadConfig.scaleType != null) {// TODO: 2018/3/16 根据scaleType优化圆形圆角操作，合并transformation
            if (loadConfig.targetView != null) loadConfig.targetView.setScaleType(loadConfig.scaleType);
            switch (loadConfig.scaleType) {
                case CENTER_CROP:
                    list.add(new CenterCrop());
                    break;
                case CENTER_INSIDE:
                case FIT_XY:
                    list.add(new CenterInside());
                    break;
                case FIT_CENTER:
                case FIT_START:
                case FIT_END:
                    list.add(new FitCenter());
                    break;
                case CENTER:
                case MATRIX:
                default:
            }
        }
        if (loadConfig.isCircle) {
            list.add(new CircleCrop());
        } else if (loadConfig.roundCornerRadius > 0) {
            list.add(new RoundedCorners(loadConfig.roundCornerRadius));
        }
        if (loadConfig.transformationList != null) {
            for (int i = 0; i < loadConfig.transformationList.size(); ++i) {
                list.add(new TransformationWrapper(loadConfig.transformationList.get(i)));
            }
        }
        if (list.size() > 0) requestOptions = requestOptions.transforms(list.toArray(new Transformation[list.size()]));
        if (loadConfig.format != null) {
            if (loadConfig.format == Bitmap.Config.RGB_565) {
                requestOptions = requestOptions.format(DecodeFormat.PREFER_RGB_565);
            } else {
                requestOptions = requestOptions.format(DecodeFormat.PREFER_ARGB_8888);
            }
        }
        requestBuilder = requestBuilder.apply(requestOptions);

        if (loadConfig.loadListener != null) {
            requestBuilder = requestBuilder.listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    loadConfig.loadListener.onFail();
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    if (loadConfig.loadListener.returnBitmap && resource instanceof Bitmap)
                        loadConfig.loadListener.onSuccess((Bitmap) resource);
                    else {
                        loadConfig.loadListener.onSuccess(null);
                    }
                    return false;
                }
            });
        }
        if (loadConfig.targetView != null) {
            requestBuilder.into(loadConfig.targetView);
        } else {
            requestBuilder.into(new SimpleTarget() {
                @Override
                public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
                    //do nothing
                }
            });
        }
    }

    private static class TransformationWrapper extends com.bumptech.glide.load.resource.bitmap.BitmapTransformation implements BitmapTransformation.ReuseBitmapListener {
        private BitmapTransformation transformation;
        private BitmapPool bitmapPool;

        TransformationWrapper(@NonNull BitmapTransformation transformation) {
            this.transformation = transformation;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            this.bitmapPool = pool;
            if (transformation.getReuseBitmapListener() == null) {
                transformation.setReuseBitmapListener(this);
            }
            return transformation.transform(toTransform, outWidth, outHeight);
        }

        @Override
        public Bitmap getReuseableBitmap(int width, int height, Bitmap.Config config) {
            if (bitmapPool == null) return null;
            return bitmapPool.get(width, height, config);
        }

        @Override
        public int hashCode() {
            if (transformation.getCacheKey() != null) {
                return transformation.getCacheKey().hashCode();
            }
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof TransformationWrapper) {
                if (transformation.getCacheKey() != null) {
                    return transformation.getCacheKey().equals(((TransformationWrapper) obj).transformation.getCacheKey());
                }
            }
            return false;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            if (transformation.getCacheKey() != null) {
                messageDigest.update(transformation.getCacheKey().getBytes());
            }
        }
    }
}