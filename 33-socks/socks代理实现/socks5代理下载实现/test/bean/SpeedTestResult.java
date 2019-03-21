package com.speed.vpnsocks.test.bean;

/**
 * Created by chengjie on 2018/12/7
 * Description:
 */
public class SpeedTestResult {
    private int normalDown;
    private int proxyDown;
    private int normalUp;
    private int proxyUp;
    private int normalPing;
    private int proxyPing;

    public int getNormalDown() {
        return normalDown;
    }

    public void setNormalDown(int normalDown) {
        this.normalDown = normalDown;
    }

    public int getProxyDown() {
        return proxyDown;
    }

    public void setProxyDown(int proxyDown) {
        this.proxyDown = proxyDown;
    }

    public int getNormalUp() {
        return normalUp;
    }

    public void setNormalUp(int normalUp) {
        this.normalUp = normalUp;
    }

    public int getProxyUp() {
        return proxyUp;
    }

    public void setProxyUp(int proxyUp) {
        this.proxyUp = proxyUp;
    }

    public int getNormalPing() {
        return normalPing;
    }

    public void setNormalPing(int normalPing) {
        this.normalPing = normalPing;
    }

    public int getProxyPing() {
        return proxyPing;
    }

    public void setProxyPing(int proxyPing) {
        this.proxyPing = proxyPing;
    }
}
