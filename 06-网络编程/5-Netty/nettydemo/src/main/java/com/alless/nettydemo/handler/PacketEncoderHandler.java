
package com.alless.nettydemo.handler;

import com.alless.nettydemo.DataBuffer;
import com.alless.nettydemo.packet.BasePacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * channel().write(p)之后数据会传递到ChannelOutboundHandler，编码加工
 */
public class PacketEncoderHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        BasePacket request = (BasePacket)msg;
        DataBuffer buffer = null;
        try {
            buffer = request.encode();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (null != buffer) {
            ctx.writeAndFlush(buffer.getOrignalBuffer());
        }
    }
/*
    *//**
     * @Description: 把要发送的数据编码成二进制数据并发送
     *//*
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        BasePacket request = (BasePacket) e.getMessage();
        DataBuffer buffer = null;
        try {
            buffer = request.encode();
        } catch (Exception e2) {
        	//Logger.getLogger(PacketEncoderHandler.class).e("packet#got exception:%s", e2.getMessage() == null ? "" : e2.getMessage());
        }
        if (null != buffer) {
            Channels.write(ctx, e.getFuture(), buffer.getOrignalBuffer());
        }
    }*/

}
