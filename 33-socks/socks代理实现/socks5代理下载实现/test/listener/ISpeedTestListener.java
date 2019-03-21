package com.speed.vpnsocks.test.listener;

import com.speed.vpnsocks.test.bean.SpeedTestReport;

/**
 * Created by chengjie on 2018/11/29
 * Description:
 */
public interface ISpeedTestListener {
    void onPrepare();
    void onCompletion(SpeedTestReport report);

    void onProgress(int total, int progress);

    void onError(int errorCode,String result);
}
