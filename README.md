# ImageLoader
Glide、Fresco的封装，统一接口、统一使用ImageView完成图片加载，解决了fresco使用自定义view的高侵入性，可自由切换
<br><br>
Glide加载圆形、圆角图片时针对ScaleType做了优化，用一次变换完成，节省内存、提高性能
<br><br>
以前一直用的Glide，直到有一天要换成Fresco。。。这才知道有多么麻烦，于是有了这个库
<br><br>
Picaso可能以后会支持，目前需要的话可以自己实现一个PicasoLoader
<br><br>
#### 使用方法
in application:
```java
    ImageLoader.init(this, new GlideLoader());
    //or
    ImageLoader.init(this, new FrescoLoader());
```
in usage:
```java
    ImageLoader.with(context)
        .load(url)
        .diskCache(LoadConfig.DISK_CACHE_ALL)
        .scaleType(ImageView.ScaleType.CENTER_CROP)
        .placeholder(R.mipmap.a)
        .error(R.mipmap.ic_launcher)
        .round(30)
        .fadeDuration(300)
        .blur(8, 5)
        .into(imageView);
```

#### 参数
```java
    public Context context;
    public String url;
    public File file;
    public int resId = Integer.MIN_VALUE;
    public Uri uri;
    public int placeholderResId = Integer.MIN_VALUE;
    public Drawable placeholderDrawable;
    public int errorResId = Integer.MIN_VALUE;
    public Drawable errorDrawable;
    /**
     * 磁盘缓存策略，glide支持所有，fresco只支持有和没有两种
     */
    public int diskCache = DISK_CACHE_DEFAULT;
    /**
     * 针对该次请求使用的config，只支持RGB_565、ARGB_8888
     */
    public Bitmap.Config format;
    /**
     * 除了对图片做变换，同时也会修改ImageView的ScaleType
     */
    public ImageView.ScaleType scaleType;
    /**
     * 跳过内存缓存
     */
    public boolean skipMemory;
    /**
     * 为true的话gif不会自动播放
     */
    public boolean asBitmap;
    /**
     * 期望获得的图片宽高,负值表示按原图大小加载
     */
    public int width, height;
    /**
     * 圆形图片，支持placeholder，最好搭配fitCenter使用，否则fresco会用镜像显示小图片
     */
    public boolean isCircle;
    /**
     * 圆角图片，支持placeholder，最好搭配centerCrop使用，否则fresco会用镜像显示小图片
     */
    public float roundCornerRadius;
    /**
     * float array of 8 radii in pixels. Each corner receives two radius values [X, Y].
     * The corners are ordered top-left, top-right, bottom-right, bottom-left.
     */
    public float[] roundCornerRadii;
    /**
     * 高斯模糊时将原图缩小多少倍，可以节省内存，提高效率，不过会影响生成的图片大小，
     * 在使用CENTER_INSIDE之类的不会缩放小图的ScaleType时，请填1
     */
    public float blurSampleSize;
    /**
     * 高斯模糊采样半径
     */
    public int blurRadius;
    /**
     * 透明渐变动画时长，0为关闭动画
     */
    public int fadeDuration;
    /**
     * 统一的图形变换接口，fresco动图不支持
     */
    public List<BitmapTransformation> transformationList;
    public LoadListener loadListener;
    public ImageView targetView;
```

### 引入
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
Step 2. Add the dependency:
```gradle
    dependencies {
        complie 'com.github.wtuadn.ImageLoader:base:1.0.3'//必选
        complie 'com.github.wtuadn.ImageLoader:glideloader:1.0.3'//二选一
        complie 'com.github.wtuadn.ImageLoader:frescoloader:1.0.3'//二选一
    }
```
<br><br>
最后感谢[ImageLoaderFramework](https://github.com/ladingwu/ImageLoaderFramework) Fresco直接用ImageView加载的方法受此启发，Demo里的图片地址列表也是直接引用它的。