package com.yunva.sy.sdk.http;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alless on 2018/8/4.
 * Description:封装的简单HttpUrlConnection请求工具类，支持http/https，get,post请求，结果返回String。
 */

public class HttpUrlClient {
    private static final int MAX_RUNNING_THREAD = 4;
    private ExecutorService executor;
    private static HttpUrlClient sHttpUrlClient;
    private HttpUrlClient(){}
    public static HttpUrlClient getInstance(){
        if(sHttpUrlClient==null){
            synchronized (HttpUrlClient.class){
                if(sHttpUrlClient==null){
                    sHttpUrlClient = new HttpUrlClient();
                }
            }
        }
        return sHttpUrlClient;
    }

    public void doRequest(final Request request) {
        start(new Runnable() {
            @Override
            public void run() {
                executeRequest(request);
            }
        });
    }

    private void start(Runnable runnable) {
        if (null == executor) {
            try {
                executor = Executors.newFixedThreadPool(MAX_RUNNING_THREAD);
            } catch (Throwable t) {
                executor = Executors.newCachedThreadPool();
            }
        }
        try {
            executor.submit(runnable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //执行请求，返回结果。
    private void executeRequest(Request request) {
        String url = request.getUrl();
        HttpConnection connection = new HttpConnection(url);
        String result = connection.doRequest(request);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
            //转换数据
            // convertBean(login,connection.getResponseCode(), result);
            request.getListener().onResult(connection.getResponseCode(), result);
        } else {
            request.getListener().onResult(connection.getResponseCode(), connection.getResponseMessage());

        }

    }


}
