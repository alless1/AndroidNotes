package com.network.vpnsocks.test.netty;


import android.util.Log;

import com.android.commonlib.utils.L;
import com.network.vpnsocks.test.bean.SocksInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * Created by chengjie on 2019/2/14
 * Description:
 */
public class SpeedChannelInit extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final SocksInfo socksInfo;
    private final boolean isDownload;
    private final SpeedTestForNetty.OnHandlerCallback listener;

    public SpeedChannelInit(SslContext sslCtx, SocksInfo socksInfo, boolean isDownload, SpeedTestForNetty.OnHandlerCallback listener) {
        this.sslCtx = sslCtx;
        this.socksInfo = socksInfo;
        this.isDownload = isDownload;
        this.listener = listener;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addFirst(trafficHandler); //流量统计
        if (socksInfo != null){
            pipeline.addLast(new Socks5ProxyHandler(new InetSocketAddress(socksInfo.getAddress(), socksInfo.getPort()), socksInfo.getUser(), socksInfo.getPassword()));
        }
        if (sslCtx != null)
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        if (isDownload) {
            pipeline.addLast(new HttpDownloadHandler(listener));
        } else {
            pipeline.addLast(new HttpUploadHandler(listener));
        }

    }

//    private static final EventExecutorGroup EXECUTOR_GROUP = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2);
//    private static final GlobalTrafficShapingHandler trafficHandler = new GlobalTrafficShapingHandler(EXECUTOR_GROUP, Long.MAX_VALUE, Long.MAX_VALUE);
//    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//    static {
//        executorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                TrafficCounter trafficCounter = trafficHandler.trafficCounter();
////                L.d("writ " + trafficCounter.lastWriteThroughput());
////                L.d("Read " + trafficCounter.lastReadThroughput());
////                L.i("liao", "flow monitor:{} " + System.lineSeparator() + trafficCounter);
//                long speed = trafficCounter.lastWriteThroughput() + trafficCounter.lastReadThroughput();
//                L.w("speed = " + speed / 1024);
//            }
//        }, 0, 1, TimeUnit.SECONDS);
//    }
}
