package com.yakir.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yalir.R;

/**
 * @author yakir
 * @function 初始化UniversalImageLoader，加载网络图片
 */

public class ImageLoaderManager {

    /**
     * 设置一些默认的配置参数
     */
    private static final int THREAD_COUNT = 4; // 标明加载p图片时最多限制4条线程
    private static final int PROPRITY = 2; // 标明图片加载优先级
    private static final int DISK_CACHE_SIZE = 50 * 1024;// 标明UIL最多缓存多少图片
    private static final int CONNECTION_TIME_OUT = 5 * 1000;// 链接超时时间
    private static final int READ_TIME_OUT = 30 * 1000;// 读取超时时间

    private static ImageLoaderManager mInstance = null;
    private static ImageLoader mImageLoader = null;

    public static ImageLoaderManager getInstance(Context context) {
        if (null != mInstance) {
            synchronized (ImageLoaderManager.class) {
                if (null != mInstance) {
                    mInstance = new ImageLoaderManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 单利私有构造方法
     *
     * @param context
     */
    private ImageLoaderManager(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(THREAD_COUNT)
                .threadPriority(Thread.NORM_PRIORITY - PROPRITY)
                .denyCacheImageMultipleSizesInMemory()// 防止缓存多套尺寸的图片到内存中
                .memoryCache(new WeakMemoryCache())// 使用弱应用内存缓存
                .diskCacheSize(DISK_CACHE_SIZE)// 分配SD卡缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())// 使用MD5命名文件
                .tasksProcessingOrder(QueueProcessingType.LIFO)// 图片下载顺序
                .defaultDisplayImageOptions(getDefaultOptions())// 得到默认的图片加载Options
                .imageDownloader(new BaseImageDownloader(context, CONNECTION_TIME_OUT, READ_TIME_OUT))// 设置图片下载器
                .writeDebugLogs()// debug环境下会输出日志
                .build();
        ImageLoader.getInstance().init(configuration);
        mImageLoader = ImageLoader.getInstance();
    }

    /**
     * 得到默认的图片加载Options
     *
     * @return
     */
    private DisplayImageOptions getDefaultOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.jndroidsdk_img_error)
                .showImageOnFail(R.drawable.jndroidsdk_img_error)
                .cacheInMemory(true)// 允许缓存内存
                .cacheOnDisk(true)// 允许缓存到硬盘
                .bitmapConfig(Bitmap.Config.RGB_565)// 使用图片解码类型
                .decodingOptions(new BitmapFactory.Options())// 图片解码配置
                .build();
        return options;
    }

    /**
     * 不缓存
     *
     * @return
     */
    public DisplayImageOptions getOptionsWithNoCache() {
        DisplayImageOptions options = new
                DisplayImageOptions.Builder()
                //.cacheInMemory(true)//设置下载的图片是否缓存在内存中, 重要，否则图片不会缓存到内存中
                //.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中, 重要，否则图片不会缓存到硬盘中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .decodingOptions(new BitmapFactory.Options())//设置图片的解码配置
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new FadeInBitmapDisplayer(400))
                .build();
        return options;
    }

    /**
     * 加载图片API
     *
     * @param imageView
     * @param url
     */
    public void displayImage(ImageView imageView, String url) {
        if (null != mImageLoader) {
            mImageLoader.displayImage(url, imageView, null, null);
        }
    }

    public void displayImage(ImageView imageView, String url, ImageLoadingListener listener) {
        if (null != mImageLoader) {
            mImageLoader.displayImage(url, imageView, null, listener);
        }
    }

    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener listener) {
        if (null != mImageLoader) {
            mImageLoader.displayImage(url, imageView, options, listener);
        }
    }
}
