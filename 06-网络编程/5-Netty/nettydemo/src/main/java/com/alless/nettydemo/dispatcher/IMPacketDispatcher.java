package com.alless.nettydemo.dispatcher;

import com.alless.nettydemo.DataBuffer;
import com.alless.nettydemo.manager.LoginManager;
import com.alless.nettydemo.packet.Header;
import com.alless.nettydemo.utils.Logger;

import io.netty.buffer.ByteBuf;

/**
 * Created by ${程杰} on 2017/9/7.
 * 描述:
 */

public class IMPacketDispatcher {
    private static boolean isFisrst = true;
    private static boolean isRepeat = false;
    private static int pbLength = 0;
    private static int msgType = -1;
    private static byte isSuccess = 0;
    public static DataBuffer sDataBuffer1;//完整的包
    public static DataBuffer sDataBuffer3 = new DataBuffer();//剩余的数据

    public static void dispatch2(ByteBuf byteBuf) {
        if (byteBuf == null) {
            if (!isRepeat) {
                //logger.e("packet#channelBuffer is null");
                return;
            }
            isRepeat = false;

        } else {
            //将接收到的包直接放入缓存区
            sDataBuffer3.writeDataBuffer(new DataBuffer(byteBuf));
        }
        sDataBuffer1 = null;
        //判断是否是包头开始
        if (isFisrst) {
            Header header = new Header();
            //解析包头，去掉包头，获得Pblength
            header.decode(sDataBuffer3);
            sDataBuffer3.discardReadBytes();
            msgType = header.getMsgType();//具体协议。
            isSuccess = header.getbRet();
            pbLength = header.getPbLenth();//包体长度
        }

        //直到缓存区凑够一个包长，取出一个包长
        if (sDataBuffer3.readableBytes() >= pbLength) {
            sDataBuffer1 = new DataBuffer();
            sDataBuffer1.writeBytes(sDataBuffer3.readBytes(pbLength));
            //从缓存区去掉这个包
            sDataBuffer3.discardReadBytes();
        }

        //是否取出了一个完整的包，如果是就分配消息，如果不是就设置false,下次循环
        if (sDataBuffer1 == null) {
            isFisrst = false;
        } else {
            isFisrst = true;
            //发包
            switch (msgType) {
                //登陆
                case 641:
                    // LoginManager.instance().onLoginRsp(isSuccess, pbLength, mDataBuffer);
                    if(isSuccess==0){
                        LoginManager.instance().onLoginRsp(sDataBuffer1);
                    }
                    Logger.e("登录结果", "isSuccess=" + isSuccess);
                    break;
                //退出
                case 642:
                    // LoginManager.instance().onLoginRsp(isSuccess, pbLength, mDataBuffer);
                    Logger.e("退出结果", "isSuccess=" + isSuccess);
                    break;
                default:
                    break;
            }

            //有可能还有包，再读一遍
            if (sDataBuffer3.readableBytes() >= 18) {
                isRepeat = true;
                dispatch2(null);
            }
        }

    }




}
