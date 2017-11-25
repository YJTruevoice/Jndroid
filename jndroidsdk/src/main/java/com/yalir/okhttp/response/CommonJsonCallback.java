package com.yalir.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.yalir.okhttp.exception.OKHttpException;
import com.yalir.okhttp.listener.DisposeDataHandle;
import com.yalir.okhttp.listener.DisposeDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author yakir
 * @function 专门处理JSON的回调相应
 */

public class CommonJsonCallback implements Callback {

    // 与服务器返回的字段的对应关系
    protected final String RESULT_CODD = "ecode";
    protected final int RESULT_CODD_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    // 定义异常类型
    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private Handler mDeliveryHandler; // 进行消息转发
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
    }

    // 请求失败处理
    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OKHttpException(NETWORK_ERROR, e));
            }
        });
    }

    // 相应处理函数
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    /**
     * 处理服务器返回的相应数据
     *
     * @param responseObj
     */
    private void handleResponse(Object responseObj) {

        if (null != responseObj && "".equals(responseObj.toString().trim())) {
            mListener.onFailure(new OKHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }
        try {
            JSONObject result = new JSONObject(responseObj.toString());
            // 尝试解析JSON
            if (result.has(RESULT_CODD)) {
                // 从json对象中取出响应码，若为0，则是正确相应
                if (result.getInt(RESULT_CODD) == RESULT_CODD_VALUE) {
                    if (null == mClass) {// 返回空数据就不需要解析
                        mListener.onSuccess(responseObj);
                    } else {
                        // json对象转化为对应实体
                        Object obj = JSON.parseObject(result.toString(), mClass);// 得封装转换方法
                        if (null != obj) {
                            mListener.onSuccess(obj);
                        } else {
                            mListener.onFailure(new OKHttpException(OTHER_ERROR, EMPTY_MSG));
                        }
                    }
                } else {
                    // 将服务器返回的异常回调到应用层处理
                    mListener.onFailure(new OKHttpException(OTHER_ERROR, result.get(RESULT_CODD)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mListener.onFailure(new OKHttpException(OTHER_ERROR, e.getMessage()));
        }
    }
}
