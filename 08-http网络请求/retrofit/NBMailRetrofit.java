package com.example.xmldemo.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class NBMailRetrofit {
    private static NBMailRetrofit sNBMailRetrofit;
    public static final String BASE_URL = "http://www.oschina.net/action/api/";

    private final Api mApi;
    private Gson mGson = new GsonBuilder().setLenient().create();//设置宽大处理畸形的json

    private NBMailRetrofit () {
        //使用Retrofit来实现Api接口 需要配置gson转换器
        Retrofit retrofit = new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mApi = retrofit.create(Api.class);
    }

    public static NBMailRetrofit getInstance () {
        if (sNBMailRetrofit == null) {
            synchronized (NBMailRetrofit.class) {
                if (sNBMailRetrofit == null) {
                    sNBMailRetrofit = new NBMailRetrofit();
                }
            }
        }
        return sNBMailRetrofit;
    }

    public Api getApi () {
        return mApi;
    }

}
