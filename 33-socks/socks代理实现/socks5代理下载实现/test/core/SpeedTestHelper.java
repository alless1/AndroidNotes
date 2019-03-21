package com.speed.vpnsocks.test.core;

import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.speed.master.http.HttpConfig;
import com.speed.vpnsocks.test.bean.ETestType;
import com.speed.vpnsocks.test.bean.SocksInfo;
import com.speed.vpnsocks.test.bean.SpeedTestReport;
import com.speed.vpnsocks.test.bean.SpeedTestRequest;
import com.speed.vpnsocks.test.listener.IPingTestListener;
import com.speed.vpnsocks.test.listener.ISpeedTestListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.speed.vpnsocks.test.bean.ETestType.FINISH;
import static com.speed.vpnsocks.test.bean.ETestType.NONE;


/**
 * Created by chengjie on 2019/1/4
 * Description:
 */
public class SpeedTestHelper {
    private static final String TAG = "SpeedTestHelper";
    public static final int MSG_SUCCESS = 0;
    public static final int MSG_DELAY_NEXT = 2;
    public static final int MSG_FAIL = 1;
    public static final int MSG_PROGRESS = 3;
    public static final int MSG_RATE = 4;
    public static final int MSG_PING = 5;
    public static final int MSG_PREPARE = 6;

    private String downloadUrl;
    private String uploadUrl;

    private boolean isRunning;
    private SpeedTestRequest mSpeedTestRequest;
    private SocksInfo mSocksInfo;
    private OnTestTaskListener mOnTestTaskListener;
    private static boolean isMultiThread = false;//okHttp无法使用多线程
    private volatile static int prepareCount = 0;
    private volatile static int completionCount = 0;
    private volatile static int errorCount = 0;
    private volatile static boolean thisTimePrepare;
    private volatile static boolean throwError;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private long startRunTime;
    private int mPrepareTrafficRxKbs;//初始值
    private int mPrepareTrafficTxKbs;//初始值
    private int mLastTrafficRxKbs;//下行
    private int mLastTrafficTxKbs;//上行
    private Lock mLock = new ReentrantLock();

    public enum TestModel {Socket, SocketP, OkHttp, HttpUrlConnection;}

    private TestModel mTestModel = TestModel.Socket;

    private List<Integer> mSpeedRateList = new ArrayList<>();

    private ExecutorService mThreadExecutor = Executors.newCachedThreadPool();

