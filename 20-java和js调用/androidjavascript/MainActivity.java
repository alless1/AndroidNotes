package com.itheima.androidjavascript;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    /***************************************************************
     * H5和Android通信三种方式
     * 1.android主动调用js:javascript:方法名(参数)
     * 2.js主动调用Android
     * 3.js callback式调用Android(非常绕)，来源（典故：为了避免客户端开发人员经常骚扰H5美眉，解耦H5只关心H5，客户端只关心android）
     * ***************************************************************
     */

    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //设置webview
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);

        //页面加载完成调用js方法
        //重新浏览器内核对象
        initWebClient();

        //js和Android两种不同开发语言，不认识
        //核心方法：设置js和Android通信桥梁接口(简而言之，就是设置通信桥梁类)
        JavascriptMethos jsMethos = new JavascriptMethos(this, mWebview);
        //参数1：提供给js调用的方法的对象, 参数2：参数1的映射字符串（第一个参数的别名），因为字符串再所有开发一样通用
        mWebview.addJavascriptInterface(jsMethos, JavascriptMethos.JSINTERFACE);

        //显示
        mWebview.loadUrl("http:/10.0.3.2:8080/html35/index.html");
    }

    private void initWebClient() {
        mWebview.setWebChromeClient(new WebChromeClient());


        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //页面加载完成调用该方法
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //调用js 方法
                //WebView.loadUrl("javascript:方法名(参数)")
                JSONObject json = new JSONObject();
                try {
                    json.put("name", "android");
                    json.put("msg", "你好，我是Android，加个蚝友");
                    mWebview.loadUrl("javascript:receiveMessage("+json.toString()+")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        mWebview = (WebView) findViewById(R.id.webview);
    }
}
