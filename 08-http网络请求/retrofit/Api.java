package com.example.xmldemo.network;

import com.example.xmldemo.xxBean.xxxDeamBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Administrator on 2017/3/26.
 */

public interface Api {
    /**
     * 泛型T你想要解析后数据结构
     */
    @GET("login_validate")
    Call<xxxDeamBean> getRequest(@Query("username") String username, @Query("pwd") String pwd, @Query("keep_login") String keep_login);
}

