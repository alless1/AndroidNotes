package com.speed.vpnsocks.test.core;

import android.util.Log;

import com.speed.master.utils.ErrorCode;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by chengjie on 2019/1/3.
 * Description:
 */

public class SpeedTestForSocket extends SpeedTestBase {
    private static final String TAG = "SpeedTestForSocket";
    protected String mHost;
    protected int mPort;
    protected Socket mSocket;
    protected InputStream mIs;
    protected OutputStream mOs;


    public SpeedTestForSocket(SpeedTestRequest request) {
        super(request);
    }


    @Override
    protected void initData() {
        super.initData();
        try {
            URL url = new URL(mUrl);
            mHost = url.getHost();
            mPort = url.getPort();
            if (mPort == -1) {
                if (mUrl.startsWith("https")) {
                    mPort = 443;
                } else {
                    mPort = 80;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(ErrorCode.URL_EXCEPTION, e.getMessage());
        }

    }


    @Override
    protected void initProxyClient() {
        mSocket = getSocket(mSocksInfo.getAddress(), mSocksInfo.getPort());
        if (mSocket == null)
            return;
        try {
            if (chooseAuthentication(mIs, mOs, mSocksInfo.getUser(), mSocksInfo.getPassword())) {
                sendTargetAddress(mHost, mPort, mIs, mOs);
            }
        } catch (IOException e) {
            e.printStackTrace();
            onError(ErrorCode.OTHER_IO_EXCEPTION, e.getMessage());
        }
    }

    @Override
    protected void initCommonClient() {
        mSocket = getSocket(mHost, mPort);
    }

    private Socket getSocket(String ip, int port) {
        Socket socket = null;
        try {
            if (mUrl.contains("https")) {
                SocketFactory socketFactory = SSLSocketFactory.getDefault();
                socket = socketFactory.createSocket();

            } else {
                socket = new Socket();
            }
            socket.setSendBufferSize(SOCKET_SEND_BUFFER_SIZE);
            socket.setReceiveBufferSize(SOCKET_RECEIVE_BUFFER_SIZE);
            socket.connect(new InetSocketAddress(ip, port), CONNECTION_TIMEOUT_SECOND * 1000);
            socket.setSoTimeout(READ_TIMEOUT_SECOND * 1000);
            mIs = socket.getInputStream();
            mOs = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            socket = null;
            onError(ErrorCode.OTHER_IO_EXCEPTION, e.getMessage());
        }
        return socket;
    }

    private boolean chooseAuthentication(InputStream is, OutputStream os, String user, String password) throws IOException {
        byte[] req = new byte[]{0x05, 0x01, 0x02};//05协议版本，01一种方式，02用户名和密码
        os.write(req);
        os.flush();
        int version = is.read();//05,协议版本
        int METHOD = is.read();//02,用户名和密码方式
        //Mylog.d(TAG, "chooseAuthentication: version =" + version + "method =" + METHOD);
        Log.d(TAG, "chooseAuthentication: version =" + version + "METHOD=" + METHOD);
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
                return true;
            } else {
                Log.e(TAG, "socks5 verify fail status =" + status);
                onError(ErrorCode.CRN_VERIFY_FAIL, "socks5 verify fail status =" + status);
                return false;
            }

        } else {
            Log.e(TAG, "socks5 handshake fail METHOD =" + METHOD);
            onError(ErrorCode.CRN_VERIFY_FAIL, "socks5 handshake fail METHOD =" + METHOD);
            return false;
        }
    }

    private void sendTargetAddress(String host, int dport, InputStream is, OutputStream os) throws IOException {

        if (isIpv4(host)) {
            // 传ipv4的方式
            byte[] connReq = new byte[]{0x05, 0x01, 0x00, 0x01};
            os.write(connReq);
            byte[] hostIp = InetAddress.getByName(mHost).getAddress();
            os.write(hostIp);

        } else {
            // 传域名的方式
            int domainLen = host.length();
            byte[] connReq = new byte[]{0x05, 0x01, 0x00, 0x03};
            os.write(connReq);
            os.write((byte) domainLen);
            os.write(host.getBytes());
        }


        short port = (short) dport;
        byte[] portBytes = new byte[2];
        portBytes[0] = (byte) (port >> 8);
        portBytes[1] = (byte) (port & 0x00ff);
        os.write(portBytes);
        os.flush();


        byte[] connRep = new byte[4];
        is.read(connRep);
        Log.d(TAG, "sendTargetAddress:response socks version is " + connRep[0]);
    }

