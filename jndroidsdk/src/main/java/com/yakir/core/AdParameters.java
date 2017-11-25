package com.yakir.core;


import com.yakir.constant.SDKConstant;

/**
 * @author: yakir
 * @function: 广告SDK全局参数配置, 都用静态来保证统一性
 */
public final class AdParameters {

    /**
     * 用来记录可自动播放的条件，默认都可以自动播放
     */
    private static SDKConstant.AutoPlaySetting currentSetting = SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI;

    public static void setCurrentSetting(SDKConstant.AutoPlaySetting setting) {
        currentSetting = setting;
    }

    public static SDKConstant.AutoPlaySetting getCurrentSetting() {
        return currentSetting;
    }

    /**
     * 获取sdk当前版本号
     */
    public static String getAdSDKVersion() {
        return "1.0.0";
    }
}
