package com.speed.vpnsocks.test.core;

import android.util.Log;

import com.speed.master.utils.ErrorCode;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by alless on 2019/1/6.
 * Description:
 */

public class SpeedTestForHttp extends SpeedTestBase {
    private static final String TAG = "SpeedTestForHttp";
    private HttpURLConnection mConnection;

    public SpeedTestForHttp(SpeedTestRequest request) {
        super(request);
    }

    @Override
    protected void initProxyClient() {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(mSocksInfo.getAddress(), mSocksInfo.getPort()));
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            private PasswordAuthentication authentication = new PasswordAuthentication(mSocksInfo.getUser(), mSocksInfo.getPassword().toCharArray());

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        });
        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(ErrorCode.URL_EXCEPTION, e.getMessage());
            return;
        }
        try {
            mConnection = (HttpURLConnection) url.openConnection(proxy);

        } catch (IOException e) {
            e.printStackTrace();
            onError(ErrorCode.OTHER_IO_EXCEPTION, e.getMessage());
        }
    }

    @Override
    protected void initCommonClient() {
        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(ErrorCode.URL_EXCEPTION, e.getMessage());
            return;
        }
        try {
            mConnection = (HttpURLConnection) url.openConnection();

        } catch (IOException e) {
            e.printStackTrace();
            onError(ErrorCode.OTHER_IO_EXCEPTION, e.getMessage());
        }

    }

    @Override
    protected void uploadFile() {
        try {
            onPrepare();
            mConnection.setRequestMethod("POST");
            mConnection.setRequestProperty("Content-Type", "application/octet-stream");
            mConnection.setDoOutput(true);
            mConnection.setUseCaches(false);
            mConnection.setRequestProperty("Accept-Charset", "UTF-8");
            mConnection.setConnectTimeout(CONNECTION_TIMEOUT_SECOND * 1000);
            mConnection.setReadTimeout(READ_TIMEOUT_SECOND * 1000);
            // mConnection.setDoInput(true);
            DataOutputStream outputStream = new DataOutputStream(mConnection.getOutputStream());

            byte[] buffer = new byte[LOCAL_WRITE_READ_BUFFER_SIZE];
            int totalLen = 0;
            while (totalLen < UPLOAD_SIZE_MAX) {
                outputStream.write(buffer, 0, LOCAL_WRITE_READ_BUFFER_SIZE);
                outputStream.flush();
                totalLen += LOCAL_WRITE_READ_BUFFER_SIZE;
                onProgress(UPLOAD_SIZE_MAX, totalLen);
                //Log.e(TAG, "uploadFile: totalLen =" + totalLen);
                if (isTerminated)
                    break;
            }
            if (isTerminated) {
                if (mListener != null) {
                    onCompletion();
                    return;
                }
            }

            int response = mConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(mConnection.getInputStream(), "UTF-8"));
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                Log.e(TAG, "uploadFile: HTTP_OK  =" + sb.toString());
                br.close();
            } else {
                Log.e(TAG, "uploadFile: response =" + response);
            }
            onCompletion();


        } catch (IOException e) {
            e.printStackTrace();
            onError(ErrorCode.UPLOAD_IO_EXCEPTION, e.getMessage());
        }

    }

    @Override
    protected void downloadFile() {
        try {
            onPrepare();
            mConnection.setRequestMethod("GET");
            mConnection.setUseCaches(false);
            mConnection.setConnectTimeout(CONNECTION_TIMEOUT_SECOND * 1000);
            mConnection.setReadTimeout(READ_TIMEOUT_SECOND * 1000);
            int responseCode = mConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                int fileLength = mConnection.getContentLength();
                Log.e(TAG, "downloadFile: fileLength =" + fileLength);
                DataInputStream dataInputStream = new DataInputStream(mConnection.getInputStream());
                int size = 0;
                int len = 0;
                byte[] buf = new byte[LOCAL_WRITE_READ_BUFFER_SIZE];
                while ((size = dataInputStream.read(buf)) != -1) {
                    len += size;
                    //out.write(buf, 0, size);
                    onProgress(fileLength, len);
                    //Log.e(TAG, "downloadFile:len = " + len);
                    if (len == fileLength || isTerminated) {
                        onCompletion();
                    }
                }
                dataInputStream.close();
            } else {
                Log.e(TAG, "downloadFile: responseCode =" + responseCode);
                onError(ErrorCode.DOWNLOAD_ON_RESPONSE_FAIL, mConnection.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            onError(ErrorCode.DOWNLOAD_IO_EXCEPTION, e.getMessage());

        }
    }

    @Override
    protected void release() {
        super.release();
        if(mConnection!=null){
            mConnection.disconnect();
        }

    }
}
