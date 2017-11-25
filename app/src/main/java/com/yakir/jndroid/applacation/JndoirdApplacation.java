package com.yakir.jndroid.applacation;

import android.app.Application;

/**
 * @author yakir
 * @function 程序入口
 */

public class JndoirdApplacation extends Application {

    private static JndoirdApplacation mApplacation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplacation = this;
    }

    public static JndoirdApplacation getInstance() {
        return mApplacation;
    }
}
