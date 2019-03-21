package com.speed.vpnsocks.test.bean;

import com.speed.vpnsocks.test.listener.ISpeedTestListener;

/**
 * Created by chengjie on 2019/1/4
 * Description:
 */
public class SpeedTestTask {
    private SocksInfo mSocksInfo;
    private TestConfigInfo mConfigInfo;
    private ISpeedTestListener mTestListener;

    public SocksInfo getSocksInfo() {
        return mSocksInfo;
    }

    public void setSocksInfo(SocksInfo socksInfo) {
        mSocksInfo = socksInfo;
    }

    public TestConfigInfo getConfigInfo() {
        return mConfigInfo;
    }

    public void setConfigInfo(TestConfigInfo configInfo) {
        mConfigInfo = configInfo;
    }

    public ISpeedTestListener getTestListener() {
        return mTestListener;
    }

    public void setTestListener(ISpeedTestListener testListener) {
        mTestListener = testListener;
    }
}
