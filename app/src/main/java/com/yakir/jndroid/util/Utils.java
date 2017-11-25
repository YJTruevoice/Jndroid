package com.yakir.jndroid.util;

import android.content.Context;

import com.yakir.jndroid.applacation.JndoirdApplacation;

/**
 * @author yakir
 * @function
 */

public class Utils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @see #dip2px(float)
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            context = JndoirdApplacation.getInstance();
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        final float scale = JndoirdApplacation.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = JndoirdApplacation.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(float spValue) {
        final float scale = JndoirdApplacation.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * 从px 转为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
