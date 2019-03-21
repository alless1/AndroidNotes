package com.speed.vpnsocks.test.bean;

/**
 * Created by chengjie on 2019/1/11
 * Description:
 */
public class UsageFlowBean {
    public int uid;
    public long send;
    public long receive;
    public int upMax;
    public int downMax;

    public UsageFlowBean(int uid, long send, long receive) {
        this.uid = uid;
        this.send = send;
        this.receive = receive;
    }
}
