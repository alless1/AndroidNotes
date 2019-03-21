package com.speed.vpnsocks.test.core;

import android.print.PageRange;
import android.text.TextUtils;
import android.util.Log;

import com.speed.master.utils.ErrorCode;
import com.speed.vpnsocks.test.bean.SocksInfo;
import com.speed.vpnsocks.test.bean.SpeedTestReport;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;
import com.speed.vpnsocks.test.listener.ISpeedTestListener;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public class SpeedTestSocket implements Runnable {
    private static final String TAG = "SpeedTestSocket";
    private String mUrl;
    private SpeedTestRequest.TestType mTestType;
    private SocksInfo mSocksInfo;
    private String mSaveFilePath;
    private ISpeedTestListener mListener;
    private boolean isProxy;
    private String mUploadFilePath;
    private String mHost;
    private int mPort;
    private boolean mIsDownload;
    private volatile boolean isTerminated = false;
    private final int TEST_TIME_LIMIT = 5 * 1000;//5s
    private final int TEST_TIME_OUT_MAX = 10 * 1000;//5s
    private final int SET_SO_TIMEOUT = 5 * 1000;
    private final int CONNECT_TIME_OUT = 3 * 1000;
    private final int SOCKET_SEND_BUFFER_SIZE = 1024*128;
    private final int SOCKET_RECEIVE_BUFFER_SIZE = 1024*128;
    private final int READ_BUFFER_SIZE = 1024*64;//下载
    private final int WRITE_BUFFER_SIZE = 1024*64;//上传



    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            isTerminated = true;
        }
    };
    private TimerTask mTimeOutTask = new TimerTask() {
        @Override
        public void run() {
            if(mListener!=null){
                mListener.onCompletion(null);
            }
            mListener = null;
        }
    };
    private long mStartTime;


    public SpeedTestSocket(SpeedTestRequest request) {
        mUrl = request.getUrl();
        mTestType = request.getTestType();
        mSocksInfo = request.getSocksInfo();
        mSaveFilePath = request.getSaveFilePath();
        mUploadFilePath = request.getUploadFilePath();
        mListener = request.getListener();
        mTimer = new Timer();

    }

    public void start() {

        if (!checkParams()) {
            return;
        }

        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;

        try {

            URL url = new URL(mUrl);
            mHost = url.getHost();
            mPort = url.getPort();
            if (mPort == -1)
                if (mUrl.contains("https")) {
                    mPort = 443;
                } else {
                    mPort = 80;
                }


            if (mSocksInfo == null || TextUtils.isEmpty(mSocksInfo.getAddress()) || TextUtils.isEmpty(mSocksInfo.getUser())) {
                //走普通
                socket = getSocket(mHost, mPort);
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } else {
                //走代理
                socket = getSocket(mSocksInfo.getAddress(), mSocksInfo.getPort());
                is = socket.getInputStream();
                os = socket.getOutputStream();
                int error = chooseAuthentication(is, os, mSocksInfo.getUser(), mSocksInfo.getPassword());
                if(error==ErrorCode.OK){
                    sendTargetAddress(mUrl, is, os);
                    isProxy = true;
                }else {
                    if (mListener != null)
                        mListener.onError(error, "socks5 verify fail");
                    mListener = null;
                    return;
                }
/*                if (chooseAuthentication(is, os, mSocksInfo.getUser(), mSocksInfo.getPassword())) {
                    sendTargetAddress(mUrl, is, os);
                    isProxy = true;
                } else {
             *//*       socket = getSocket(mHost, mPort);
                    isProxy = false;*//*
                    if (mListener != null)
                        mListener.onError(ErrorCode.CRN_FAIL, "socks5 verify fail");
                    mListener = null;
                }*/
            }


            if (mListener != null)
                mListener.onPrepare();

            startTimer();

            switch (mTestType) {
                case DOWNLOAD:
                    downloadFile(mUrl, is, os);
                    break;
                case UPLOAD:
                    uploadFile(mUrl, mUploadFilePath, is, os);
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null)
                mListener.onError(ErrorCode.TEST_CONNECT_TIME_OUT, e.getMessage());
            mListener = null;
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
                Log.e(TAG, "Input close error");
            }
        }


    }

    private void startTimer() {
        mTimer.schedule(mTimerTask, TEST_TIME_LIMIT);
        mTimer.schedule(mTimeOutTask,TEST_TIME_OUT_MAX);
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(mUrl))
            return false;

