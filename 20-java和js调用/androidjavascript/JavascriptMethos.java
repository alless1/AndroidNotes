package com.itheima.androidjavascript;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 统一管理所有js和Android通信
 * Created by youliang.ji on 2017/5/12.
 */

public class JavascriptMethos {

    public static String JSINTERFACE = "jsInterface";
    private Context mContext;
    private WebView mWebView;

    public JavascriptMethos(Context mContext, WebView mWebView) {
        this.mContext = mContext;
        this.mWebView = mWebView;
    }

    /**
     * 给js调用的弹出toast方法
     * 为什么要添加注解：android4.2以上（包含），如果不加注解，
     * js无法调用Android方法，因为4.2之前js和android通信有安全问题，如果不加上该注解，4.2以上js无法调用Android方法
     *
     * @param json
     */
    @JavascriptInterface
    public void showToast(String json) {
        Toast.makeText(mContext, json, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void getHotelData(String json) throws JSONException {

        //解析callbak方法名
        JSONObject jsJson = new JSONObject(json);
        final String callback = jsJson.optString("callback");

        System.out.println("接收到js传递callback参数=" + json);
        //模拟访问网络
        //返回json
        final JSONObject callbackJson = new JSONObject();
        try {
            callbackJson.put("name", "8天连锁酒店");
            callbackJson.put("hotel", "99");
            callbackJson.put("phone", "075588888888");

            //调用js方法
            //mWebView.loadUrl("javascript:方法名(参数)");默认Android调用js独立运行在一个进程WebViewCoreThread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //mWebView.loadUrl("javascript:receiveHotelData(" + callbackJson.toString() + ")");
                    mWebView.loadUrl("javascript:"+callback+"(" + callbackJson.toString() + ")");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler();
}
