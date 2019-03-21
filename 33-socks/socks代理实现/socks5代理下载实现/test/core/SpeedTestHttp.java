package com.speed.vpnsocks.test.core;

import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;

import com.speed.master.http.RequestUtils;
import com.speed.master.utils.ErrorCode;
import com.speed.vpnsocks.test.bean.SocksInfo;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;
import com.speed.vpnsocks.test.listener.ISpeedTestListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import static android.content.ContentValues.TAG;

/**
 * Created by chengjie on 2018/12/28
 * Description:
 */
public class SpeedTestHttp {
    private static final String TAG = "SpeedTestHttp";
    private static final String UP_FILE_NAME = "android_test_up";
    private String mUrl;
    private SpeedTestRequest.TestType mTestType;
    private SocksInfo mSocksInfo;
    private String mSaveFilePath;
    private String mUploadFilePath;
    private ISpeedTestListener mListener;
    private final int DEFAULT_CONNECTION_TIMEOUT = 3;//s
    private final int DEFAULT_WRITE_TIMEOUT = 10;//s
    private final int DEFAULT_READ_TIMEOUT = 10;//s
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");
    private final int TEST_TIME_LIMIT = 5 * 1000;//下载最大时间
    private final int TEST_TIME_OUT_MAX = 8 * 1000;//辅助
    private Timer mTimer;
    private volatile boolean isTerminated;
    private volatile boolean isCompletion;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            isTerminated = true;
        }
    };
    private TimerTask mTimeOutTask = new TimerTask() {
        @Override
        public void run() {
            onCompletion();
        }
    };

    private synchronized void onError(int error, String result) {
        if (mListener != null) {
            Log.e(TAG, "onError: " + error + result);
            mListener.onError(error, result);
            mListener = null;
            cancelTimer();
            //release();
        }
    }

    private synchronized void onCompletion() {
        if (mListener != null) {
            isCompletion = true;
            Log.e(TAG, "onCompletion: ");
            mListener.onCompletion(null);
            mListener = null;
            cancelTimer();
            release();
        }
    }

    private OkHttpClient mOkHttpClient;

    public SpeedTestHttp(SpeedTestRequest request) {
        mUrl = request.getUrl();
        mTestType = request.getTestType();
        mSocksInfo = request.getSocksInfo();
        mSaveFilePath = request.getSaveFilePath();
        mUploadFilePath = request.getUploadFilePath();
        mListener = request.getListener();
        mTimer = new Timer();
    }

    private void startTimer() {
        mTimer.schedule(mTimerTask, TEST_TIME_LIMIT);
        mTimer.schedule(mTimeOutTask, TEST_TIME_OUT_MAX);
    }

    private void cancelTimer() {
        mTimer.cancel();
    }

    public void start() {
        mOkHttpClient = null;

        if (mSocksInfo == null || TextUtils.isEmpty(mSocksInfo.getAddress()) || TextUtils.isEmpty(mSocksInfo.getUser())) {
            mOkHttpClient = getClient();
        } else {
            mOkHttpClient = getProxyClient();
        }

        switch (mTestType) {
            case DOWNLOAD:
                downloadFile(mOkHttpClient);
                break;
            case UPLOAD:
                uploadFile(mOkHttpClient);
                break;
        }
    }

    private void release() {
        if (mOkHttpClient != null)
            //mOkHttpClient.connectionPool().evictAll();
            mOkHttpClient.dispatcher().executorService().shutdown();
    }


    public OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public OkHttpClient getProxyClient() {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(mSocksInfo.getAddress(), mSocksInfo.getPort()));
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            private PasswordAuthentication authentication = new PasswordAuthentication(mSocksInfo.getUser(), mSocksInfo.getPassword().toCharArray());

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        });
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .proxy(proxy)
                .build();

    }


    private void downloadFile(OkHttpClient client) {
        Request request = new Request.Builder().url(mUrl).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (isCompletion)
                    return;
                Log.e(TAG, "onFailure: " + e.getMessage());
                onError(ErrorCode.DOWNLOAD_IO_EXCEPTION, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200 || response.body() == null) {
                    Log.e(TAG, "onResponse: " + response.code());
                    onError(ErrorCode.DOWNLOAD_ON_RESPONSE_FAIL, String.valueOf(response.code()));
                    return;
                }
                if (mListener != null)
                    mListener.onPrepare();

                startTimer();

                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    int contentLength = (int) response.body().contentLength();
                    Log.d(TAG, "onResponse: contentLength =" + contentLength);
                    File file = new File(mSaveFilePath, "testDown.zip");
                    if (file.exists())
                        file.delete();
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024 * 64];
                    int len = -1;
                    int lengthProgress = 0;
                    while ((len = is.read(buffer)) != -1 && !isTerminated) {
                        fos.write(buffer, 0, len);
                        lengthProgress += len;

                        if (mListener != null) {
                            mListener.onProgress(contentLength, lengthProgress);
                        }
                        Log.e(TAG, "onResponse: lengthProgress =" + lengthProgress);
                    }
                    onCompletion();
                    fos.close();
                    is.close();
                    // String result = "代理状态：" + (CVSpeedUp.isProxyRunning() && Constants.isGoProxyChannel) + "\r\n文件大小：" + lengthProgress + "\r\n耗费时间 =" + (System.currentTimeMillis() - startTime) / 1000 + "s" + "\r\n文件路径：" + file.getAbsolutePath();
                    //listener.onSuccess(result);
                } catch (Exception e) {
                    onError(ErrorCode.DOWNLOAD_READ_TIME_OUT, e.getMessage());
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (Exception e1) {

                    }

                }
            }
        });

    }

    private void uploadFile(OkHttpClient client) {
        File file = new File(mUploadFilePath);
        if (!file.exists()) {
            onError(ErrorCode.FILE_FAIL, "uploadFile: file no exist");
            Log.e(TAG, "uploadFile: file no exist");
            return;
        }


        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(UP_FILE_NAME, file.getName(), createProgressRequestBody(MEDIA_OBJECT_STREAM, file))
                .build();
        Request request = new Request.Builder().url(mUrl).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (isCompletion)
                    return;
                Log.e(TAG, "uploadFile onFailure" + e.toString());
                //failedCallBack("上传失败", callBack);
                onError(ErrorCode.UPLOAD_IO_EXCEPTION, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e(TAG, "response --isSuccessful--->" + string);
                    //successCallBack((T) string, callBack);
                    onCompletion();
                } else {
                    // failedCallBack("上传失败", callBack);
                    Log.e(TAG, "onResponse: 上传失败");
                    onError(ErrorCode.UPLOAD_ON_RESPONSE_FAIL, response.message());
                }
            }
        });


    }

    public RequestBody createProgressRequestBody(final MediaType contentType, final File file) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                if (mListener != null)
                    mListener.onPrepare();
                startTimer();
                Source source;
                source = Okio.source(file);
                Buffer buf = new Buffer();
                int remaining = (int) contentLength();
                int current = 0;
                long read = 0;
                while ((read = source.read(buf, 1024)) != -1 && !isTerminated) {
                    sink.write(buf, read);
                    current += read;
                    Log.e(TAG, "remaining " + remaining + " current------>" + current);
                    if (mListener != null) {
                        mListener.onProgress(remaining, current);
                    }
                }
                onCompletion();
            }
        };
    }

}
