package com.network.vpnsocks.test.core;

import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.commonlib.utils.L;
import com.android.commonlib.utils.ToastUtils;
import com.network.booster.App;
import com.network.booster.data.product.GlobalResp;
import com.network.booster.http.HttpConfig;
import com.network.booster.utils.Constants;
import com.android.commonlib.utils.LogSaveUtils;
import com.network.booster.utils.SpUtils;
import com.network.vpnsocks.test.bean.ETestType;
import com.network.vpnsocks.test.bean.SocksInfo;
import com.network.vpnsocks.test.bean.SpeedTestReport;
import com.network.vpnsocks.test.bean.SpeedTestRequest;
import com.network.vpnsocks.test.listener.IPingTestListener;
import com.network.vpnsocks.test.listener.ISpeedTestListener;
import com.network.vpnsocks.test.netty.SpeedTestForNetty;
import com.network.vpnsocks.test.utils.PathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.network.vpnsocks.test.bean.ETestType.FINISH;
import static com.network.vpnsocks.test.bean.ETestType.NONE;
import static com.network.vpnsocks.test.core.SpeedTestBase.UPLOAD_SIZE_MAX;


/**
 * Created by chengjie on 2019/1/4
 * Description:
 */
public class SpeedTestHelper {
    private static final String TAG = "SpeedTestHelper";
    //    private static final String TAG = "liao";
    public static final int MSG_SUCCESS = 0;
    public static final int MSG_FAIL = 1;
    public static final int MSG_DELAY_NEXT = 2;
    public static final int MSG_PROGRESS = 3;
    public static final int MSG_RATE = 4;
    public static final int MSG_PING = 5;
    public static final int MSG_PREPARE = 6;
    //最小速度，低于这个，算测试无效数据
    private static final int MIN_SPEED = 30;

    private String downloadUrl;
    private String uploadUrl;

    private boolean isRunning;
    private SocksInfo mSocksInfo;
    private OnTestTaskListener mOnTestTaskListener;
    private static boolean isMultiThread = false;//okHttp无法使用多线程
    private volatile static int prepareCount = 0;
    private volatile static int completionCount = 0;
    public volatile static int errorCount = 0;
    private volatile static boolean throwError;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private long startRunTime;
    private int mPrepareTrafficRxKbs;//初始值
    private int mPrepareTrafficTxKbs;//初始值
    private int mLastTrafficRxKbs;//下行
    private int mLastTrafficTxKbs;//上行
    private final int INTERVAL_TIME_MS = 1000;//毫秒
    private Lock mLock = new ReentrantLock();
    private String mUploadFilePath;

    public enum TestModel {Socket, SocketP, Netty}

    public static TestModel sTestModel;

    private static List<Integer> mSpeedRateList = new ArrayList<>();

    private ExecutorService mThreadExecutor = Executors.newCachedThreadPool();

    public SpeedTestHelper() {
        downloadUrl = HttpConfig.BASE_REQUEST_UP_DOWN + HttpConfig.REQUEST_REQUEST_TEST_DOWN;
        uploadUrl = HttpConfig.BASE_REQUEST_UP_DOWN + HttpConfig.REQUEST_REQUEST_TEST_UP;
        mSpeedRateList = new ArrayList<>();
    }

    private class RateTimerTask extends TimerTask {

        @Override
        public void run() {
            int speed = getIntervalFlow() * 1000 / INTERVAL_TIME_MS;//间隔速度
            synchronized (mSpeedRateList) {
                if (errorCount == 0 || errorCount == 4) {
                    mSpeedRateList.add(speed);
                } else {
                    mSpeedRateList.add(speed / (4 - errorCount) * 4);
                }
            }
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
                mTimer.schedule(mTimerTask, 1000, INTERVAL_TIME_MS);
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
            L.e(TAG, "onError: " + errorCode + " result =" + result);
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
        L.d(TAG, "onPrepare: prepareCount =" + prepareCount);
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
        L.d(TAG, "onCompletion: " + completionCount);
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
        L.d(TAG, "countErrorMulti: " + errorCount);
        if (!isMultiThread || errorCount == 4) {
            throwError = true;
            return true;
        } else {
            return false;
        }
    }

