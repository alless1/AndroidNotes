package com.network.vpnsocks.test.core;

import android.text.TextUtils;
import android.util.Log;

import com.android.commonlib.utils.L;
import com.network.booster.data.product.GlobalResp;
import com.network.booster.utils.SpUtils;
import com.network.vpnsocks.test.bean.SocksInfo;
import com.network.vpnsocks.test.bean.SpeedTestRequest;
import com.network.vpnsocks.test.listener.ISpeedTestListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chengjie on 2019/1/3.
 * Description:
 */

public abstract class SpeedTestBase implements Runnable {
    private static final String TAG = "SpeedTestBase";
    protected SpeedTestRequest.TestType mTestType;
    protected SocksInfo mSocksInfo;
    protected String mUrl;
    protected String mSaveFilePath;
    protected String mUploadFilePath;
    protected ISpeedTestListener mListener;
    protected final int CONNECTION_TIMEOUT_SECOND = 15 * 1000;//s 链接超时
    protected final int WRITE_TIMEOUT_SECOND = 15 * 1000;//s
    protected final int READ_TIMEOUT_SECOND = 15 * 1000;//s 读取超时


    protected int SOCKET_SEND_BUFFER_SIZE = 1024 * 64;
    protected int SOCKET_RECEIVE_BUFFER_SIZE = 1024 * 64;
    protected int LOCAL_WRITE_READ_BUFFER_SIZE = 1024 * 64;//读写buffer

    protected int mDownloadTimeMax = 10 * 1000;//下载最大时间

    protected int mUploadTimeMax = 10 * 1000;//上传最大时间

    public static int UPLOAD_SIZE_MAX = 10 * 1024 * 1024;//上传文件大小

    protected int EXTRA_TIME = 3 * 1000;
    protected int COUNT_NUM = 7;//3秒计算一次，最多计算COUNT_NUM次（20s）

    protected final String UP_FILE_NAME = "android_test_up";//上传文件名
    protected volatile boolean isTerminated;
    protected long mStartTime;
    protected long mEndTime;
    private Timer mTimer;
    private int mCountNum;
    protected volatile boolean isCompletion;
    protected volatile boolean isPrepare;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            isTerminated = true;
        }
    };
    private TimerTask mTimeOutTask = new TimerTask() {
        @Override
        public void run() {
//            if (SpeedTestHelper.sTestModel == SpeedTestHelper.TestModel.Netty) {
            mCountNum++;
//            L.e(" mTimeOutTask " + mCountNum);
            if (mCountNum > 2 && SpeedTestHelper.isSuccessCount()) {
                onCompletion();
            } else {
                if (mCountNum >= COUNT_NUM) {
                    //达到最大时间
                    onCompletion();
                }
            }
//            } else {
//                onCompletion();
//            }
        }
    };

    private TimerTask mPreTimerTask = new TimerTask() {
        @Override
        public void run() {
            if(!isPrepare){
                onPrepare();
            }
        }
    };


    @Override
    public void run() {
        process();
    }

    private void startTimer() {
        int time = mDownloadTimeMax;
        switch (mTestType) {
            case UPLOAD:
                time = mUploadTimeMax;
                break;
            case DOWNLOAD:
                time = mDownloadTimeMax;
                break;
        }
        //mTimer.schedule(mTimerTask, time);
//        if (SpeedTestHelper.sTestModel == SpeedTestHelper.TestModel.Netty) {
        mCountNum = 0;
        mTimer.schedule(mTimeOutTask, 0, EXTRA_TIME);
//        } else {
//        mTimer.schedule(mTimeOutTask, time + EXTRA_TIME);
//            //延迟3s强制中断,多线程上传文件可能会抛出socket closed异常,无妨。
//        }
    }

    private void cancelTimer() {
        mTimer.cancel();
    }

    protected synchronized void onPrepareBefore(){
        mTimer.schedule(mPreTimerTask,5*1000);
    }

    protected synchronized void onPrepare() {
        if (!isPrepare && mListener != null){
            isPrepare=true;
            mListener.onPrepare();
            startTimer();
            mStartTime = System.currentTimeMillis();
        }
    }

    protected void onProgress(int total, int progress) {
        if (mListener == null)
            return;
        if (isCompletion)
            return;
        mListener.onProgress(total, progress);
    }

    protected synchronized void onError(int error, String result) {
        if (!isCompletion && mListener != null) {
            isCompletion = true;
            L.e(TAG, "onError: " + error + result);
            mListener.onError(error, result);
            cancelTimer();
            release();
        }
    }

    protected synchronized void onCompletion() {
        if (!isCompletion && mListener != null) {
            isCompletion = true;
            mEndTime = System.currentTimeMillis();
            L.d(TAG, "onCompletion: " + (mEndTime - mStartTime) / 1000 + " s");
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
        initBaseData();

    }

    protected void process() {

        onPrepareBefore();//onPrepare();

        initData();

        if (mSocksInfo == null || TextUtils.isEmpty(mSocksInfo.getAddress()) || TextUtils.isEmpty(mSocksInfo.getUser())) {
            initCommonClient();
        } else {
            initProxyClient();
        }

        if (isCompletion)
            return;

        switch (mTestType) {
            case DOWNLOAD:
                downloadFile();
                break;
            case UPLOAD:
                uploadFile();
                break;
        }

    }


    protected abstract void initData();

    protected abstract void initProxyClient();

    protected abstract void initCommonClient();

    protected abstract void uploadFile();

    protected abstract void downloadFile();

    private void initBaseData() {
        GlobalResp global = SpUtils.getGlobal();
        if (global == null)
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

    protected void release() {
    }

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
