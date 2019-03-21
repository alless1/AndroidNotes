package com.speed.vpnsocks.test.core;

import android.text.TextUtils;
import android.util.Log;

import com.speed.master.data.product.GlobalResp;
import com.speed.master.utils.SpUtils;
import com.speed.vpnsocks.test.bean.SocksInfo;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;
import com.speed.vpnsocks.test.listener.ISpeedTestListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chengjie on 2019/1/3.
 * Description:
 */

public abstract class SpeedTestBase implements Runnable {
    private static final String TAG = "SpeedTestBase";
    private SpeedTestRequest.TestType mTestType;
    protected SocksInfo mSocksInfo;
    protected String mUrl;
    protected String mSaveFilePath;
    protected String mUploadFilePath;
    protected ISpeedTestListener mListener;
    protected final int CONNECTION_TIMEOUT_SECOND = 30;//s 链接超时
    protected final int WRITE_TIMEOUT_SECOND = 30;//s
    protected final int READ_TIMEOUT_SECOND = 30;//s 读取超时

    protected final int TEST_TIME_LIMIT = 10 * 1000;//下载最大时间

    protected final int TEST_TIME_OUT_MAX = 13 * 1000;//辅助
    protected final int READ_BUFFER_SIZE = 1024 * 64;//下载buffer
    protected final int WRITE_BUFFER_SIZE = 1024 * 64;//上传buffer

    protected  int SOCKET_SEND_BUFFER_SIZE = 1024 * 64;
    protected  int SOCKET_RECEIVE_BUFFER_SIZE = 1024 * 64;
    protected  int LOCAL_WRITE_READ_BUFFER_SIZE = 1024 * 64;

    private int mDownloadTimeMax = 10*1000;

    private int mUploadTimeMax = 10*1000;

    protected int UPLOAD_SIZE_MAX = 3*1024*1024;

    protected final String UP_FILE_NAME = "android_test_up";//上传文件名
    protected volatile boolean isTerminated;
    protected long mStartTime;
    protected long mEndTime;
    private Timer mTimer;
    protected volatile boolean isCompletion;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            isTerminated = true;
        }
    };
    private TimerTask mTimeOutTask = new TimerTask() {
        @Override
        public void run() {
            onCompletion();
        }
    };



    @Override
    public void run() {
        start();
    }

    private void startTimer() {
        int time = mDownloadTimeMax ;
        switch (mTestType){
            case UPLOAD:
                time =  mUploadTimeMax;
                break;
            case DOWNLOAD:
                time =  mDownloadTimeMax;
                break;
        }
        mTimer.schedule(mTimerTask,time);
        mTimer.schedule(mTimeOutTask, time+2*1000);//延迟2s强制中断
    }

    private void cancelTimer() {
        mTimer.cancel();
    }

    protected void onPrepare(){
        if(mListener==null)
            return;
        mListener.onPrepare();
        startTimer();
        mStartTime = System.currentTimeMillis();
    }
    protected void onProgress(int total,int progress){
        if(mListener==null)
            return;
        if(isCompletion)
            return;
        mListener.onProgress(total,progress);
    }
    protected synchronized void onError(int error, String result) {
        if (!isCompletion && mListener!=null) {
            isCompletion = true;
            Log.e(TAG, "onError: " + error + result);
            mListener.onError(error, result);
            cancelTimer();
            //release();
        }
    }

    protected synchronized void onCompletion() {
        if (!isCompletion && mListener!=null) {
            isCompletion = true;
            mEndTime = System.currentTimeMillis();
            Log.d(TAG, "onCompletion: "+(mEndTime-mStartTime)/1000+ " s");
            mListener.onCompletion(null);
            cancelTimer();
            release();
        }
    }

    public SpeedTestBase(SpeedTestRequest request) {
        mUrl = request.getUrl();
        mTestType = request.getTestType();
        mSocksInfo = request.getSocksInfo();
        mSaveFilePath = request.getSaveFilePath();
        mUploadFilePath = request.getUploadFilePath();
        mListener = request.getListener();
        mTimer = new Timer();
        initData();


    }

    public void start() {
        initData();

        if (mSocksInfo == null || TextUtils.isEmpty(mSocksInfo.getAddress()) || TextUtils.isEmpty(mSocksInfo.getUser())) {
            initCommonClient();
        } else {
            initProxyClient();
        }

        if(isCompletion)
            return;

        switch (mTestType) {
            case DOWNLOAD:
                downloadFile();
                break;
            case UPLOAD:
                uploadFile();
                break;
        }

        release();
    }



    protected abstract void initProxyClient();

    protected abstract void initCommonClient();

    protected abstract void uploadFile();

    protected abstract void downloadFile();

    protected void initData(){
        GlobalResp global = SpUtils.getGlobal();
        if(global==null)
            return;
        int uploadSize = global.getUploadSize();
        UPLOAD_SIZE_MAX = uploadSize * 1024 * 1024 > UPLOAD_SIZE_MAX ? uploadSize * 1024 * 1024 : UPLOAD_SIZE_MAX;

        int downTime = global.getDownTime();
        int uploadTime = global.getUploadTime();
        mDownloadTimeMax = downTime * 1000 > mDownloadTimeMax ? downTime * 1000 : mDownloadTimeMax;
        mUploadTimeMax = uploadTime * 1000 > mUploadTimeMax ? uploadTime * 1000 : mUploadTimeMax;

        int sendBufferSize = global.getSendBuffer();
        SOCKET_SEND_BUFFER_SIZE = sendBufferSize * 1024 > SOCKET_SEND_BUFFER_SIZE ? sendBufferSize * 1024 : SOCKET_SEND_BUFFER_SIZE;
        int receiveBufferSize = global.getReceiveBuffer();
        SOCKET_RECEIVE_BUFFER_SIZE = receiveBufferSize * 1024 > SOCKET_RECEIVE_BUFFER_SIZE ? receiveBufferSize * 1024 : SOCKET_RECEIVE_BUFFER_SIZE;
        int localBufferSize = global.getReadBuffer();
        LOCAL_WRITE_READ_BUFFER_SIZE = localBufferSize * 1024 > LOCAL_WRITE_READ_BUFFER_SIZE ? localBufferSize * 1024 : LOCAL_WRITE_READ_BUFFER_SIZE;

        //Log.e(TAG, "initData: mDownloadTimeMax ="+mDownloadTimeMax + "mUploadTimeMax ="+ mUploadTimeMax+"SOCKET_SEND_BUFFER_SIZE="+SOCKET_SEND_BUFFER_SIZE +"SOCKET_RECEIVE_BUFFER_SIZE="+SOCKET_RECEIVE_BUFFER_SIZE+"LOCAL_WRITE_READ_BUFFER_SIZE="+LOCAL_WRITE_READ_BUFFER_SIZE );
    }

    protected void release(){}

    public boolean isIpv4(String host) {
        char[] chars = host.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (".0123456789".indexOf(String.valueOf(chars[i])) == -1) {
                return false;
            }
        }
        return true;
    }
}
