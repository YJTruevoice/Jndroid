package com.yakir.okhttp.listener;

/**
 * @author yakir
 * @function 自定义请求网络结果回调
 */

public interface DisposeDataListener {
    /**
     * 请求成功回调处理
     */
    void onSuccess(Object responseObj);

    /**
     * 请求失败回调
     */
    void onFailure(Object reasonObj);
}