    public SpeedTestHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//502 Bad Gateway
            mTestModel = TestModel.SocketP;
            isMultiThread = true;
        } else {
            mTestModel = TestModel.Socket;
            isMultiThread = true;
        }


        downloadUrl = HttpConfig.BASE_REQUEST_UP_DOWN + HttpConfig.REQUEST_REQUEST_TEST_DOWN;
        uploadUrl = HttpConfig.BASE_REQUEST_UP_DOWN + HttpConfig.REQUEST_REQUEST_TEST_UP;
    }

    private class RateTimerTask extends TimerTask {

        @Override
        public void run() {
            //Log.e(TAG, "run: RateTimerTask" );
            int speed = getIntervalSpeed();//间隔速度
            mSpeedRateList.add(speed);
            int progress = 0;
            switch (mCurrentType) {
                case NORMAL_DOWN:
                case PROXY_DOWN:
                    progress = getTotalRxKbs() - mPrepareTrafficRxKbs;
                    break;
                case NORMAL_UP:
                case PROXY_UP:
                    progress = getTotalTxKbs() - mPrepareTrafficTxKbs;
                    break;
            }
            sendMessageProgress(progress, speed);
        }
    }


    private ETestType mCurrentType = NONE;

    private IPingTestListener mPingTestListener = new IPingTestListener() {
        @Override
        public void onProgress(int instantRTT) {
            sendMessageProgress(0, instantRTT);

        }

        @Override
        public void onCompletion(int avgRT) {
            //sendMessagePing(avgRT);
            sendMessageSuccess(5, avgRT);
        }
    };


    private class MySpeedTestListener implements ISpeedTestListener {

        @Override
        public void onPrepare() {
            mLock.lock();
            if (countPrepareMulti()) {
                if (mTimerTask != null)
                    mTimerTask.cancel();
                mSpeedRateList.clear();

                mLastTrafficRxKbs = getTotalRxKbs();
                mLastTrafficTxKbs = getTotalTxKbs();
                mPrepareTrafficRxKbs = getTotalRxKbs();
                mPrepareTrafficTxKbs = getTotalTxKbs();

                startRunTime = System.currentTimeMillis();
                mTimerTask = new RateTimerTask();
                mTimer.schedule(mTimerTask, 1000, 1000);
                sendMessagePrepare();
            }
            mLock.unlock();
        }

        @Override
        public void onCompletion(SpeedTestReport report) {

            mLock.lock();
            if (countCompletionMulti()) {
                if (mTimerTask != null)
                    mTimerTask.cancel();
                long time = (System.currentTimeMillis() - startRunTime) / 1000;
                sendMessageSuccess((int) time, countSpeedRate());
            }
            mLock.unlock();

        }

        @Override
        public void onProgress(int total, int progress) {
            //不从这里计算
        }

        @Override
        public void onError(int errorCode, String result) {
            Log.e(TAG, "onError: " + errorCode + " result =" + result);
            mLock.lock();
            if (countErrorMulti()) {
                if (mTimerTask != null)
                    mTimerTask.cancel();
                sendMessageFail(errorCode, result);
            }

            if (onErrorCompletionMulti()) {
                if (mTimerTask != null)
                    mTimerTask.cancel();
                long time = (System.currentTimeMillis() - startRunTime) / 1000;
                sendMessageSuccess((int) time, countSpeedRate());
            }
            mLock.unlock();
        }
    }

    private static synchronized boolean countPrepareMulti() {
        prepareCount++;
        Log.e(TAG, "onPrepare: prepareCount =" + prepareCount);
        if (throwError)
            return false;
        if (!isMultiThread || prepareCount == 4) {
            return true;
        } else {
            return false;
        }
    }

    private static synchronized boolean countCompletionMulti() {
        completionCount++;
        Log.e(TAG, "onCompletion: " + completionCount);
        if (throwError)
            return false;
        if (!isMultiThread || completionCount == 4 || completionCount + errorCount == 4) {
            return true;
        } else {
            return false;
        }
    }

    private static synchronized boolean countErrorMulti() {
        errorCount++;
        Log.e(TAG, "countErrorMulti: " + errorCount);
        if (!isMultiThread || errorCount == 4) {
            throwError = true;
            return true;
        } else {
            return false;
        }
    }

    private static synchronized boolean onErrorCompletionMulti() {
        if (throwError)
            return false;
        if (!isMultiThread)
            return false;
        if (completionCount > 0 && completionCount + errorCount == 4) {
            return true;
        } else {
            return false;
        }
    }

    private int countSpeedRate() {
        Collections.sort(mSpeedRateList);
        Collections.reverse(mSpeedRateList);
        Log.e(TAG, "countSpeedRate1: " + mSpeedRateList.toString());
        int size = mSpeedRateList.size();
        int halfSize = mSpeedRateList.size() / 2;//在一半的时候开始判断.
        //int maxSize = mSpeedRateList.size() / 10;//去掉一个最高的.
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (size == 0) {
            return 0;
        } else if (size == 1) {
            return mSpeedRateList.get(0);
        } else if (size == 2) {
            return (mSpeedRateList.get(0) + mSpeedRateList.get(1)) / 2;
        } else {
            for (int i = 1; i < size - 1; i++) {//去掉最高的和最低的,去掉相差50%的
                if (i >= halfSize && mSpeedRateList.get(i) * 3 / 2 < mSpeedRateList.get(i - 1)) {
                    break;
                }
                arrayList.add(mSpeedRateList.get(i));
            }
        }

        Log.e(TAG, "countSpeedRate2: " + arrayList.toString());
        if (arrayList.size() == 0)
            return 0;
        int total = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            total += arrayList.get(i);
        }
        return total / arrayList.size();
    }


    private ISpeedTestListener mTestListener1 = new MySpeedTestListener();
    private ISpeedTestListener mTestListener2 = new MySpeedTestListener();
    private ISpeedTestListener mTestListener3 = new MySpeedTestListener();
    private ISpeedTestListener mTestListener4 = new MySpeedTestListener();

    private void resetCount() {
        prepareCount = 0;
        completionCount = 0;
        errorCount = 0;
        thisTimePrepare = false;
    }

    private Handler mSpeedTestHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (mOnTestTaskListener == null)
                return;
            switch (msg.what) {
                case MSG_DELAY_NEXT:
                    nextStep();
                    break;
                case MSG_PREPARE:
                    mOnTestTaskListener.onPrepare(mCurrentType);
                    break;
                case MSG_PROGRESS:
                    mOnTestTaskListener.onProgress(msg.arg1, msg.arg2);
                    break;
                case MSG_SUCCESS:
                    mOnTestTaskListener.onCompletion(msg.arg1, msg.arg2);
                    delayedNext();
                    break;
                case MSG_FAIL:
                    mOnTestTaskListener.onError(msg.arg1, (String) msg.obj);
                    break;

            }

        }
    };

    public void start(SpeedTestRequest speedTestRequest, OnTestTaskListener taskListener) {
        if (isRunning)
            return;
        isRunning = true;
        if (!checkInfo(speedTestRequest))
            return;
        mSpeedTestRequest = speedTestRequest;
        mSocksInfo = mSpeedTestRequest.getSocksInfo();
        mOnTestTaskListener = taskListener;

        mCurrentType = NONE;
        throwError = false;
        delayedNext();

    }


    private void delayedNext() {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_DELAY_NEXT;
        mSpeedTestHandler.sendMessageDelayed(message, 1000);
    }

    private boolean checkInfo(SpeedTestRequest speedTestRequest) {
        return true;
    }

    public void interrupt() {
        isRunning = false;
        throwError = true;
        mOnTestTaskListener = null;
        mThreadExecutor.shutdown();
    }




    private void setMultiThread(boolean b) {
        isMultiThread = b;
    }

    private void setTestModel(TestModel model) {
        mTestModel = model;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void executePing() {
        sendMessagePrepare();
        mThreadExecutor.submit(new PingTest(mSocksInfo.getAddress(), 5, mPingTestListener));
    }

    private void executeTask(SpeedTestRequest.TestType type, SocksInfo socksInfo, String url) {
        if (isMultiThread) {
            mThreadExecutor.submit(switchImp(assembleTask(type, socksInfo, url, mTestListener1)));
            mThreadExecutor.submit(switchImp(assembleTask(type, socksInfo, url, mTestListener2)));
            mThreadExecutor.submit(switchImp(assembleTask(type, socksInfo, url, mTestListener3)));
            mThreadExecutor.submit(switchImp(assembleTask(type, socksInfo, url, mTestListener4)));
        } else {
            mThreadExecutor.submit(switchImp(assembleTask(type, socksInfo, url, mTestListener1)));
        }
    }

    private Runnable switchImp(SpeedTestRequest request) {
        switch (mTestModel) {
            case OkHttp:
                return new SpeedTestForOkHttp(request);
            case HttpUrlConnection:
                return new SpeedTestForHttp(request);
            case SocketP:
                return new SpeedTestForSocketP(request);
            case Socket:
            default:
                return new SpeedTestForSocket(request);
        }
    }

    private SpeedTestRequest assembleTask(SpeedTestRequest.TestType type, SocksInfo socksInfo, String url, ISpeedTestListener listener) {
        SpeedTestRequest testRequest = new SpeedTestRequest();
        testRequest.setTestType(type);
        testRequest.setUrl(url);
        testRequest.setSocksInfo(socksInfo);
        testRequest.setListener(listener);
        //setCommonInfo(testRequest);
        return testRequest;
    }


    private void setCommonInfo(SpeedTestRequest request) {
        request.setDownloadTimeMax(mSpeedTestRequest.getDownloadTimeMax());
        request.setUploadTimeMax(mSpeedTestRequest.getUploadTimeMax());
        request.setSocketSendBufferSize(mSpeedTestRequest.getSocketSendBufferSize());
        request.setSocketReceiveBufferSize(mSpeedTestRequest.getSocketReceiveBufferSize());
        request.setLocalWriteReadBufferSize(mSpeedTestRequest.getLocalWriteReadBufferSize());
        request.setSaveFilePath(mSpeedTestRequest.getSaveFilePath());
        request.setUploadFilePath(mSpeedTestRequest.getUploadFilePath());
    }


    private void nextStep() {
        if (throwError)
            return;
        Log.e(TAG, "nextStep: mCurrentType 1 =" + mCurrentType);
        resetCount();
        switch (mCurrentType) {
            case NONE:
                mCurrentType = ETestType.NORMAL_DOWN;
                executeTask(SpeedTestRequest.TestType.DOWNLOAD, null, downloadUrl);
                break;
            case NORMAL_DOWN:
                mCurrentType = ETestType.NORMAL_UP;
                executeTask(SpeedTestRequest.TestType.UPLOAD, null, uploadUrl);
                break;
            case NORMAL_UP:
                mCurrentType = ETestType.NORMAL_PING;
                executePing();
                break;
            case NORMAL_PING:
                mCurrentType = ETestType.PROXY_DOWN;
                executeTask(SpeedTestRequest.TestType.DOWNLOAD, mSocksInfo, downloadUrl);
                break;
            case PROXY_DOWN:
                mCurrentType = ETestType.PROXY_UP;
                executeTask(SpeedTestRequest.TestType.UPLOAD, mSocksInfo, uploadUrl);
                break;
            case PROXY_UP:
                mCurrentType = ETestType.PROXY_PING;
                executePing();
                break;
            case PROXY_PING:
                mCurrentType = FINISH;
                if (mOnTestTaskListener != null) {
                    mOnTestTaskListener.onPrepare(FINISH);
                }
                isRunning = false;
                Log.e(TAG, "nextStep: 测试流程结束!");
                break;
        }
        Log.e(TAG, "nextStep: mCurrentType 2 =" + mCurrentType);
    }

    public interface OnTestTaskListener {
        void onPrepare(ETestType type);

        void onCompletion(int times, int speedKs);

        void onRate(int ks);

        void onProgress(int progress, int speedKs);

        void onError(int errorCode, String result);
    }

    private void sendMessagePrepare() {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_PREPARE;
        mSpeedTestHandler.sendMessage(message);
    }

    private void sendMessageSuccess(int timeS, int averageSpeed) {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_SUCCESS;
        message.arg1 = timeS;
        message.arg2 = averageSpeed;
        mSpeedTestHandler.sendMessage(message);
    }

    private void sendMessageFail(int error, String result) {
        isRunning = false;
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_FAIL;
        message.arg1 = error;
        message.obj = result;
        mSpeedTestHandler.sendMessage(message);

    }

    private void sendMessageProgress(int progress, int speedKs) {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_PROGRESS;
        message.arg1 = progress;
        message.arg2 = speedKs;
        mSpeedTestHandler.sendMessage(message);
    }

/*    private void sendMessageRate(int ks) {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_RATE;
        message.arg1 = ks;
        mSpeedTestHandler.sendMessage(message);
    }*/

    private void sendMessagePing(int ping) {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_PING;
        message.arg1 = ping;
        mSpeedTestHandler.sendMessage(message);
    }

    private int getTotalRxKbs() {
        return (int) (TrafficStats.getTotalRxBytes() / 1024);
    }

    private int getTotalTxKbs() {
        return (int) (TrafficStats.getTotalTxBytes() / 1024);
    }


    private int getIntervalSpeed() {
        switch (mCurrentType) {
            case NORMAL_DOWN:
            case PROXY_DOWN:
                return getDownSpeed();
            case NORMAL_UP:
            case PROXY_UP:
                return getUpSpeed();
        }
        return getDownSpeed();
    }

    private int getDownSpeed() {
        int current = getTotalRxKbs();
        int speed = current - mLastTrafficRxKbs;
        if (speed < 0)
            speed = 0;
        mLastTrafficRxKbs = current;
        return speed;
    }

    private int getUpSpeed() {
        int current = getTotalTxKbs();
        int speed = current - mLastTrafficTxKbs;
        if (speed < 0)
            speed = 0;
        mLastTrafficTxKbs = current;
        return speed;
    }
}