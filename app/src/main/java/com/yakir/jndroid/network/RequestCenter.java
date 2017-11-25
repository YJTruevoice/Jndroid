package com.yakir.jndroid.network;

import com.yakir.jndroid.model.recommand.BaseRecommandModel;
import com.yakir.okhttp.CommonOkHttpClient;
import com.yakir.okhttp.listener.DisposeDataHandle;
import com.yakir.okhttp.listener.DisposeDataListener;
import com.yakir.okhttp.request.CommonRequest;
import com.yakir.okhttp.request.RequestParams;

/**
 * @author yakir
 * @function
 */

public class RequestCenter {
    // 根据参数发送post请求
    private static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    /**
     * 发送首页请求
     *
     * @param listener
     */
    public static void requestRecommandData(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.HOME_RECOMMAND, null, listener, BaseRecommandModel.class);
    }

}
