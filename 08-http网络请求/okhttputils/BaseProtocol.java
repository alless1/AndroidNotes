package net.oschina.app.base;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import net.oschina.app.util.XmlUtils;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by alless on 2017/4/25.
 * 网络请求的protocol基类
 */

public abstract class BaseProtocol<RESPONSE> {
    String url = getUrl();
    protected int curIndex;
    private Map<String, String> mParamsMap;
    private Map<String, File> mFileMap;
    private static final String TAG = "BaseProtocol";

    /**
     * 设置当前请求的页数
     */
    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    /**
     * get请求方式获取数据
     */
    public void loadDataByGet(final CallBack<RESPONSE> callBack, final int reqType) {
        OkHttpUtils.get()
                .url(url)
                .headers(getHeaderMap())
                .params(getParamsMap())//?key=value
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //利用回调将结果传递给接口
                        callBack.onError(call,e,id,reqType);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        /**
                         * 获取传递的泛型类型!!!
                         */
                        Type type = ((ParameterizedType) BaseProtocol.this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                        RESPONSE result = XmlUtils.toBean((Class<RESPONSE>) type, response.getBytes());
                        callBack.onResponse(result,id,reqType);
                    }
                });
    }

    /**
     * post请求方式
     */
    public void loadDataByPost(final BaseProtocol.CallBack<RESPONSE> callBack, final int reqType) {
        PostFormBuilder builder = OkHttpUtils
                .post()
                .url(this.url);
        //获取文件map集合
        Map<String, File> files = getFileMap();
        if (files != null) {
            for(Map.Entry<String,File> entry :files.entrySet()){
                String key = entry.getKey();
                File file = entry.getValue();
                builder.addFile(key, file.getName(), file);
            }
        }
        builder.headers(getHeaderMap())
                .params(getParamsMap())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callBack.onError(call,e,id,reqType);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获取传递的泛型类型
                        Type type = ((ParameterizedType) BaseProtocol.this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                        RESPONSE result = XmlUtils.toBean((Class<RESPONSE>) type, response.getBytes());
                        callBack.onResponse(result,id,reqType);
                    }
                });
    }


    /**
     * 获取当前请求的Url
     * 子类必须实现
     * @return
     */
    protected abstract String getUrl();

    /**
     * 获取请求头信息
     * @return
     */
    protected Map<String,String> getHeaderMap() {
        return null;
    }

    /**
     * 获取请求的参数
     * @return
     */
    protected Map<String,String> getParamsMap() {
        return null;
    }

    /**
     * 获取上传文件集合
     * @return
     */
    protected Map<String,File> getFileMap() {
        return null;
    }

    //定义回调接口
    public interface CallBack<RESPONSE>{
        void onError(Call call, Exception e, int id, int reqType);

        void onResponse(RESPONSE response, int id, int reqType);
    }

}
