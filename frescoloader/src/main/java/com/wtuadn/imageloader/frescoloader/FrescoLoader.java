package com.wtuadn.imageloader.frescoloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wtuadn.imageloader.base.LoadConfig;
import com.wtuadn.imageloader.base.Loader;
import com.wtuadn.imageloader.base.transforms.BitmapTransformation;
import com.wtuadn.imageloader.base.transforms.BlurTransformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wtuadn on 2018/2/10.
 */

public class FrescoLoader implements Loader {

    @Override
    public void resume(@NonNull Context context) {
        Fresco.getImagePipeline().resume();
    }

    @Override
    public void pause(@NonNull Context context) {
        Fresco.getImagePipeline().pause();
    }

    @Override
    public void clearDiskCache() {
        Fresco.getImagePipeline().clearDiskCaches();
    }

    @Override
    public void clearMemoryCache() {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    @Override
    public void onTrimMemory(int level) {
        //do nothing
    }

    @Override
    public void onLowMemory() {
        clearMemoryCache();
    }

    @Override
    public void load(@NonNull final LoadConfig loadConfig) {
        if (loadConfig.targetView == null && loadConfig.loadListener == null) return;
        DraweeHolder draweeHolder = null;
        GenericDraweeHierarchy hierarchy = null;
        DraweeController draweeController = null;
        if (loadConfig.targetView != null) {
            draweeHolder = (DraweeHolder) loadConfig.targetView.getTag(R.id.drawee_tag);
        }
        if (draweeHolder == null) {
            hierarchy = GenericDraweeHierarchyBuilder.newInstance(loadConfig.context.getResources()).build();
            draweeHolder = DraweeHolder.create(hierarchy, loadConfig.context);
            if (loadConfig.targetView != null) {
                loadConfig.targetView.setTag(R.id.drawee_tag, draweeHolder);
                loadConfig.targetView.addOnAttachStateChangeListener(new AttachListener(draweeHolder));
            }
        } else {
            hierarchy = (GenericDraweeHierarchy) draweeHolder.getHierarchy();
            draweeController = draweeHolder.getController();
        }
        if (loadConfig.scaleType != null) {
            if (loadConfig.targetView != null) loadConfig.targetView.setScaleType(loadConfig.scaleType);
            ScalingUtils.ScaleType scaleType = ScalingUtils.ScaleType.CENTER_CROP;
            switch (loadConfig.scaleType) {
                case CENTER_INSIDE:
                    scaleType = ScalingUtils.ScaleType.CENTER_INSIDE;
                    break;
                case FIT_CENTER:
                    scaleType = ScalingUtils.ScaleType.FIT_CENTER;
                    break;
                case CENTER:
                    scaleType = ScalingUtils.ScaleType.CENTER;
                    break;
                case FIT_XY:
                    scaleType = ScalingUtils.ScaleType.FIT_XY;
                    break;
                case FIT_START:
                    scaleType = ScalingUtils.ScaleType.FIT_START;
                    break;
                case FIT_END:
                    scaleType = ScalingUtils.ScaleType.FIT_END;
            }
            hierarchy.setActualImageScaleType(scaleType);
            if (loadConfig.placeholderResId != Integer.MIN_VALUE) {
                hierarchy.setPlaceholderImage(loadConfig.placeholderResId, scaleType);
            } else if (loadConfig.placeholderDrawable != null) {
                hierarchy.setPlaceholderImage(loadConfig.placeholderDrawable, scaleType);
            }
            if (loadConfig.errorResId != Integer.MIN_VALUE) {
                hierarchy.setFailureImage(loadConfig.errorResId, scaleType);
            } else if (loadConfig.errorDrawable != null) {
                hierarchy.setFailureImage(loadConfig.errorDrawable, scaleType);
            }
        }
        if (loadConfig.isCircle) {
            hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        } else if (loadConfig.roundCornerRadius > 0) {
            hierarchy.setRoundingParams(new RoundingParams().setCornersRadius(loadConfig.roundCornerRadius));
        } else if (loadConfig.roundCornerRadii != null && loadConfig.roundCornerRadii.length == 8) {
            hierarchy.setRoundingParams(new RoundingParams().setCornersRadii(loadConfig.roundCornerRadii));
        }
        hierarchy.setFadeDuration(loadConfig.fadeDuration);

        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder();
        if (loadConfig.loadListener != null) {
            controllerBuilder.setControllerListener(new WControllerListener(draweeHolder, loadConfig));
        }
        Uri uri = loadConfig.uri;
        if (uri == null) {
            if (!TextUtils.isEmpty(loadConfig.url)) uri = Uri.parse(loadConfig.url);
            else if (loadConfig.file != null) uri = Uri.fromFile(loadConfig.file);
            else uri = UriUtil.getUriForResourceId(loadConfig.resId);
        }
        if (loadConfig.skipMemory) Fresco.getImagePipeline().evictFromMemoryCache(uri);
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (loadConfig.width > 0 && loadConfig.height > 0) {
            requestBuilder.setResizeOptions(ResizeOptions.forDimensions(loadConfig.width, loadConfig.height));
        } else if (loadConfig.targetView != null && loadConfig.width == 0 && loadConfig.height == 0) {
            if (loadConfig.targetView.getMeasuredWidth() > 0 && loadConfig.targetView.getMeasuredHeight() > 0) {
                requestBuilder.setResizeOptions(ResizeOptions.forDimensions(loadConfig.targetView.getMeasuredWidth(), loadConfig.targetView.getMeasuredHeight()));
            } else if (loadConfig.targetView.getLayoutParams() != null) {
                ViewGroup.LayoutParams lp = loadConfig.targetView.getLayoutParams();
                requestBuilder.setResizeOptions(ResizeOptions.forDimensions(lp.width, lp.height));
            }
        }
        if (loadConfig.format != null || loadConfig.asBitmap || (loadConfig.loadListener != null && loadConfig.loadListener.returnBitmap)) {
            ImageDecodeOptionsBuilder decodeOptionsBuilder = ImageDecodeOptions.newBuilder().setForceStaticImage(
                    loadConfig.asBitmap || (loadConfig.loadListener != null && loadConfig.loadListener.returnBitmap));
            if (loadConfig.format != null) decodeOptionsBuilder.setBitmapConfig(loadConfig.format);
            requestBuilder.setImageDecodeOptions(decodeOptionsBuilder.build());
            if (loadConfig.loadListener != null && loadConfig.loadListener.returnBitmap) hierarchy.setFadeDuration(0);
        }
        if (loadConfig.transformationList != null || loadConfig.blurRadius > 0) {
            List<BitmapTransformation> list = new ArrayList<>(1);
            if (loadConfig.blurRadius > 0) {
                list.add(new BlurTransformation(loadConfig.blurSampleSize, loadConfig.blurRadius));
            }
            if (loadConfig.transformationList != null) list.addAll(loadConfig.transformationList);
            requestBuilder.setPostprocessor(new MultiTransformation(list));
        }
        if (loadConfig.diskCache == LoadConfig.DISK_CACHE_NONE) requestBuilder.disableDiskCache();
        draweeController = controllerBuilder.setImageRequest(requestBuilder.build())
                .setAutoPlayAnimations(true)
                .setOldController(draweeController)
                .build();
        draweeHolder.setController(draweeController);
        if (loadConfig.targetView != null) {
            boolean isAttachedToWindow;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                isAttachedToWindow = loadConfig.targetView.isAttachedToWindow();
            } else {
                isAttachedToWindow = loadConfig.targetView.getWindowToken() != null;
            }
            if (isAttachedToWindow) draweeHolder.onAttach();
            loadConfig.targetView.setImageDrawable(draweeHolder.getTopLevelDrawable());
        } else {
            draweeHolder.onAttach();
        }
    }

