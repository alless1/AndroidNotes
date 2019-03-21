package com.network.vpnsocks.test.core;

import android.text.TextUtils;
import android.util.Log;

import com.network.booster.utils.ErrorCode;
import com.network.vpnsocks.test.bean.SocksInfo;
import com.network.vpnsocks.test.listener.CRNConnectionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chengjie on 2018/12/3
 * Description:
 */
public class CRNTest implements Runnable {
    private static final String TAG = "CRNTest";
    private SocksInfo mInfo;
    private CRNConnectionListener mListener;
    private final int CONNECT_TIME_OUT = 20000;
    private boolean isTimeOut = false;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {

        }
    };

    public CRNTest(SocksInfo info, CRNConnectionListener listener) {
        mInfo = info;
        mListener = listener;
    }

    @Override
    public void run() {
        verify();
    }

    private void verify() {
        String address = mInfo.getAddress();
        int port = mInfo.getPort();
        String user = mInfo.getUser();
        String password = mInfo.getPassword();

        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            //socket = new Socket(address, port);
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress, CONNECT_TIME_OUT);
            socket.setSoTimeout(CONNECT_TIME_OUT);
            is = socket.getInputStream();
            os = socket.getOutputStream();
/*            if(chooseAuthentication(is, os,user,password)){
                mListener.connectionSuccess(true);
            }else {
                mListener.connectionSuccess(false);
            }*/
            chooseAuthentication(is, os, user, password);

        } catch (Exception e) {
            e.printStackTrace();
            mListener.connectionSuccess(ErrorCode.CRN_CONNECT_TIME_OUT, e.getMessage());
        }

        try {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseAuthentication(InputStream is, OutputStream os, String user, String password) {
        try {
            byte[] req = new byte[]{0x05, 0x01, 0x02};//05协议版本，01一种方式，02用户名和密码
            os.write(req);

            os.flush();
            int version = is.read();//05,协议版本
            int METHOD = is.read();//02,用户名和密码方式
            //Mylog.d(TAG, "chooseAuthentication: version =" + version + "method =" + METHOD);

            if (METHOD == 0x02) {
                //校验
                byte[] buff = new byte[1024];
                buff[0] = 0x05;//协议版本
                buff[1] = (byte) user.length();
                System.arraycopy(user.getBytes("UTF-8"), 0, buff, 2, buff[1]);
                int pIndex;
                int pLenIndex;
                pIndex = buff[1] + 2;
                pLenIndex = pIndex + 1;
                buff[pIndex] = (byte) password.length();
                System.arraycopy(password.getBytes("UTF-8"), 0, buff, pLenIndex, buff[pIndex]);
                os.write(buff, 0, pLenIndex + buff[pIndex]);
                os.flush();

                int version1 = is.read();
                int status = is.read();

                if (status == 0x00) {
                    Log.d(TAG, "socks5 verify success");
                    mListener.connectionSuccess(ErrorCode.OK, "socks5 verify success");
                    //return true;
                } else {
                    Log.e(TAG, "socks5 verify fail status =" + status);
                    mListener.connectionSuccess(ErrorCode.CRN_VERIFY_FAIL, "socks5 verify fail version = " + version + " version1=" + version1 + " status =" + status);
                    //return false;
                }

            } else {
                Log.e(TAG, "chooseAuthentication:error METHOD =" + METHOD);
                mListener.connectionSuccess(ErrorCode.CRN_HANDSHAKE_FAIL, "chooseAuthentication:error METHOD =" + METHOD);
                //return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            mListener.connectionSuccess(ErrorCode.CRN_READ_TIME_OUT, e.getMessage());
            // return false;
        }
    }

}
