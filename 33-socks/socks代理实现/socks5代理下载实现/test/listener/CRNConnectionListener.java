package com.speed.vpnsocks.test.listener;

/**
 * crn开启代理是否成功
 */
public interface CRNConnectionListener {
    void connectionSuccess(int error,String result);
}