    private static class AttachListener implements View.OnAttachStateChangeListener {
        private DraweeHolder draweeHolder;

        private AttachListener(DraweeHolder draweeHolder) {
            this.draweeHolder = draweeHolder;
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            draweeHolder.onAttach();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            draweeHolder.onDetach();
        }
    }

    private static class WControllerListener implements ControllerListener<ImageInfo> {
        private DraweeHolder draweeHolder;
        private LoadConfig loadConfig;

        private WControllerListener(DraweeHolder draweeHolder, LoadConfig loadConfig) {
            this.draweeHolder = draweeHolder;
            this.loadConfig = loadConfig;
        }

        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
            if (loadConfig.loadListener.returnBitmap) {
                if (imageInfo instanceof CloseableStaticBitmap
                        && !loadConfig.isCircle && loadConfig.roundCornerRadius <= 0
                        && (loadConfig.roundCornerRadii == null || loadConfig.roundCornerRadii.length != 8)) {
                    Bitmap bitmap = ((CloseableStaticBitmap) imageInfo).getUnderlyingBitmap();
                    if (bitmap != null) {
                        loadConfig.loadListener.onSuccess(Bitmap.createBitmap(bitmap));
                        return;
                    }
                }
                int width = 0, height = 0;
                if (imageInfo != null) {
                    width = imageInfo.getWidth();
                    height = imageInfo.getHeight();
                } else if (loadConfig.targetView != null) {
                    width = loadConfig.targetView.getMeasuredWidth();
                    height = loadConfig.targetView.getMeasuredHeight();
                    if (width <= 0 || height <= 0) {
                        ViewGroup.LayoutParams lp = loadConfig.targetView.getLayoutParams();
                        if (lp != null) {
                            width = lp.width;
                            height = lp.height;
                        }
                    }
                }
                if (width <= 0 || height <= 0) {
                    if (loadConfig.width > 0 && loadConfig.height > 0) {
                        width = loadConfig.width;
                        height = loadConfig.height;
                    } else {
                        width = 200;
                        height = 200;
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                draweeHolder.getTopLevelDrawable().setBounds(0, 0, width, height);
                draweeHolder.getTopLevelDrawable().draw(canvas);
                loadConfig.loadListener.onSuccess(bitmap);
            } else {
                loadConfig.loadListener.onSuccess(null);
            }
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            loadConfig.loadListener.onFail();
        }

        @Override
        public void onSubmit(String id, Object callerContext) {
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        }

        @Override
        public void onIntermediateImageFailed(String id, Throwable throwable) {
        }

        @Override
        public void onRelease(String id) {
        }
    }

