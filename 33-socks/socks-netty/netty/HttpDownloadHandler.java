package com.network.vpnsocks.test.netty;

import android.os.Environment;
import android.util.Log;

import com.network.booster.App;
import com.network.booster.utils.ErrorCode;

import java.io.File;
import java.io.FileOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;


/**
 * Created by chengjie on 2019/2/13
 * Description:
 */
public class HttpDownloadHandler extends ChannelInboundHandlerAdapter {
    private static final String TAG = "HttpDownloadHandler";
    private boolean readingChunks = false; // 分块读取开关
   // private FileOutputStream fOutputStream = null;// 文件输出流
    private File localfile = null;// 下载文件的本地对象
    //private String local = null;// 待下载文件名
    private int succCode;// 状态码
    private SpeedTestForNetty.OnHandlerCallback listener;

    public HttpDownloadHandler(SpeedTestForNetty.OnHandlerCallback listener) {
       this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.e(TAG, "channelActive: " );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof HttpResponse) {// response头信息
            HttpResponse response = (HttpResponse) msg;
            succCode = response.getStatus().code();
            if (succCode == 200) {
                //setDownLoadFile();// 设置下载文件
                readingChunks = true;
                //todo 从这里开始触发计数
                listener.onPrepare();
            }else {
                Log.e(TAG, "channelRead: 响应错误码 "+succCode );
                listener.onError(ErrorCode.NETTY_DOWNLOAD_FAIL,""+succCode);
            }
            // System.out.println("CONTENT_TYPE:"
            // + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));
        }
        if (msg instanceof HttpContent) {// response体信息
            HttpContent chunk = (HttpContent) msg;
            if (chunk instanceof LastHttpContent) {
                readingChunks = false;
            }

            ByteBuf buffer = chunk.content();
            byte[] dst = new byte[buffer.readableBytes()];
            if (succCode == 200) {
                while (buffer.isReadable()) {
                    buffer.readBytes(dst);
                    //fOutputStream.write(dst);
                    buffer.release();
                    //Log.e(TAG, "channelRead: down" );
                }
          /*      if (null != fOutputStream) {
                    fOutputStream.flush();
                }*/
            }

        }
        if (!readingChunks && succCode ==200) {
/*            if (null != fOutputStream) {
                System.out.println("Download done->" + localfile.getAbsolutePath());
                fOutputStream.flush();
                fOutputStream.close();
                localfile = null;
                fOutputStream = null;

            }*/
            ctx.channel().close();
            listener.onCompletion();
        }
    }

    /**
     * 配置本地参数，准备下载
     */
/*    private void setDownLoadFile() throws Exception {
        if (null == fOutputStream) {

            String cachePath = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = App.getInstance().getExternalCacheDir().getPath();
            } else {
                cachePath = App.getInstance().getCacheDir().getPath();
            }
            Log.e(TAG, "setDownLoadFile: "+cachePath );
            localfile = new File(cachePath, "testDown.zip");
            fOutputStream = new FileOutputStream(localfile);
        }
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        super.exceptionCaught(ctx,cause);
        Log.e(TAG, "exceptionCaught: ",cause );
        ctx.channel().close();
        listener.onError(ErrorCode.NETTY_DOWNLOAD_FAIL,cause.getMessage());
    }
}
