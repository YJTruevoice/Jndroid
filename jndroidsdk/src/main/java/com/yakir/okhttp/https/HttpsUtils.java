package com.yakir.okhttp.https;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author yakir
 * @function https工具
 */

public class HttpsUtils {


    /**
     * trust all the point
     *
     * @return
     */
    public static SSLSocketFactory getSslSocketFactory() {

        // 1.生成信任管理器类
        X509TrustManager mTrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        // 2.创建加密上下文
        SSLContext sslContext = null;
        try {
            // 与服务器要保持一致的算法类型
            sslContext = sslContext.getInstance("SSL");
            X509TrustManager[] xTrustArray = new X509TrustManager[]{mTrustManager};
            sslContext.init(null, xTrustArray, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }

}