    private static class MultiTransformation extends BasePostprocessor implements BitmapTransformation.ReuseBitmapListener {
        private final Collection<BitmapTransformation> transformations;
        private Bitmap dest;

        private MultiTransformation(Collection<BitmapTransformation> transformations) {
            this.transformations = transformations;
        }

        @Override
        public void process(Bitmap dest, Bitmap source) {
            this.dest = dest;
            Bitmap result = source;
            for (BitmapTransformation t : transformations) {
                if (t.getReuseBitmapListener() == null) {
                    t.setReuseBitmapListener(this);
                }
                result = t.transform(result, dest.getWidth(), dest.getHeight());
            }
            if (result != dest) {
                if (dest.getWidth() != result.getWidth() || dest.getHeight() != result.getHeight()) {
                    result = Bitmap.createScaledBitmap(result, dest.getWidth(), dest.getHeight(), true);
                }
                super.process(dest, result);
            }
        }

        @Override
        public Bitmap getReuseableBitmap(int width, int height, Bitmap.Config config) {
            Bitmap reuseableBitmap = null;
            if (dest != null && dest.getWidth() == width && dest.getHeight() == height && dest.getConfig() == config) {
                reuseableBitmap = dest;
                dest = null;
            }
            return reuseableBitmap;
        }

        @Override
        public String getName() {
            return "MultiTransformation";
        }

        @Nullable
        @Override
        public CacheKey getPostprocessorCacheKey() {
            StringBuilder builder = new StringBuilder();
            for (BitmapTransformation t : transformations) {
                builder.append(t.getCacheKey()).append(" ");
            }
            return new SimpleCacheKey(builder.toString());
        }
    }
}