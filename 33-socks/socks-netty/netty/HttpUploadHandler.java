package com.network.vpnsocks.test.netty;

import android.util.Log;

import com.network.booster.utils.ErrorCode;

import java.nio.charset.Charset;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * Created by chengjie on 2019/2/13
 * Description:
 */
public class HttpUploadHandler extends ChannelInboundHandlerAdapter {
    private static final String TAG = "HttpUploadHandler";
    private boolean readingChunks = false;
    private int succCode = 200;
    private SpeedTestForNetty.OnHandlerCallback listener;

    public HttpUploadHandler(SpeedTestForNetty.OnHandlerCallback listener){
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.e(TAG, "channelRead: ");
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            Log.e(TAG, "channelRead: 1" );
            System.err.println("STATUS: " + response.status());
            System.err.println("VERSION: " + response.protocolVersion());

            if (!response.headers().isEmpty()) {
                for (CharSequence name : response.headers().names()) {
                    for (CharSequence value : response.headers().getAll(name)) {
                        System.err.println("HEADER: " + name + " = " + value);
                    }
                }
            }

            if (response.status().code() == 200 && HttpUtil.isTransferEncodingChunked(response)) {
                listener.onCompletion();
                readingChunks = true;
                System.err.println("CHUNKED CONTENT {");
            } else {
                System.err.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            Log.e(TAG, "channelRead: 2" );
            HttpContent chunk = (HttpContent) msg;
            System.err.println(chunk.content().toString(Charset.forName("UTF-8")));

            if (chunk instanceof LastHttpContent) {
                if (readingChunks) {
                    System.err.println("} END OF CHUNKED CONTENT");
                } else {
                    System.err.println("} END OF CONTENT");
                }
                readingChunks = false;
            } else {
                System.err.println(chunk.content().toString(Charset.forName("UTF-8")));
            }
        }
    }

    boolean isPrepare = false;
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
       // Log.e(TAG, "channelWritabilityChanged: " );
        //todo 从这里开始触发计数
        if(!isPrepare){
            isPrepare = true;
            listener.onPrepare();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Log.e(TAG, "channelReadComplete: " );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.channel().close();
        listener.onError(ErrorCode.NETTY_UPLOAD_FAIL,cause.getMessage());
        Log.e(TAG, "exceptionCaught: ", cause);
    }
}
