package com.wtuadn.imageloader.glideloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.base.LoadConfig;
import com.wtuadn.imageloader.base.Loader;
import com.wtuadn.imageloader.base.transforms.BitmapTransformation;
import com.wtuadn.imageloader.base.transforms.BlurTransformation;
import com.wtuadn.imageloader.base.transforms.CircleTransformation;
import com.wtuadn.imageloader.base.transforms.RoundTransformation;
import com.wtuadn.imageloader.base.transforms.ScaleUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
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
    public void onTrimMemory(int level) {
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
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.file);
            else requestBuilder = requestBuilder.load(loadConfig.file);
        } else if (loadConfig.resId != Integer.MIN_VALUE) {
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.resId);
            else requestBuilder = requestBuilder.load(loadConfig.resId);
        } else {
            if (requestBuilder == null) requestBuilder = requestManager.load(loadConfig.uri);
            else requestBuilder = requestBuilder.load(loadConfig.uri);
        }
        TransitionOptions transitionOptions = null;
        if (loadConfig.fadeDuration > 0) {
            DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder(loadConfig.fadeDuration).setCrossFadeEnabled(true).build();
            transitionOptions = isBitmap ? BitmapTransitionOptions.withCrossFade(factory) : DrawableTransitionOptions.withCrossFade(factory);
            requestBuilder = requestBuilder.transition(transitionOptions);
        }

        RequestOptions requestOptions = RequestOptions.skipMemoryCacheOf(loadConfig.skipMemory);
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

        if (loadConfig.scaleType != null) {
            if (loadConfig.targetView != null) loadConfig.targetView.setScaleType(loadConfig.scaleType);
            switch (ScaleUtils.getWrappedScaleType(loadConfig.scaleType)) {
                case ScaleUtils.FIT_CENTER:
                    requestOptions = requestOptions.downsample(DownsampleStrategy.FIT_CENTER);
                    break;
                default:
                    requestOptions = requestOptions.downsample(DownsampleStrategy.CENTER_OUTSIDE);
            }
        }

        List<BitmapTransformation> list = new ArrayList<>(2);
        if (loadConfig.blurRadius > 0) {
            list.add(new BlurTransformation(loadConfig.blurSampleSize, loadConfig.blurRadius));
        }
        if (loadConfig.isCircle) {
            CircleTransformation transformation = new CircleTransformation(loadConfig.scaleType);
            TransformationWrapper wrapper = new TransformationWrapper(transformation);
            requestBuilder = placeholderWithTransform(loadConfig, isBitmap, requestBuilder, wrapper);
            requestBuilder = errorWithTransform(loadConfig, isBitmap, transitionOptions, requestBuilder, wrapper);
            list.add(transformation);
        } else if (loadConfig.roundCornerRadius > 0) {
            RoundTransformation transformation = new RoundTransformation(loadConfig.scaleType, loadConfig.roundCornerRadius);
            TransformationWrapper wrapper = new TransformationWrapper(transformation);
            requestBuilder = placeholderWithTransform(loadConfig, isBitmap, requestBuilder, wrapper);
            requestBuilder = errorWithTransform(loadConfig, isBitmap, transitionOptions, requestBuilder, wrapper);
            list.add(transformation);
        } else if (loadConfig.roundCornerRadii != null && loadConfig.roundCornerRadii.length == 8) {
            RoundTransformation transformation = new RoundTransformation(loadConfig.scaleType, loadConfig.roundCornerRadii);
            TransformationWrapper wrapper = new TransformationWrapper(transformation);
            requestBuilder = placeholderWithTransform(loadConfig, isBitmap, requestBuilder, wrapper);
            requestBuilder = errorWithTransform(loadConfig, isBitmap, transitionOptions, requestBuilder, wrapper);
            list.add(transformation);
        } else {
            requestOptions = requestOptions.placeholder(loadConfig.placeholderResId)
                    .placeholder(loadConfig.placeholderDrawable)
                    .error(loadConfig.errorResId)
                    .error(loadConfig.errorDrawable);
        }
        if (loadConfig.transformationList != null) {
            list.addAll(loadConfig.transformationList);
        }
        if (list.size() > 0) requestOptions = requestOptions.transform(new MultiTransformation(list));
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
                    if (loadConfig.loadListener.returnBitmap && resource instanceof Bitmap) {
                        loadConfig.loadListener.onSuccess((Bitmap) resource);
                    } else {
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

    @NonNull
    private RequestBuilder placeholderWithTransform(@NonNull LoadConfig loadConfig, boolean isBitmap, RequestBuilder requestBuilder, com.bumptech.glide.load.resource.bitmap.BitmapTransformation bitmapTransformation) {
        RequestManager requestManager = Glide.with(loadConfig.context);
        if (loadConfig.placeholderResId != Integer.MIN_VALUE) {
            RequestBuilder thumbnailRequest;
            if (isBitmap) {
                thumbnailRequest = requestManager.asBitmap().load(loadConfig.placeholderResId);
            } else {
                thumbnailRequest = requestManager.load(loadConfig.placeholderResId);
            }
            requestBuilder = requestBuilder.thumbnail(
                    thumbnailRequest.apply(RequestOptions.bitmapTransform(bitmapTransformation)));
        } else if (loadConfig.placeholderDrawable != null) {
            RequestBuilder thumbnailRequest;
            if (isBitmap) {
                thumbnailRequest = requestManager.asBitmap().load(loadConfig.placeholderDrawable);
            } else {
                thumbnailRequest = requestManager.load(loadConfig.placeholderDrawable);
            }
            requestBuilder = requestBuilder.thumbnail(
                    thumbnailRequest.apply(RequestOptions.bitmapTransform(bitmapTransformation)));
        }
        return requestBuilder;
    }

    @NonNull
    private RequestBuilder errorWithTransform(@NonNull LoadConfig loadConfig, boolean isBitmap, TransitionOptions transitionOptions, RequestBuilder requestBuilder, com.bumptech.glide.load.resource.bitmap.BitmapTransformation bitmapTransformation) {
        RequestManager requestManager = Glide.with(loadConfig.context);
        if (loadConfig.errorResId != Integer.MIN_VALUE) {
            RequestBuilder errorRequest;
            if (isBitmap) {
                errorRequest = requestManager.asBitmap().load(loadConfig.errorResId);
            } else {
                errorRequest = requestManager.load(loadConfig.errorResId);
            }
            if (transitionOptions != null) errorRequest = errorRequest.transition(transitionOptions);
            requestBuilder = requestBuilder.error(
                    errorRequest.apply(RequestOptions.bitmapTransform(bitmapTransformation)));
        } else if (loadConfig.errorDrawable != null) {
            RequestBuilder errorRequest;
            if (isBitmap) {
                errorRequest = requestManager.asBitmap().load(loadConfig.errorDrawable);
            } else {
                errorRequest = requestManager.load(loadConfig.errorDrawable);
            }
            if (transitionOptions != null) errorRequest = errorRequest.transition(transitionOptions);
            requestBuilder = requestBuilder.error(
                    errorRequest.apply(RequestOptions.bitmapTransform(bitmapTransformation)));
        }
        return requestBuilder;
    }

    private static class MultiTransformation extends com.bumptech.glide.load.resource.bitmap.BitmapTransformation implements BitmapTransformation.ReuseBitmapListener {
        private final Collection<BitmapTransformation> transformations;
        private BitmapPool bitmapPool;

        private MultiTransformation(Collection<BitmapTransformation> transformations) {
            this.transformations = transformations;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            this.bitmapPool = pool;
            for (BitmapTransformation t : transformations) {
                if (t.getReuseBitmapListener() == null) {
                    t.setReuseBitmapListener(this);
                }
                toTransform = t.transform(toTransform, outWidth, outHeight);
            }
            return toTransform;
        }

        @Override
        public Bitmap getReuseableBitmap(int width, int height, Bitmap.Config config) {
            if (bitmapPool == null) return null;
            return bitmapPool.get(width, height, config);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MultiTransformation) {
                return transformations.equals(((MultiTransformation) obj).transformations);
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return transformations.hashCode();
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            for (BitmapTransformation t : transformations) {
                if (t.getCacheKey() != null) {
                    messageDigest.update(t.getCacheKey().getBytes());
                }
            }
        }
    }

    private static class TransformationWrapper extends com.bumptech.glide.load.resource.bitmap.BitmapTransformation implements BitmapTransformation.ReuseBitmapListener {
        private BitmapTransformation transformation;
        private BitmapPool bitmapPool;

        private TransformationWrapper(@NonNull BitmapTransformation transformation) {
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