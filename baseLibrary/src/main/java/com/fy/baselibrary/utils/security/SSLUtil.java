package com.fy.baselibrary.utils.security;

import android.annotation.SuppressLint;
import android.util.Log;

import com.fy.baselibrary.utils.ResUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 证书工具类
 * Created by fangs on 2017/3/1.
 */
public class SSLUtil {

    private SSLUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    //使用命令keytool -printcert -rfc -file srca.cer 导出证书为字符串，
    // 然后将字符串转换为输入流，如果使用的是okhttp可以直接使用 new Buffer().writeUtf8(s).inputStream()

    public static Object[] getSSLSocketFactory(String... crtFileName){
        return getSSLSocketFactory(null, crtFileName);
    }

    /**
     * 返回SSLSocketFactory
     *
     * @param certificates 证书的输入流
     * @return SSLSocketFactory
     */
    public static Object[] getSSLSocketFactory(InputStream... certificates) {
        return getSSLSocketFactory(null, certificates);
    }

    /**
     * 双向认证
     *
     * @param keyManagers  KeyManager[]
     * @param certificates 证书的输入流
     * @return SSLSocketFactory
     */
    public static <T> Object[] getSSLSocketFactory(KeyManager[] keyManagers, T... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            InputStream crtIs = null;
            for (T certificate : certificates) {
                String certificateAlias = Integer.toString(index++);

                if (certificate instanceof InputStream){
                    crtIs = (InputStream) certificate;
                } else if (certificate instanceof String){
                    crtIs = ResUtils.getAssetsInputStream((String) certificate);
                }

                if (null == crtIs) continue;

                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(crtIs));
                try {
                    crtIs.close();
                } catch (IOException e) {
                }
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), new SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            Object[] result = new Object[2];
            result[0] = sslSocketFactory;
            result[1] = trustManager;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得双向认证所需的参数
     *
     * @param bks          bks证书的输入流
     * @param keystorePass 秘钥
     * @return KeyManager[]对象
     */
    public static KeyManager[] getKeyManagers(InputStream bks, String keystorePass) {
        KeyStore clientKeyStore = null;
        try {
            clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bks, keystorePass.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, keystorePass.toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
            return keyManagers;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     */
    @SuppressLint("TrulyRandom")
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            Log.d("checkClientTrusted", "authType:" + authType);
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            Log.d("checkServerTrusted", "authType:" + authType);
            try {
                chain[0].checkValidity();
            } catch (Exception var4) {
                Log.e("checkServerTrusted", "Exception", var4);
            }

        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    //配置信任所有证书
    public static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

}