    private static synchronized boolean onErrorCompletionMulti() {
        L.e("onErrorCompletionMulti");
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

    /**
     * 测速平均值
     *
     * @return
     */
    private int countSpeedRate() {
        synchronized (mSpeedRateList) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            try {
                Collections.sort(mSpeedRateList);
                Collections.reverse(mSpeedRateList);
                int size = mSpeedRateList.size();
                int halfSize = mSpeedRateList.size() / 2;//在一半的时候开始判断.
                //int maxSize = mSpeedRateList.size() / 10;//去掉一个最高的.
                if (size < 5) {
                    arrayList.addAll(mSpeedRateList);
                } else {
                    for (int i = 2; i < size - 2; i++) {//去掉最高和最低的,去掉相差50%的
                        //前两个值异常偏大,不比较
                        if (i > 2 && mSpeedRateList.get(i) * 3 / 2 < mSpeedRateList.get(i - 1)) {
                            break;
                        }
                        if (mSpeedRateList.get(i) > MIN_SPEED) {
                            arrayList.add(mSpeedRateList.get(i));
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                //Log.d(TAG, "countSpeedRate2: " + arrayList.toString());
            }
            if (arrayList.size() == 0) {
                if (mSpeedRateList != null && mSpeedRateList.size() > 2) {
                    return mSpeedRateList.get(2);
                } else {
                    return 0;
                }
            }
            int total = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                total += arrayList.get(i);
//                L.e("count " + arrayList.get(i));
            }
            return total / arrayList.size();
        }
    }

    /**
     * 是否已经成功采样
     *
     * @return
     */
    public static boolean isSuccessCount() {
        synchronized (mSpeedRateList) {
            try {
                if (mSpeedRateList == null || mSpeedRateList.size() < 5) {
                    return false;
                }
                Collections.sort(mSpeedRateList);
                Collections.reverse(mSpeedRateList);
                int count = 0;
                for (int i = 2; i < mSpeedRateList.size() - 2; i++) {
                    //去掉最高两个和最低的,去掉相差50%的
                    if (i > 2 && mSpeedRateList.get(i) * 3 / 2 < mSpeedRateList.get(i - 1)) {
                        break;
                    }
                    if (mSpeedRateList.get(i) > MIN_SPEED) {
                        //低于MIN_SPEED的速度，认为不正常
                        count++;
                    }
                }
                if (count >= 5) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
    }


    private ISpeedTestListener mTestListener1 = new MySpeedTestListener();
    private ISpeedTestListener mTestListener2 = new MySpeedTestListener();
    private ISpeedTestListener mTestListener3 = new MySpeedTestListener();
    private ISpeedTestListener mTestListener4 = new MySpeedTestListener();

    private void resetCount() {
        prepareCount = 0;
        completionCount = 0;
        errorCount = 0;
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

    public void start(SocksInfo socksInfo, OnTestTaskListener taskListener) {
        if (isRunning)
            return;
        mSocksInfo = socksInfo;
        if (mSocksInfo == null)
            return;
        Constants.errorThreadNum = new StringBuffer();
        initModel();
        initUploadFile();
        mOnTestTaskListener = taskListener;
        isRunning = true;
        mCurrentType = NONE;
        throwError = false;
        delayedNext();
        Log.d(TAG, "start: sTestModel =" + sTestModel);
    }

    private void initModel() {
        if (sTestModel != TestModel.Netty) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                sTestModel = TestModel.SocketP;
            } else {
                sTestModel = TestModel.Socket;
            }
        }
        isMultiThread = true;
    }

    /* */

    /**
     * 配置任务类型和多线程
     *//*
    public void setTestModel(TestModel model, boolean isMultiThread) {
        if (isRunning)
            return;
        this.sTestModel = model;
        this.isMultiThread = isMultiThread;
        if(model==TestModel.Socket||model==TestModel.SocketP){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                sTestModel = TestModel.SocketP;//华为9.0的必须用这个，不然socks读不到数据
            } else {
                sTestModel = TestModel.Socket;
            }
        }

    }*/
    private void delayedNext() {
        Message message = mSpeedTestHandler.obtainMessage();
        message.what = MSG_DELAY_NEXT;
        mSpeedTestHandler.sendMessageDelayed(message, 1000);
    }


    public void interrupt() {
        isRunning = false;
        throwError = true;
        mOnTestTaskListener = null;
        mThreadExecutor.shutdown();
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
        switch (sTestModel) {
            case SocketP:
                return new SpeedTestForSocketP(request);
            case Netty:
                return new SpeedTestForNetty(request);
            case Socket:
            default:
                return new SpeedTestForSocket(request);
        }
    }

    private SpeedTestRequest assembleTask(SpeedTestRequest.TestType type, SocksInfo
            socksInfo, String url, ISpeedTestListener listener) {
        SpeedTestRequest testRequest = new SpeedTestRequest();
        testRequest.setTestType(type);
        testRequest.setUrl(url);
        testRequest.setSocksInfo(socksInfo);
        testRequest.setListener(listener);
        testRequest.setUploadFilePath(mUploadFilePath);
        return testRequest;
    }

    private void initUploadFile() {
        GlobalResp global = SpUtils.getGlobal();
        if (global != null) {
            int uploadSize = global.getUploadSize();
            UPLOAD_SIZE_MAX = uploadSize * 1024 * 1024 > UPLOAD_SIZE_MAX ? uploadSize * 1024 * 1024 : UPLOAD_SIZE_MAX;
        }
        mUploadFilePath = PathUtil.createEmptyFile(App.getInstance(), UPLOAD_SIZE_MAX);
    }

    private void nextStep() {
        if (throwError)
            return;
        int lastErrorNum = errorCount;
        resetCount();
        Log.d(TAG, "nextStep: mCurrentType =" + mCurrentType);
        switch (mCurrentType) {
            case NONE:
                mCurrentType = ETestType.NORMAL_DOWN;
                executeTask(SpeedTestRequest.TestType.DOWNLOAD, null, downloadUrl);
                break;
            case NORMAL_DOWN:
                Constants.errorThreadNum.append(lastErrorNum).append(",");
                mCurrentType = ETestType.NORMAL_UP;
                executeTask(SpeedTestRequest.TestType.UPLOAD, null, uploadUrl);
                break;
            case NORMAL_UP:
                Constants.errorThreadNum.append(lastErrorNum).append(",");
                mCurrentType = ETestType.NORMAL_PING;
                executePing();
                break;
            case NORMAL_PING:
                mCurrentType = ETestType.PROXY_DOWN;
                executeTask(SpeedTestRequest.TestType.DOWNLOAD, mSocksInfo, downloadUrl);
                break;
            case PROXY_DOWN:
                Constants.errorThreadNum.append(lastErrorNum).append(",");
                mCurrentType = ETestType.PROXY_UP;
                executeTask(SpeedTestRequest.TestType.UPLOAD, mSocksInfo, uploadUrl);
                break;
            case PROXY_UP:
                Constants.errorThreadNum.append(lastErrorNum).append(",");
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
    }

    public interface OnTestTaskListener {
        void onPrepare(ETestType type);

        void onCompletion(int times, int speedKs);

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


    private int getIntervalFlow() {
        switch (mCurrentType) {
            case NORMAL_DOWN:
            case PROXY_DOWN:
                return getDownFlow();
            case NORMAL_UP:
            case PROXY_UP:
                return getUpFlow();
        }
        return getDownFlow();
    }

    private int getDownFlow() {
        int current = getTotalRxKbs();
        int flow = current - mLastTrafficRxKbs;
        if (flow < 0)
            flow = 0;
        mLastTrafficRxKbs = current;
        return flow;
    }

    private int getUpFlow() {
        int current = getTotalTxKbs();
        int flow = current - mLastTrafficTxKbs;
        if (flow < 0)
            flow = 0;
        mLastTrafficTxKbs = current;
        return flow;
    }
}