/*        if (mIsDownload && TextUtils.isEmpty(mSaveFilePath))
            return false;
        if (!mIsDownload && TextUtils.isEmpty(mUploadFilePath))
            return false;*/
        return true;
    }

    private Socket getProxySocket(String proxyIp, int proxyPort) throws IOException {
        Socket socket = new Socket(proxyIp, proxyPort);
        socket.setSoTimeout(SET_SO_TIMEOUT);
        return socket;
    }

    private Socket getLocalSocket(String ip, int port) throws IOException {
        Socket s = new Socket();
        s.setSoTimeout(SET_SO_TIMEOUT);
        s.connect(new InetSocketAddress(ip, port));
        return s;
    }

    private Socket getSocket(String ip, int port) throws IOException {
        Socket socket = null;
        if (mUrl.contains("https")) {
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            socket = socketFactory.createSocket();
        } else {
            socket = new Socket();
        }
        socket.setSendBufferSize(SOCKET_SEND_BUFFER_SIZE);
        socket.setReceiveBufferSize(SOCKET_RECEIVE_BUFFER_SIZE);
        socket.connect(new InetSocketAddress(ip, port), CONNECT_TIME_OUT);
        socket.setSoTimeout(SET_SO_TIMEOUT);
        return socket;
    }

    private int chooseAuthentication(InputStream is, OutputStream os, String user, String password) throws IOException {
        byte[] req = new byte[]{0x05, 0x01, 0x02};//05协议版本，01一种方式，02用户名和密码
        os.write(req);
        os.flush();
        int version = is.read();//05,协议版本
        int METHOD = is.read();//02,用户名和密码方式
        //Mylog.d(TAG, "chooseAuthentication: version =" + version + "method =" + METHOD);
        Log.e(TAG, "chooseAuthentication: version ="+version+ "METHOD="+METHOD );
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
                return ErrorCode.OK;
            } else {
                Log.e(TAG, "socks5 verify fail status =" + status);
                return ErrorCode.CRN_VERIFY_FAIL;
            }

        } else {
            return ErrorCode.CRN_HANDSHAKE_FAIL;
        }
    }

    private void sendTargetAddress(String url, InputStream is, OutputStream os) throws IOException {

        if (isIpv4(mHost)) {
            // 传ipv4的方式
            byte[] connReq = new byte[]{0x05, 0x01, 0x00, 0x01};
            os.write(connReq);
            byte[] hostIp = InetAddress.getByName(mHost).getAddress();
            os.write(hostIp);

        } else {
            // 传域名的方式
            int domainLen = mHost.length();
            byte[] connReq = new byte[]{0x05, 0x01, 0x00, 0x03};
            os.write(connReq);
            os.write((byte) domainLen);
            os.write(mHost.getBytes());
        }


        short port = (short) mPort;
        byte[] portBytes = new byte[2];
        portBytes[0] = (byte) (port >> 8);
        portBytes[1] = (byte) (port & 0x00ff);
        os.write(portBytes);
        os.flush();

        Log.e(TAG, "sendTargetAddress: mHost = "+mHost+" mPort ="+mPort );

       /* byte[] connRep = new byte[4];
        is.read(connRep);
        System.out.println("reponse socks version is " + connRep[0]);
        if (connRep[1] == 0x00) {
           // System.out.println("reply is success");
            Log.d(TAG, "reply is success" );
        } else {
            Log.e(TAG, "Reply failed! code:"+connRep[1] );
            //System.out.println("Reply failed! code: " + connRep[1]);
        }
        System.out.println("address type is " + connRep[3]);
        if (connRep[3] == 0x03) {
            System.out.println("address type is domain");
        } else if (connRep[3] == 0x01) {
            System.out.println("address type is ipv4");
        } else if (connRep[3] == 0x04) {
            System.out.println("address type is ipv6");
        }

        byte[] ipbytes = new byte[4];
        is.read(ipbytes);
        printIp(ipbytes);

        byte[] portbytes = new byte[2];
        is.read(portbytes);
        printPort(portbytes);*/

    }
    private static void printIp(byte[] ipbytes) {
        for (byte b : ipbytes) {
            System.out.print(b + ".");
        }
    }
    private static void printPort(byte[] portbytes) {
        short port = (short) ((portbytes[0] << 8) | (portbytes[1]));
        System.out.println(port);
    }

    private boolean isIpv4(String host) {
        char[] chars = host.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (".0123456789".indexOf(String.valueOf(chars[i])) == -1) {
                return false;
            }
        }
        return true;
    }

    private void downloadFile(String url, InputStream is, OutputStream os) {
        mStartTime = System.currentTimeMillis();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GET " + url + " HTTP/1.1\r\n");
        stringBuffer.append("Host:" + mHost + "\r\n\r\n");
        try {
            os.write(stringBuffer.toString().getBytes());
            os.flush();
            File file = new File(mSaveFilePath, "testDown.zip");//mUrl.substring(mUrl.lastIndexOf("/") + 1)
            if (file.exists())
                file.delete();
            FileOutputStream dos = new FileOutputStream(file);

            DataInputStream dataInputStream = new DataInputStream(is);
            int allLength = 0;
            int count, offset = 0;
            byte[] buffer = new byte[READ_BUFFER_SIZE];
            boolean eohFound = false;
            int bodyLength = 0;
            //Log.e(TAG, "downloadFile: 1" );
            while ((count = dataInputStream.read(buffer)) != -1) {
                //Log.e(TAG, "downloadFile: 2" );
                //Log.e(TAG, "downloadFile: count ="+count );
                offset = 0;
                if (!eohFound) {
                    String stringHeader = new String(buffer, 0, count);
                    Log.e(TAG, "downloadFile: stringHeader = " + stringHeader);
                    int indexOfEOH = stringHeader.indexOf("\r\n\r\n");

                    if (indexOfEOH != -1) {
                        //处理包头
                        String content = "CONTENT-LENGTH: ";
                        int start = stringHeader.toUpperCase().indexOf(content);
                        String contentLength = stringHeader.substring(start + content.length());
                        int end = contentLength.indexOf("\r\n");
                        String bodyLengthString = contentLength.substring(0, end); //包体长度
                        bodyLength = Integer.parseInt(bodyLengthString);
                        Log.d(TAG, "bodyLength =" + bodyLength);
                        count = count - indexOfEOH - 4;
                        offset = indexOfEOH + 4;
                        eohFound = true;
                    } else {
                        count = 0;
                    }
                }
                allLength += count;

               /* dos.write(buffer, offset, count);
                dos.flush();*/


                if (mListener != null && bodyLength != 0) {
                    mListener.onProgress(bodyLength, allLength);
                    //Log.e(TAG, "downloadFile: bodyLength = "+bodyLength+"allLength ="+allLength);
                    if (allLength == bodyLength || isTerminated) {
                        Log.d(TAG, "下载完成 downloadFile: bodyLength = " + bodyLength + "allLength =" + allLength);
                        long endTime = System.currentTimeMillis();
                        SpeedTestReport report = new SpeedTestReport();
                        report.setStartTime(mStartTime);
                        report.setEndTime(endTime);
                        report.setFileLocalPath(file.getAbsolutePath());
                        report.setFileSize(bodyLength);
                        report.setRequestUrl(mUrl);
                        report.setProxy(isProxy);
                        mListener.onCompletion(report);
                        mListener = null;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null)
                mListener.onError(ErrorCode.DOWNLOAD_READ_TIME_OUT, e.getMessage());
            mListener = null;
        }

    }


    private void uploadFile(String netUrl, String filePath, InputStream is, OutputStream os) {


        File file = new File(filePath);
        if (!file.exists()) {
            if (mListener != null)
                mListener.onError(ErrorCode.FILE_FAIL, "uploadFile: file no exist");
            mListener = null;
            Log.e(TAG, "uploadFile: file no exist");
            return;
        }


        mStartTime = System.currentTimeMillis();

        //Log.e(TAG, "uploadFile: 1" );

        try {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("POST " + netUrl + " HTTP/1.1");
            stringBuilder.append("\r\n");
            stringBuilder.append("Host: " + mHost);
            stringBuilder.append("\r\n");
            // stringBuilder.append("Content-Type: application/octet-stream;");
            stringBuilder.append("Content-Disposition: form-data; name=abcd; filename=" + file.getName());
            stringBuilder.append("\r\n");
            stringBuilder.append("Content-Length: " + file.length());
            stringBuilder.append("\r\n");
            stringBuilder.append("Accept: text/html, application/xhtml+xml, application/xml, */*");
            stringBuilder.append("\r\n");
            stringBuilder.append("Connection:keep-Alive");
            stringBuilder.append("\r\n\r\n");
            os.write(stringBuilder.toString().getBytes("UTF-8"));

            int len;
            byte[] buffer = new byte[WRITE_BUFFER_SIZE];
            FileInputStream input = new FileInputStream(file);
            int totalLen = 0;
            while ((len = input.read(buffer)) != -1) {
                //Log.e(TAG, "uploadFile: 2" );
                os.write(buffer, 0, len);
                //Log.e(TAG, "uploadFile: len ="+len );
                //Log.e(TAG, "uploadFile: 3" );
                totalLen += len;
                if (mListener != null)
                    mListener.onProgress((int) file.length(), totalLen);
                if (isTerminated)
                    break;
            }
            os.flush();

            //Log.e(TAG, "uploadFile: 4" );
            if (isTerminated) {
                //上传文件到时间了不用管响应
                if (mListener != null) {
                    long endTime = System.currentTimeMillis();
                    SpeedTestReport report = new SpeedTestReport();
                    report.setStartTime(mStartTime);
                    report.setEndTime(endTime);
                    report.setFileLocalPath(file.getAbsolutePath());
                    report.setFileSize((int) file.length());
                    report.setRequestUrl(mUrl);
                    report.setProxy(isProxy);
                    mListener.onCompletion(report);
                    mListener = null;
                    Log.e(TAG, "uploadFile: castTime ="+(endTime-mStartTime) );
                    return;
                }
            }

            /*读取响应*/

            DataInputStream dataInputStream = new DataInputStream(is);
            int allLength = 0;
            int count, offset = 0;
            //byte[] buffer2 = new byte[1024 * 10];
            boolean eohFound = false;
            int bodyLength = 0;
            StringBuilder builder = new StringBuilder();
            while ((count = dataInputStream.read(buffer)) != -1) {
                offset = 0;
                if (!eohFound) {
                    String stringHeader = new String(buffer, 0, count);
                    int indexOfEOH = stringHeader.indexOf("\r\n\r\n");
                    if (indexOfEOH != -1) {
                        //处理包头
                        String content = "CONTENT-LENGTH: ";
                        int start = stringHeader.toUpperCase().indexOf(content);
                        String contentLength = stringHeader.substring(start + content.length());
                        int end = contentLength.indexOf("\r\n");
                        String bodyLengthString = contentLength.substring(0, end); //包体长度
                        bodyLength = Integer.parseInt(bodyLengthString);
                        Log.d(TAG, "bodyLength =" + bodyLength);
                        count = count - indexOfEOH - 4;
                        offset = indexOfEOH + 4;
                        eohFound = true;
                    } else {
                        count = 0;
                    }
                }

                allLength += count;
                builder.append(new String(buffer, offset, count, "utf-8"));
                Log.e(TAG, "上传响应 uploadFile: builder =" + builder);
                if (mListener != null && bodyLength != 0) {
                    //listener.onProgress(bodyLength, allLength);
                    if (allLength == bodyLength) {
                        long endTime = System.currentTimeMillis();
                        SpeedTestReport report = new SpeedTestReport();
                        report.setStartTime(mStartTime);
                        report.setEndTime(endTime);
                        report.setFileLocalPath(file.getAbsolutePath());
                        report.setFileSize(bodyLength);
                        report.setRequestUrl(mUrl);
                        report.setProxy(isProxy);
                        mListener.onCompletion(report);
                        mListener = null;
                        Log.d(TAG, "uploadFile: result = " + builder.toString());
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null)
                mListener.onError(ErrorCode.UPLOAD_READ_TIME_OUT, e.getMessage());
            mListener = null;
        }
    }

    @Override
    public void run() {
        start();
    }

    public static void main(String[] args) {
        String httpUrl = "http://test1-store.oss-cn-beijing.aliyuncs.com/5be246f0aa8fb0e42a273eb5.apk";//7m
        try {
            URL url = new URL(httpUrl);
            String host = url.getHost();
            int port = url.getPort();
            System.out.print("host = " + host + "port=" + port);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