    @Override
    protected void uploadFile() {
/*        File file = new File(mUploadFilePath);
        if (!file.exists()) {
            onError(ErrorCode.FILE_FAIL, "uploadFile: file no exist");
            return;
        }*/

        onPrepare();

        try {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("POST " + mUrl + " HTTP/1.1");
            stringBuilder.append("\r\n");
            stringBuilder.append("Host: " + mHost);
            stringBuilder.append("\r\n");
            stringBuilder.append("Content-Type: application/octet-stream;");
            //stringBuilder.append("Content-Disposition: form-data; name=speedTest; filename=" + "android_upload.zip");
            stringBuilder.append("\r\n");
            stringBuilder.append("Content-Length: " + UPLOAD_SIZE_MAX);
            stringBuilder.append("\r\n");
            stringBuilder.append("Accept: text/html, application/xhtml+xml, application/xml, */*");
            stringBuilder.append("\r\n");
            stringBuilder.append("Connection:keep-Alive");
            stringBuilder.append("\r\n\r\n");
            mOs.write(stringBuilder.toString().getBytes("UTF-8"));

            int len;
            byte[] buffer = new byte[LOCAL_WRITE_READ_BUFFER_SIZE];
            int totalLen = 0;
/*            FileInputStream input = new FileInputStream(file);
            int fileLength = (int) file.length();
*//*            while ((len = input.read(buffer)) != -1) {
                mOs.write(buffer, 0, len);
                totalLen += len;
                onProgress(fileLength, totalLen);
                if (isTerminated)
                    break;
            }*/
            while (totalLen < UPLOAD_SIZE_MAX) {
                mOs.write(buffer, 0, LOCAL_WRITE_READ_BUFFER_SIZE);
                totalLen += LOCAL_WRITE_READ_BUFFER_SIZE;
                onProgress(UPLOAD_SIZE_MAX, totalLen);
                //Log.e(TAG, "uploadFile: "+totalLen);
                if (isTerminated)
                    break;
            }
            mOs.flush();

            if (isTerminated) {
                //上传文件到时间了不用管响应
                if (mListener != null) {
                  /*  long endTime = System.currentTimeMillis();
                    SpeedTestReport report = new SpeedTestReport();
                    report.setStartTime(mStartTime);
                    report.setEndTime(endTime);
                    report.setFileLocalPath(file.getAbsolutePath());
                    report.setFileSize((int) file.length());
                    report.setRequestUrl(mUrl);
                    report.setProxy(isProxy);
                    mListener.onCompletion(report);
                    mListener = null;
                    Log.e(TAG, "uploadFile: castTime ="+(endTime-mStartTime) );*/
                    onCompletion();
                    return;
                }
            }

            /*读取响应*/

            DataInputStream dataInputStream = new DataInputStream(mIs);
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
                        try {
                            bodyLength = Integer.parseInt(bodyLengthString);
                        }catch (Exception e){
                            e.printStackTrace();
                            break;
                        }
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
                //Log.e(TAG, "上传响应 uploadFile: builder =" + builder);
                if (mListener != null && bodyLength != 0) {
                    //listener.onProgress(bodyLength, allLength);
                    if (allLength == bodyLength) {
    /*                    long endTime = System.currentTimeMillis();
                        SpeedTestReport report = new SpeedTestReport();
                        report.setStartTime(mStartTime);
                        report.setEndTime(endTime);
                        report.setFileLocalPath(file.getAbsolutePath());
                        report.setFileSize(bodyLength);
                        report.setRequestUrl(mUrl);
                        report.setProxy(isProxy);
                        mListener.onCompletion(report);
                        mListener = null;
                        Log.d(TAG, "uploadFile: result = " + builder.toString());*/

                        break;
                    }
                }

            }
            onCompletion();

        } catch (Exception e) {
            e.printStackTrace();
            onError(ErrorCode.UPLOAD_READ_TIME_OUT, e.getMessage());
        }
    }

    @Override
    protected void downloadFile() {
        onPrepare();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GET " + mUrl + " HTTP/1.1\r\n");
        stringBuffer.append("Host:" + mHost + "\r\n\r\n");
        try {
            mOs.write(stringBuffer.toString().getBytes());
            mOs.flush();
  /*          File file = new File(mSaveFilePath, "testDown.zip");//mUrl.substring(mUrl.lastIndexOf("/") + 1)
            if (file.exists())
                file.delete();
            FileOutputStream dos = new FileOutputStream(file);*/

            DataInputStream dataInputStream = new DataInputStream(mIs);
            int allLength = 0;
            int count, offset = 0;
            byte[] buffer = new byte[LOCAL_WRITE_READ_BUFFER_SIZE];
            boolean eohFound = false;//找包头
            int bodyLength = 0;
            //Log.e(TAG, "downloadFile: 1" );
            while ((count = dataInputStream.read(buffer)) != -1) {
                //Log.e(TAG, "downloadFile: 2" );
                offset = 0;
                if (!eohFound) {
                    String stringHeader = new String(buffer, 0, count);
                    //Log.e(TAG, "downloadFile: stringHeader = " + stringHeader);
                    int indexOfEOH = stringHeader.indexOf("\r\n\r\n");

                    if (indexOfEOH != -1) {

                        //处理响应码
                        String subCode ="";
                        if(stringHeader.contains("HTTP/1.1 ")){
                            subCode = "HTTP/1.1 ";//HTTP/1.1 200 OK
                        }else {
                            subCode = "HTTP/1.0 ";//HTTP/1.0 502 Bad Gateway
                        }
                        String stringHeader1 = stringHeader.substring(stringHeader.indexOf(subCode) + subCode.length());
                        int code = Integer.parseInt(stringHeader1.substring(0, 3));
                        Log.e(TAG, "downloadFile: responseCode = "+code );
                        if(code != 200){
                            onError(ErrorCode.DOWNLOAD_ON_RESPONSE_FAIL,stringHeader.substring(0,indexOfEOH));//协议头上传
                            return;
                        }
                        //处理包头
                        String content = "CONTENT-LENGTH: ";
                        int start = stringHeader.toUpperCase().indexOf(content);
                        String contentLength = stringHeader.substring(start + content.length());
                        int end = contentLength.indexOf("\r\n");
                        String bodyLengthString = contentLength.substring(0, end); //包体长度
                        try {
                            bodyLength = Integer.parseInt(bodyLengthString);
                        }catch (Exception e){
                            onError(ErrorCode.DOWNLOAD_ON_RESPONSE_FAIL,stringHeader.substring(0,indexOfEOH));
                            return;
                        }

                        Log.d(TAG, "bodyLength =" + bodyLength);
                        count = count - indexOfEOH - 4;
                        offset = indexOfEOH + 4;
                        eohFound = true;
                    } else {
                        count = 0;
                    }
                }
                allLength += count;
                //Log.e(TAG, "downloadFile:count = "+count );
               /* dos.write(buffer, offset, count);
                dos.flush();*/

                if (mListener != null && bodyLength != 0) {
                    // mListener.onProgress(bodyLength, allLength);
                    onProgress(bodyLength, allLength);
                    //Log.e(TAG, "downloadFile: bodyLength = "+bodyLength+"allLength ="+allLength);
                    if (allLength == bodyLength || isTerminated) {
                        Log.d(TAG, "下载完成 downloadFile: bodyLength = " + bodyLength + "allLength =" + allLength);
                      /*  long endTime = System.currentTimeMillis();
                        SpeedTestReport report = new SpeedTestReport();
                        report.setStartTime(mStartTime);
                        report.setEndTime(endTime);
                        report.setFileLocalPath(file.getAbsolutePath());
                        report.setFileSize(bodyLength);
                        report.setRequestUrl(mUrl);
                        //report.setProxy(isProxy);
                        mListener.onCompletion(report);
                        mListener = null;*/
                       // onCompletion();
                        break;
                    }
                }
            }
            onCompletion();
        } catch (Exception e) {
            e.printStackTrace();
            onError(ErrorCode.DOWNLOAD_READ_TIME_OUT, e.getMessage());
        }
    }

    @Override
    protected void release() {
        super.release();
        try {
            if (mIs != null)
                mIs.close();
            if (mOs != null)
                mOs.close();
            if (mSocket != null)
                mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }
}
