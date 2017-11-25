package com.yakir.okhttp;

import com.yakir.okhttp.listener.DisposeDataHandle;
import com.yakir.okhttp.response.CommonJsonCallback;
import com.yakir.okhttp.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author yakir
 * @function 请求发送，请求参数的配置，支持HTTPS
 */

public class CommonOkHttpClient {

    private static final int TIME_OUT = 30; // 超时时间
    private static OkHttpClient mOkHttpClient;

    /**
     * 给Client配置参数
     */
    static {
        // 创建client对象的构建者
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        // 为构建者填充超时间
        okHttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);// 链超时间
        okHttpBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);// 读超时间
        okHttpBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);// 写超时间

        okHttpBuilder.followRedirects(true);
        // 支持HTTPS
        okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        okHttpBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory());

        // 生成client对象
        mOkHttpClient = okHttpBuilder.build();
    }

    /**
     * 发送具体的http/https请求
     *
     * @param request
     * @param commCallback
     * @return Call
     */
    public static Call sendReqeust(Request request, CommonJsonCallback commCallback) {

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commCallback);

        return call;
    }

    public static void get(Request request, DisposeDataHandle disposeDataHandle) {
        sendReqeust(request, new CommonJsonCallback(disposeDataHandle));
    }
}
