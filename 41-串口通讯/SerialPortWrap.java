package ubox.com.uboxsys.business.serial;

import android.text.TextUtils;
import android.util.Log;


import com.ubox.hal.serial.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import ubox.com.uboxsys.business.utils.ByteUtils;
import ubox.com.uboxsys.globel.utils.CrcUtils;
import ubox.com.uboxsys.log.LogInfo;


/**
 * Created by chengjie on 2020-04-08
 * Description:
 */
public class SerialPortWrap {
    private static final String TAG = "SerialPortWrap";
    private SerialReceiveCallback mCallback;
    private SerialPort mSerialPort;
    private Object mObject = new Object();
    private boolean isInterrupt = false;
    private LinkedBlockingQueue<String> mSendQueue = new LinkedBlockingQueue<>();
    //必选参数
    private String mFilePath;
    private int mBaudRate;
    private int mFlags;//默认0
    //可选参数
    private int DELAY_TIME = 200;//发送延迟时间
    private String HEAD_1 = "88";//协议头
    private int CRC_TYPE = 1;//校验类型
    private ByteBuffer mByteBuffer = ByteBuffer.allocate(1024);

    public void setDELAY_TIME(int DELAY_TIME) {
        this.DELAY_TIME = DELAY_TIME;
    }

    public void setHEAD_1(String HEAD_1) {
        this.HEAD_1 = HEAD_1;
    }

    public void setCRC_TYPE(int CRC_TYPE) {
        this.CRC_TYPE = CRC_TYPE;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        mByteBuffer = byteBuffer;
    }


    public SerialPortWrap(String filePath, int baudRate, int flags, SerialReceiveCallback callback) {
        mFilePath = filePath;
        mBaudRate = baudRate;
        mFlags = flags;
        mCallback = callback;
    }

