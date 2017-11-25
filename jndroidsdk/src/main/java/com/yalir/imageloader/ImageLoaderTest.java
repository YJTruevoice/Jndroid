package com.yalir.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author yakir
 * @function
 */

public class ImageLoaderTest {

    Context context;
    ImageView imageView;

    private void testApi() {

        // 为Imageloader配置参数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).build();
        // 获取ImageLoader实例
        ImageLoader imageLoader = ImageLoader.getInstance();
        // 显示图片时候的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();

        // 加载图片
        imageLoader.displayImage("url", imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
            }
        });
        // 使用自己封装的类加载图片
        ImageLoaderManager.getInstance(context).displayImage(imageView, "");
    }
}
