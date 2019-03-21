package com.speed.vpnsocks.test.bean;

/**
 * Created by chengjie on 2019/1/4
 * Description:
 */
public class TestConfigInfo {
    private int uploadSizeMax = 3 * 1024 * 1024;//上传文件大小
    private int uploadTimeMillisecondMax = 5 * 1000;//上传最大时间
    private int downloadTimeMillisecondMax = 5 * 1000;//下载最大时间
    private int socketSendBufferSize = 1024 * 10;//默认值最低 10k
    private int socketReceiveBufferSize = 1024 * 10;
    private int localWriteReadBufferSize = 1024 * 10;

    public int getUploadSizeMax() {
        return uploadSizeMax;
    }

    public void setUploadSizeMax(int uploadSizeMax) {
        this.uploadSizeMax = uploadSizeMax;
    }

    public int getUploadTimeMillisecondMax() {
        return uploadTimeMillisecondMax;
    }

    public void setUploadTimeMillisecondMax(int uploadTimeMillisecondMax) {
        this.uploadTimeMillisecondMax = uploadTimeMillisecondMax;
    }

    public int getDownloadTimeMillisecondMax() {
        return downloadTimeMillisecondMax;
    }

    public void setDownloadTimeMillisecondMax(int downloadTimeMillisecondMax) {
        this.downloadTimeMillisecondMax = downloadTimeMillisecondMax;
    }

    public int getSocketSendBufferSize() {
        return socketSendBufferSize;
    }

    public void setSocketSendBufferSize(int socketSendBufferSize) {
        this.socketSendBufferSize = socketSendBufferSize;
    }

    public int getSocketReceiveBufferSize() {
        return socketReceiveBufferSize;
    }

    public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
        this.socketReceiveBufferSize = socketReceiveBufferSize;
    }

    public int getLocalWriteReadBufferSize() {
        return localWriteReadBufferSize;
    }

    public void setLocalWriteReadBufferSize(int localWriteReadBufferSize) {
        this.localWriteReadBufferSize = localWriteReadBufferSize;
    }
}
