package com.yunva.sy.sdk.http;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by alless on 2018/8/4.
 * Description:
 */
public class HttpConnection {

    private HttpURLConnection mConn = null;

    private static final String LOG_TAG = "HttpConnection";
    protected static final String HTTP_REQ_PROPERTY_CHARSET = "Accept-Charset";
    protected static final String HTTP_REQ_VALUE_CHARSET = "UTF-8";
    protected static final String HTTP_REQ_PROPERTY_CONTENT_TYPE = "Content-Type";
    protected static final String HTTP_REQ_PROPERTY_CONTENT_LENGTH = "Content-Length";
    protected static final String HTTP_REQ_METHOD_GET = "GET";
    protected static final String HTTP_REQ_METHOD_POST = "POST";
    protected static final String HTTP_REQ_COOKIE = "Cookie";

    //连接超时
    protected static final int CONNECT_TIMEOUT = 5 * 1000;
    //读取超时
    protected static final int DEFAULT_READ_TIMEOUT = 10 * 1000;

    public HttpConnection(String url) {
        try {
            if (url.startsWith("https:")) {
                // connection = new HTTPSConnection(url);
                trustAllHosts();
                mConn = (HttpsURLConnection) new URL(url).openConnection();
                ((HttpsURLConnection) mConn).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            } else {
                // connection = new HTTPConnection(url);

                mConn = (HttpURLConnection) new URL(url).openConnection();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private HttpURLConnection getURLConnection() {
        return mConn;
    }

    // Create a trust manager that does not validate certificate chains, Android use X509 cert
    public static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }

                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立请求
     *
     * @param request
     * @return
     */
    public String doRequest(Request request) {
        if (null == getURLConnection()) {
            Log.e(LOG_TAG, "URLConnection is null");
            return "";
        }
        //设置通用属性
        setURLConnectionCommonPara(request);

        switch (request.getRequestType()) {
            case GET:
                return doGetRequest();
            case POST:
                return doPostRequest(request);
            default:
                return "";
        }

    }


    //设置条件参数
    private void setURLConnectionCommonPara(Request request) {
        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return;
        }
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setUseCaches(false);
        connection.setRequestProperty(HTTP_REQ_PROPERTY_CHARSET, HTTP_REQ_VALUE_CHARSET);
        connection.setRequestProperty(HTTP_REQ_PROPERTY_CONTENT_TYPE, request.getContentType());
        //检查cookie
        if (null != request.getCookieInfo() && request.getCookieInfo().size() > 0) {
            setURLConnectionCookie(request.getCookieInfo());
        }

    }

    //设置cookie
    private void setURLConnectionCookie(HashMap<String, String> cookieInfo) {
        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return;
        }
        String cookieString = connection.getRequestProperty(HTTP_REQ_COOKIE);
        if (!TextUtils.isEmpty(cookieString)) {
            cookieString = cookieString + ";";
        } else {
            cookieString = "";
        }
        for (Map.Entry<String, String> entry : cookieInfo.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
                Log.d(LOG_TAG, "cookie inf is bad");
            } else {
                cookieString = cookieString + entry.getKey() + "=" + entry.getValue() + ";";
            }
        }
        connection.setRequestProperty(HTTP_REQ_COOKIE, cookieString);
    }


    /**
     * get请求
     *
     * @return
     */
    protected String doGetRequest() {
        String result = "";
        InputStream is = null;
        BufferedReader br = null;
        try {
            HttpURLConnection connection = getURLConnection();
            if (null == connection) {
                return "";
            }
            connection.setRequestMethod(HTTP_REQ_METHOD_GET);
            is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, HTTP_REQ_VALUE_CHARSET));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (javax.net.ssl.SSLHandshakeException ee) {
            Log.e(LOG_TAG, "javax.net.ssl.SSLPeerUnverifiedException");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
            return result;
        }
    }


    /**
     * post请求
     *
     * @param request
     * @return
     */
    protected String doPostRequest(Request request) {
        BufferedReader br = null;
        InputStream inptStream = null;
        OutputStream outputStream = null;
        try {
            HttpURLConnection connection = getURLConnection();
            if (null == connection) {
                return "";
            }
            connection.setRequestMethod(HTTP_REQ_METHOD_POST);

            if(!TextUtils.isEmpty(request.getFilePath())){//上传文件
                outputStream = new DataOutputStream(connection.getOutputStream());
                InputStream is = new FileInputStream(request.getFilePath());
                byte[] isBytes = new byte[1024];
                int len = 0;
                while ((len = is.read(isBytes)) != -1) {
                    outputStream.write(isBytes, 0, len);
                }
                is.close();
                outputStream.flush();
            }else{
                byte[] data = request.getData();
                connection.setRequestProperty(HTTP_REQ_PROPERTY_CONTENT_LENGTH, String.valueOf(data.length));
                //获得输出流，向服务器写入数据
                outputStream = connection.getOutputStream();
                outputStream.write(data);
            }

            int response = connection.getResponseCode();            //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                inptStream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(inptStream, HTTP_REQ_VALUE_CHARSET));
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inptStream != null) {
                    inptStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public String getResponseMessage() {
        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return "";
        } else {
            try {
                return getURLConnection().getResponseMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public int getResponseCode() {
        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return -1;
        } else {
            try {
                return getURLConnection().getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }


}
