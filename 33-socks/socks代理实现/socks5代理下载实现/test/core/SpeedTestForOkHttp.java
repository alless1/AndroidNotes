package com.speed.vpnsocks.test.core;

import android.os.Build;
import android.util.Log;

import com.speed.master.App;
import com.speed.master.utils.ErrorCode;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;
import com.speed.vpnsocks.test.utils.PathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
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

/**
 * Created by chengjie on 2019/1/3.
 * Description:
 */

public class SpeedTestForOkHttp extends SpeedTestBase {
    private static final String TAG = "SpeedTestForOkHttp";
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");
    private OkHttpClient mOkHttpClient;

    public SpeedTestForOkHttp(SpeedTestRequest request) {
        super(request);
    }

    @Override
    protected void initData() {
        super.initData();
        mUploadFilePath = PathUtil.createEmptyFile(App.getInstance(), UPLOAD_SIZE_MAX);
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
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS)
                //.retryOnConnectionFailure(true)
                .proxy(proxy)
                .build();
    }

    @Override
    protected void initCommonClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS)
                //.retryOnConnectionFailure(true)
                .proxy(Proxy.NO_PROXY)
                .build();
    }


    @Override
    protected void downloadFile() {
        Request request = new Request.Builder()
                .url(mUrl)
                .get()
                .header("Cache-Control", "no-cache")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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

                onPrepare();

                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    int contentLength = (int) response.body().contentLength();
                    Log.d(TAG, "onResponse: contentLength =" + contentLength);
                   /* File file = new File(mSaveFilePath, "testDown.zip");
                    if (file.exists())
                        file.delete();
                    fos = new FileOutputStream(file);*/
                    byte[] buffer = new byte[LOCAL_WRITE_READ_BUFFER_SIZE];
                    int len = -1;
                    int lengthProgress = 0;
                    while ((len = is.read(buffer)) != -1 && !isTerminated) {
                        //fos.write(buffer, 0, len);
                        lengthProgress += len;
                        onProgress(contentLength, lengthProgress);
                        //Log.e(TAG, "onResponse: lengthProgress =" + lengthProgress);
                    }
                    onCompletion();
                    //fos.close();
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
                        e1.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    protected void uploadFile() {
        File file = new File(mUploadFilePath);
        if (!file.exists()) {
            onError(ErrorCode.FILE_FAIL, "uploadFile: file no exist");
            Log.e(TAG, "uploadFile: file no exist");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0以下 writeTo会调用两次
            isSecondTime = true;
        } else {
            isSecondTime = false;
        }

// .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(UP_FILE_NAME, file.getName(), createProgressRequestBody(MEDIA_OBJECT_STREAM, file))
                .build();

        Request request = new Request.Builder()
                .url(mUrl)
                .post(body)
                .header("Cache-Control", "no-cache")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "uploadFile onFailure" + e.toString());
                //failedCallBack("上传失败", callBack);
                onError(ErrorCode.UPLOAD_IO_EXCEPTION, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.d(TAG, "response --isSuccessful--->" + string);
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

    private boolean isSecondTime = false;
    private boolean isStartProgress = false;

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
                Log.d(TAG, "writeTo:1");
                if (isSecondTime) {
                    isStartProgress = true;
                    onPrepare();
                } else {
                    isSecondTime = true;
                }

                Source source;
                source = Okio.source(file);
                Buffer buf = new Buffer();
                int remaining = (int) contentLength();
                int current = 0;
                long read = 0;
                while ((read = source.read(buf, 2048)) != -1) {
                    sink.write(buf, read);
                    current += read;
                    //Log.e(TAG, "remaining " + remaining + " current------>" + current);
                    if (isStartProgress) {
                        onProgress(remaining, current);
                        if (isTerminated) {
                            onCompletion();
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void release() {
        super.release();
/*        if (mOkHttpClient != null)
            mOkHttpClient.dispatcher().executorService().shutdown();*/
    }

}
