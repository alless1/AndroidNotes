package com.network.vpnsocks.test.netty;

import android.util.Log;

import com.network.booster.utils.ErrorCode;
import com.network.vpnsocks.test.bean.SocksInfo;
import com.network.vpnsocks.test.bean.SpeedTestRequest;
import com.network.vpnsocks.test.core.SpeedTestBase;

import java.io.File;
import java.net.URI;
import java.net.URL;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * Created by alless on 2019/2/12.
 * Description: 支持 http/https + ssl + socks
 */

public class SpeedTestForNetty extends SpeedTestBase {
    private static final String TAG = "liao";
    //private String mUrl = "https://downpack.baidu.com/appsearch_AndroidPhone_1012271b.apk";
    //private String mUrl = "http://acj3.pc6.com/pc6_soure/2019-1/com.ting.mp3.android_7011.apk";
    //private String mUrl = "https://suq.58haima.com/downFile?osType=1";
    //private String mUrl = "https://suq.58haima.com/upload/check";
    private String mHost = "";
    private int mPort = 0;

    private boolean mIsDownload;

    private SslContext mSslContext;
    private EventLoopGroup mWorkerGroup;
    private HttpDataFactory mFactory;

    private OnHandlerCallback mHandlerCallback = new OnHandlerCallback() {
        @Override
        public void onPrepare() {
            SpeedTestForNetty.this.onPrepare();
        }

        @Override
        public void onCompletion() {
            SpeedTestForNetty.this.onCompletion();
        }

        @Override
        public void onError(int error, String result) {
            SpeedTestForNetty.this.onError(error, result);
        }
    };
    private Channel mChannel;


    public SpeedTestForNetty(SpeedTestRequest request) {
        super(request);
    }

    @Override
    protected synchronized void onPrepare() {
        mDownloadTimeMax -= EXTRA_TIME;//因为netty的上传没办法控制进度，只能强制中断，时间就要缩短。
        mUploadTimeMax -= EXTRA_TIME;
        super.onPrepare();
    }

    @Override
    protected void initData() {
        try {
            URL url = new URL(mUrl);
            mHost = url.getHost();
            mPort = url.getPort();
            if (mUrl.startsWith("https")) {
                if (mPort == -1)
                    mPort = 443;
                mSslContext = SslContextBuilder.forClient().build();
            } else {
                if (mPort == -1)
                    mPort = 80;
                mSslContext = null;
            }
            if (mTestType == SpeedTestRequest.TestType.DOWNLOAD)
                mIsDownload = true;
//            Log.d(TAG, "initData: mHost =" + mHost + " mPort=" + mPort);
        } catch (Exception e) {
            Log.e(TAG, "SpeedTestForNetty: ", e);
            onError(0, e.getMessage());
        }
    }

    @Override
    protected void initProxyClient() {
        try {
            initClient(mSocksInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
            onError(ErrorCode.NETTY_INIT_FAIL, e.getMessage());
        }
    }

    @Override
    protected void initCommonClient() {
        try {
            initClient(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            onError(ErrorCode.NETTY_INIT_FAIL, e.getMessage());
        }
    }

    @Override
    protected void uploadFile() {
        try {
            //File file = new File(PathUtil.createEmptyFile(App.getInstance(), UPLOAD_SIZE_MAX));
            File file = new File(mUploadFilePath);
            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, new URI(mUrl).toASCIIString());
            request.headers().set(HttpHeaderNames.HOST, mHost);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(mFactory, request, true);
            bodyRequestEncoder.addBodyFileUpload("uploadFile", file, "application/octet-stream", false);
            request = bodyRequestEncoder.finalizeRequest();
            mChannel.writeAndFlush(request);
            if (bodyRequestEncoder.isChunked()) {
                mChannel.writeAndFlush(bodyRequestEncoder);
            }
            mChannel.closeFuture().sync();
        } catch (Exception e) {
            Log.e(TAG, "uploadFile: ", e);
            try {
                File file = new File(mUploadFilePath);
                HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, new URI(mUrl).toASCIIString());
                request.headers().set(HttpHeaderNames.HOST, mHost);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(mFactory, request, true);
                bodyRequestEncoder.addBodyFileUpload("uploadFile", file, "application/octet-stream", false);
                request = bodyRequestEncoder.finalizeRequest();
                mChannel.writeAndFlush(request);
                if (bodyRequestEncoder.isChunked()) {
                    mChannel.writeAndFlush(bodyRequestEncoder);
                }
                mChannel.closeFuture().sync();
            } catch (Exception e1) {
                Log.e(TAG, "uploadFile 1: ", e1);
                onError(ErrorCode.NETTY_UPLOAD_FAIL, e.getMessage());
            }
        } finally {
            release();
        }

    }


    @Override
    protected void downloadFile() {
        try {
            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, new URI(mUrl).toASCIIString());
            request.headers().set(HttpHeaderNames.HOST, mHost);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            mChannel.writeAndFlush(request);
            mChannel.closeFuture().sync();
        } catch (Exception e) {
            Log.e(TAG, "downloadFile: ", e);
            try {
                HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, new URI(mUrl).toASCIIString());
                request.headers().set(HttpHeaderNames.HOST, mHost);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                mChannel.writeAndFlush(request);
                mChannel.closeFuture().sync();
            } catch (Exception e1) {
                Log.e(TAG, "downloadFile 1: ", e);
                onError(ErrorCode.NETTY_DOWNLOAD_FAIL, e.getMessage());
            }
        } finally {
            release();
        }


    }

    private void initClient(SocksInfo socksInfo) throws InterruptedException {
        mWorkerGroup = new NioEventLoopGroup();
        mFactory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(mWorkerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, SOCKET_RECEIVE_BUFFER_SIZE)
                .option(ChannelOption.SO_SNDBUF, SOCKET_SEND_BUFFER_SIZE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_SECOND)
                .handler(new SpeedChannelInit(mSslContext, socksInfo, mIsDownload, mHandlerCallback));
        ChannelFuture future = bootstrap.connect(mHost, mPort).sync();
        mChannel = future.channel();
    }


    @Override
    protected void release() {
        if (mChannel != null && mChannel.isOpen())
            mChannel.close();
        if (mWorkerGroup != null)
            mWorkerGroup.shutdownGracefully();
        try {
            if (mFactory != null)
                mFactory.cleanAllHttpData();
        } catch (Exception e) {

        }

    }


    public interface OnHandlerCallback {

        void onPrepare();

        void onCompletion();

        void onError(int error, String result);
    }
}
