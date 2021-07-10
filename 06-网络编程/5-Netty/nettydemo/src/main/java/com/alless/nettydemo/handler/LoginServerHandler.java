package com.alless.nettydemo.handler;

import com.alless.nettydemo.dispatcher.IMPacketDispatcher;
import com.alless.nettydemo.utils.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LoginServerHandler extends ChannelInboundHandlerAdapter {
	protected boolean connected = false;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Logger.e("LoginServerHandler","channelActive");
		connected = true;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		IMPacketDispatcher.dispatch2((ByteBuf) msg);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		Logger.e("LoginServerHandler","channelUnregistered");//未连接上
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		Logger.e("LoginServerHandler",cause.getMessage());
	}


}
