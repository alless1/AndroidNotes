package com.yunva.sy.sdk.http;


import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by alless on 2018/8/4.
 * Description:用来存储请求参数。
 */

public class Request {
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String FILE_CONTENT_TYPE = "application/octet-stream"; //字节流，上传文件
    public static final String JSON_CONTENT_TYPE = "application/json";
    private String url;
    private Type requestType;//get ,post
    private HashMap<String, String> paramMap;//保存键值对
    private ResponseListener listener;
    private String contentType;
    private HashMap<String, String> cookieInfo ;
    private String filePath;//优先级高于data.
    private byte[] data = null;

    public enum Type {
        POST, GET
    }

    public Request(Builder builder) {
        url = builder.url;
        requestType = builder.requestType;
        if (requestType == null) {
            requestType = Type.GET;
        }
        paramMap = builder.paramMap;
        listener = builder.listener;
        contentType = builder.contentType;
        if(TextUtils.isEmpty(contentType))
            contentType = DEFAULT_CONTENT_TYPE;
        filePath = builder.filePath;
        data = builder.data;
        cookieInfo = builder.cookieInfo;
        handleParams();
    }

    private void handleParams() {
        if (paramMap.size() > 0) {
            String param = getAssemblyParam(paramMap);
            Log.e("Request","param = "+param);
            switch (requestType){
                case GET:
                    url = url+"?"+param;
                    break;
                case POST:
                    data = param.getBytes();
                    break;

            }
        }

    }

    private String getAssemblyParam(HashMap<String, String> hashMap) {
        String params = "";
        Set<Map.Entry<String, String>> entrySet = hashMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            String value = entry.getValue();

                params += key + "=" + value + "&";
/*                if("imgUrl".equals(key)){//url不能再编码
                    params += key + "=" + value + "&";
                }else {
                    params += key + "=" + URLEncoder.encode(value, "utf-8") + "&";
                }*/


        }
        return params.substring(0, params.length()-1);
    }

    public Builder newBuilder() {
        return new Builder();
    }

    public String getUrl() {
        return url;
    }

    public Type getRequestType() {
        return requestType;
    }

    public HashMap<String, String> getParamMap() {
        return paramMap;
    }

    public ResponseListener getListener() {
        return listener;
    }

    public String getContentType() {
        return contentType;
    }

    public HashMap<String, String> getCookieInfo() {
        return cookieInfo;
    }

    public byte[] getData() {
        return data;
    }
    public String getFilePath() {
        return filePath;
    }

    public static class Builder {
        private String url;
        private Type requestType;
        private HashMap<String, String> paramMap;//保存键值对
        private ResponseListener listener;
        private String contentType;
        private HashMap<String, String> cookieInfo ;
        private byte[] data = null;
        private String filePath;

        public Builder() {
            paramMap = new HashMap<>();
            cookieInfo = new HashMap<>();
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setRequestType(Type requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder setParamMap(HashMap<String, String> paramMap) {
            this.paramMap = paramMap;
            return this;
        }

        public Builder setListener(ResponseListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setCookieInfo(HashMap<String, String> cookieInfo) {
            this.cookieInfo = cookieInfo;
            return this;
        }

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }


        public Request build() {
            return new Request(this);
        }

    }
}
