package com.yakir.okhttp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yakir.okhttp.listener.DisposeDataHandle;
import com.yakir.okhttp.listener.DisposeDataListener;
import com.yakir.okhttp.request.CommonRequest;
import com.yakir.okhttp.response.CommonJsonCallback;

/**
 * @author yakir
 * @function
 */

public class BaseOkhttpTest {

    class BaseOkhttpTestActivity extends AppCompatActivity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        private void sendRequest() {

        }

        private void test() {
            CommonOkHttpClient.sendReqeust(CommonRequest.createGetRequest("http://www.baidu.com", null),
                    new CommonJsonCallback(new DisposeDataHandle(new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {

                }

                @Override
                public void onFailure(Object reasonObj) {

                }
            })));
        }

    }
}
