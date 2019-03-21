package com.speed.vpnsocks.test.core;

import com.speed.master.utils.ErrorCode;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;

/**
 * Created by chengjie on 2019/1/8
 * Description:
 */
public class SpeedTestForSocketP extends SpeedTestForSocket {
    public SpeedTestForSocketP(SpeedTestRequest request) {
        super(request);
    }

    @Override
    protected void initProxyClient() {
        InetSocketAddress mInetSocketAddress = new InetSocketAddress(mSocksInfo.getAddress(), mSocksInfo.getPort());
        Proxy mProxy = new Proxy(Proxy.Type.SOCKS, mInetSocketAddress);
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            private PasswordAuthentication authentication = new PasswordAuthentication(mSocksInfo.getUser(), mSocksInfo.getPassword().toCharArray());

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        });
        try {
            mSocket = new Socket(mProxy);
            mSocket.setSendBufferSize(SOCKET_SEND_BUFFER_SIZE);
            mSocket.setReceiveBufferSize(SOCKET_RECEIVE_BUFFER_SIZE);
            mSocket.connect(new InetSocketAddress(mHost, mPort), CONNECTION_TIMEOUT_SECOND * 1000);
            mSocket.setSoTimeout(READ_TIMEOUT_SECOND * 1000);
            mIs = mSocket.getInputStream();
            mOs = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            mSocket = null;
            onError(ErrorCode.OTHER_IO_EXCEPTION, e.getMessage());
        }
    }
}
