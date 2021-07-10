package com.alless.nettydemo.net;

import com.alless.nettydemo.handler.PacketEncoderHandler;
import com.alless.nettydemo.packet.BasePacket;
import com.alless.nettydemo.utils.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class SocketThread extends Thread {

    private EventLoopGroup mWorkerGroup = null;
    private Bootstrap mBootstrap = null;
    private ChannelFuture mChannelFuture = null;
    private String strHost = null;
    private int nPort = 0;




    public SocketThread(String strHost, int nPort, ChannelInboundHandlerAdapter handler) {

        this.strHost = strHost;

        this.nPort = nPort;

        init(handler);

    }



    private void init(final ChannelInboundHandlerAdapter handler) {
        mWorkerGroup = new NioEventLoopGroup();
        try {
            mBootstrap = new Bootstrap();
            mBootstrap.group(mWorkerGroup);
            mBootstrap.channel(NioSocketChannel.class);
            mBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            mBootstrap.option(ChannelOption.TCP_NODELAY, true);
            mBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new PacketEncoderHandler());
                    socketChannel.pipeline().addLast(handler);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            mWorkerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        try {
            //启动客户端
            mChannelFuture = mBootstrap.connect(strHost, nPort).sync();//这里会阻塞，连接成功进入下一步。失败，跳到catch,finally。
            //等待连接关闭
            mChannelFuture.channel().closeFuture().sync();//这里也会阻塞。
        } catch (Exception e) {
            Logger.e("SocketThread#run():",e.getMessage());
        } finally {
            mWorkerGroup.shutdownGracefully();
        }

    }


    public boolean sendPacket(BasePacket p) {
        if (mChannelFuture == null) {
            Logger.e("SocketThread","mChannelFuture == null");
            return false;
        }
        if (null != p && null != mChannelFuture.channel()) {
            mChannelFuture.channel().write(p);//用通道将消息发送出去
            Logger.e("SocketThread","sendPacket:ok");
            return true;
        } else {
            Logger.e("SocketThread","sendPacket:failed");
            return false;
        }

    }

}
