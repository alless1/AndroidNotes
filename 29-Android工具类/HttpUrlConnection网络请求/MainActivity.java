package com.example.httpdemo;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.httpdemo.request.HttpUrlClient;
import com.example.httpdemo.request.Request;
import com.example.httpdemo.request.ResponseListener;
import com.example.httpdemo.utils.ImgPathUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private EditText mEditText;
    private TextView mTextView;

    private String url = "https://gank.io/api/data/Android/10/1";
    public static final String URL = "http://review.yayaim.com:8080";
    public static final String STRING_METHOD = "/word/audit";
    public static final String IMG_METHOD = "/picture/audit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.et);
        mTextView = (TextView) findViewById(R.id.tv_result);
    }

    //文字检测
    private void text() {

        HashMap<String, String> map = new HashMap<>();
        map.put("appId","10000");
        map.put("userId","10000");
        String trim = mEditText.getText().toString().trim();
        try {
            String encode = URLEncoder.encode(trim, "utf-8");
            map.put("word",encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("ext","");

        Request request = new Request.Builder()
                .setUrl(URL+STRING_METHOD)
                .setParamMap(map)
                .setRequestType(Request.Type.POST)
                .setListener(new ResponseListener() {
                    @Override
                    public void onResult(final int code, final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("code = "+code+" result = "+result);
                                Log.e(TAG, "run: code = "+code+" result = "+result );
                            }
                        });
                    }
                })
                .build();
        HttpUrlClient.getInstance().doRequest(request);
    }

    //网络图片
    public void imgUrl(){
        HashMap<String, String> map = new HashMap<>();
        map.put("appId","10000");
        map.put("userId","10000");
        String imgUrl = "http://contentaudit.yayaim.com/xinggan.jpg";
        if(imgUrl.endsWith(".gif")){
            Log.e(TAG, "imgUrl: 不能检测网络gif" );
            return ;
        }
        map.put("imgUrl",imgUrl);
        //map.put("type",getImageType(imgUrl));
        map.put("type",imgUrl.substring(imgUrl.lastIndexOf(".") + 1));//直接截取后缀。

        map.put("ext","");
        Request build = new Request.Builder()
                .setUrl(URL + IMG_METHOD)
                .setParamMap(map)
                .setRequestType(Request.Type.GET)
                .setListener(new ResponseListener() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.e(TAG, "run: code = "+code+" result = "+result );
                    }
                })
                .build();
        HttpUrlClient.getInstance().doRequest(build);
    }

    //上传本地图片。先检测限制条件。
    private void localImgUrl(String imgUrl){

        //限制检测。。


        HashMap<String, String> map = new HashMap<>();
        map.put("appId","10000");
        map.put("userId","10000");
       // String imgUrl = mEditText.getText().toString().trim();

        //map.put("imgUrl",imgUrl);
        map.put("type",imgUrl.substring(imgUrl.lastIndexOf(".") + 1));//直接截取后缀。
        //map.put("type",getImageType(imgUrl));

        map.put("ext","");

        String url = "http://review.yayaim.com:8080/picture/audit?appId=10000&userId=10000&type=gif&ext=";

        Request build = new Request.Builder()
                .setUrl(url)
                //.setParamMap(map)
                .setRequestType(Request.Type.POST)
                .setFilePath(imgUrl)
                .setContentType(Request.FILE_CONTENT_TYPE)
                .setListener(new ResponseListener() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.e(TAG, "run: code = "+code+" result = "+result );
                    }
                })
                .build();
        HttpUrlClient.getInstance().doRequest(build);
    }


    public void uploadFile(String filePath){
        Request request = new Request.Builder()
                .setUrl(url)
                .setFilePath(filePath)
                .setRequestType(Request.Type.POST)
                .setContentType(Request.FILE_CONTENT_TYPE)//字节流上传文件，和后台统一。
                .setListener(new ResponseListener() {
                    @Override
                    public void onResult(final int code, final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("code = "+code+" result = "+result);
                                Log.e(TAG, "run: code = "+code+" result = "+result );
                            }
                        });
                    }
                })
                .build();
        HttpUrlClient.getInstance().doRequest(request);
    }
    private String getImageType(String imgUrl) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgUrl, options);
        String type = options.outMimeType;
        if (TextUtils.isEmpty(type)) {
            type = "未能识别的图片";
        } else {
            type = type.substring(6, type.length());
        }
        Log.e(TAG, "  type:  " + type);
        return type;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                text();
                break;
            case R.id.btn2:
                imgUrl();
                break;
            case R.id.btn3:
                //localImgUrl();
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                String realPathFromUri = ImgPathUtils.getRealPathFromUri(this, data.getData());
                Log.e(TAG, "onActivityResult: realPathFromUri= " + realPathFromUri);
                localImgUrl(realPathFromUri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}