    public synchronized boolean open() {
        if (mSerialPort != null)
            return true;
        File file = new File(mFilePath);
        if (!file.exists())
            return false;
        try {
            mSerialPort = new SerialPort(file, mBaudRate, mFlags);
            initThread();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return mSerialPort != null;
    }

    public void close() {
        if (mSerialPort != null)
            mSerialPort.close();
        mSerialPort = null;
        isInterrupt = true;
    }


    public void sendMsg(String msg) {
        mSendQueue.offer(msg);
    }

    public void startWriteThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isInterrupt()) {
                    try {
                        String take = mSendQueue.take();
                        writeMsg(take);
                        Thread.sleep(DELAY_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void writeMsg(String msg) {
        if (mSerialPort == null)
            return;
        if (TextUtils.isEmpty(msg))
            return;

        byte[] original = ByteUtils.hexStringToBytes(msg);
        final byte[] msgBytes = new byte[original.length + 2];//校验位
        System.arraycopy(original, 0, msgBytes, 0, original.length);
        // 计算CRC值，并将其填充至数组最后的两个字节
        CrcUtils.getCrc(CRC_TYPE, msgBytes);

        printLog("SerialPort => ComThread:" + ByteUtils.bytesToHexString(msgBytes));

        try {
            mSerialPort.getOutputStream()
                    .write(msgBytes);
        } catch (IOException e) {
            e.printStackTrace();
            if (mCallback != null)
                mCallback.onException(e);
        }

    }

    private void initThread() {

        startWriteThread();

        startReadThread();

        startProcessThread();
    }

    private void startReadThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSerialPort == null)
                    return;
                InputStream is = mSerialPort.getInputStream();
                if (is == null)
                    return;
                while (!isInterrupt() && mSerialPort != null) {

                    try {

                        byte[] comData = new byte[16];//目前的协议最长好像是十个字节。

                        int length = is.read(comData);//没有数据的时候会阻塞

                        printLog("ComThread: is.read length=" + length);

                        synchronized (mObject) {
                            writeByteBuffer(comData, length);
                            mObject.notifyAll();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (mCallback != null)
                            mCallback.onException(e);
                    }

                }
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startProcessThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isInterrupt() && mSerialPort != null) {
                    String result = null;
                    try {
                        synchronized (mObject) {
                            result = parseByteBuffer();
                            Log.d(TAG, "run: result=" + result);
                            if (TextUtils.isEmpty(result))
                                mObject.wait();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            processResult(result);
                        }
                    } catch (Exception e) {
                        printLog(e.getMessage());
                    }

                }

            }

        }).start();
    }

    private void processResult(String result) {
        printLog("SerialPort <= ComThread:" + result);
        if (mCallback != null)
            mCallback.onReceivedMsg(result);
    }

    private void writeByteBuffer(byte[] bytes, int len) {
        if (mByteBuffer.remaining() < len) {
            printLog("writeByteBuffer: error :mByteBuffer.remaining() =" + mByteBuffer.remaining() + " write len=" + len);
            return;
        }
        mByteBuffer.put(bytes, 0, len);
    }

    //16进制通讯方式解析
    private String parseByteBuffer() {
        printLog(String.format("parseByteBuffer: position=%s,remaining=%s,limit=%s,cap=%s", mByteBuffer.position(), mByteBuffer.remaining(), mByteBuffer.limit(), mByteBuffer.capacity()));
        mByteBuffer.flip();//进入读模式
        if (mByteBuffer.remaining() < 1) {
            mByteBuffer.compact();//进入写模式，不允许多次调用。
            //Log.d(TAG, "parseByteBuffer: 1");
            return "";
        }
        boolean hasHead = false;
        //找头88
        byte head1;
        do {
            mByteBuffer.mark();
            head1 = mByteBuffer.get();
            String headStr1 = ByteUtils.byte2HexStr(head1);
            if (HEAD_1.equals(headStr1)) {
                hasHead = true;
                break;
            }

        } while (mByteBuffer.remaining() > 0);

        if (hasHead) {//找到头，开始找完整包体，验证。
            if (mByteBuffer.remaining() < 1) {//找到头，回到头的位置，丢掉头之前的数据，切换到写模式
                mByteBuffer.reset();
                mByteBuffer.compact();
                printLog("parseByteBuffer: hasHead ");
                return "";
            }
            //找协议类型
            byte head2 = mByteBuffer.get();
            String type = ByteUtils.byte2HexStr(head2);//协议类型。

            if (mByteBuffer.remaining() < 1) {
                mByteBuffer.reset();
                mByteBuffer.compact();
                printLog("parseByteBuffer: hasHead hasType = " + type);
                return "";
            }
            //找包体长度
            byte head3 = mByteBuffer.get();
            String bodyLenStr = ByteUtils.byte2HexStr(head3);
            int bodyLen = ByteUtils.hexStr2Int(bodyLenStr);

            if (mByteBuffer.remaining() < bodyLen + 2) {//+2个校验位
                mByteBuffer.reset();
                mByteBuffer.compact();
                printLog("parseByteBuffer: hasHead hasType = " + type + " hasBodyLenHex =" + bodyLenStr);
                return "";
            }
            byte[] body = new byte[bodyLen + 2];
            mByteBuffer.get(body);

            byte[] allBytes = new byte[bodyLen + 2 + 3];
            allBytes[0] = head1;
            allBytes[1] = head2;
            allBytes[2] = head3;
            System.arraycopy(body, 0, allBytes, 3, bodyLen + 2);

            mByteBuffer.compact();

            String result = ByteUtils.bytesToHexString(allBytes);

            //校验 包体
            if (CrcUtils.checkCrc(CRC_TYPE, allBytes, allBytes.length)) {//校验成功
                return result;
            } else {
                printLog("parseByteBuffer: 协议校验失败 =" + result);
                return "";
            }

        } else {//没找到头，就丢掉当前数据，切换到写模式。
            mByteBuffer.compact();
            printLog("parseByteBuffer：没有找到协议头");
            return "";
        }

    }

    private boolean isInterrupt() {
        return isInterrupt;
    }

    public interface SerialReceiveCallback {
        void onReceivedMsg(String msg);

        void onException(Exception e);
    }

    private void printLog(String msg) {
        LogInfo.infos(msg);
    }

    public static void main(String[] args) {
        String sss = "88010200002364";
        System.out.println(sss.substring(2, 4));
        System.out.println(sss.substring(6, sss.length() - 4));
    }
}
